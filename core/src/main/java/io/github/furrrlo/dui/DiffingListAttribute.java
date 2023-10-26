package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class DiffingListAttribute<T, C, S extends DeclarativeComponentWithIdSupplier<? extends C>>
        implements DeclarativeComponentImpl.Attr<T, DiffingListAttribute<T, C, S>> {

    private static final Logger LOGGER = Logger.getLogger(DiffingListAttribute.class.getName());

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final String key;
    private final int updatePriority;
    private final DeclarativeComponentContext.ListAdder<T, C, S> adder;
    private final DeclarativeComponentContext.ListRemover<T> remover;
    private final Supplier<List<S>> valueSuppliersSupplier;
    private final Function<List<S>, List<StatefulDeclarativeComponent<?, C, ?, ?>>> valueFn;

    private List<S> suppliers;
    private List<StatefulDeclarativeComponent<?, C, ?, ?>> value;

    public DiffingListAttribute(String key,
                                int updatePriority,
                                DeclarativeComponentContext.ListAdder<T, C, S> adder,
                                DeclarativeComponentContext.ListRemover<T> remover,
                                Supplier<List<S>> suppliers,
                                Function<List<S>, List<StatefulDeclarativeComponent<?, C, ?, ?>>> value) {
        this.key = key;
        this.updatePriority = updatePriority;
        this.adder = adder;
        this.remover = remover;
        this.valueSuppliersSupplier = suppliers;
        this.suppliers = suppliers.get();
        this.valueFn = value;
        this.value = value.apply(this.suppliers);
    }

    @Override
    public int updatePriority() {
        return updatePriority;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(T obj, boolean wasSet, @Nullable DiffingListAttribute<T, C, S> prev, @Nullable Object prevValue0) {
        // prev might be this attribute itself, so we need to save the suppliers before we replace them
        final List<S> prevSuppliers = prev != null ? prev.suppliers : null;
        this.suppliers = valueSuppliersSupplier.get();
        this.value = valueFn.apply(this.suppliers);

        final List<StatefulDeclarativeComponent<?, C, ?, ?>> prevValue = wasSet ?
                (List<StatefulDeclarativeComponent<?, C, ?, ?>>) Objects.requireNonNull(prevValue0) :
                Collections.emptyList();
        final Map<StatefulDeclarativeComponent<?, C, ?, ?>, S> implToSuppliers = Stream
                .of(IntStream.range(0, suppliers.size())
                                .boxed()
                                .collect(Collectors.toMap(value::get, suppliers::get)),
                        prevSuppliers != null ?
                                IntStream.range(0, prevSuppliers.size())
                                        .boxed()
                                        .collect(Collectors.toMap(prevValue::get, prevSuppliers::get)) :
                                Collections.<StatefulDeclarativeComponent<?, C, ?, ?>, S>emptyMap())
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> {
                    // In case of memoized stuff, the values might be the same (so also the suppliers)
                    if(v1 == v2) return v1;
                    throw new UnsupportedOperationException("Same key for values " + v1 + " and " + v2);
                }));

        final List<ListDiff.OutputMove<StatefulDeclarativeComponent<?, C, ?, ?>>> outputMoves = ListDiff.diff(
                prevValue,
                value,
                StatefulDeclarativeComponent::getDeclarativeType,
                c -> Objects.requireNonNull(implToSuppliers.get(c),
                        "Missing supplier for impl " + c).getId(),
                new ArrayList<>());
        final List<StatefulDeclarativeComponent<?, C, ?, ?>> toUpdate = new ArrayList<>(prevValue);
        final Set<StatefulDeclarativeComponent<?, ?, ?, ?>> alreadyDeepUpdated = new LinkedHashSet<>();
        final Set<StatefulDeclarativeComponent<?, ?, ?, ?>> toDispose = new LinkedHashSet<>();

        final List<Runnable> actions = new ArrayList<>();
        final List<CompletableFuture<?>> componentsCreations = new ArrayList<>();

        outputMoves.forEach(move -> move.doMove(
                (idx, item) -> {
                    final S supplier = Objects.requireNonNull(implToSuppliers.get(item), "Missing supplier for impl " + item);
                    final StatefulDeclarativeComponent<?, C, ?, ?> prevItem = idx < prevValue.size() ? prevValue.get(idx) : null;
                    final boolean canSubstitutePrevItem = prevItem != null && Objects.equals(
                            item.getDeclarativeType(),
                            Objects.requireNonNull(prevItem).getDeclarativeType());

                    if (idx >= toUpdate.size())
                        toUpdate.add(item);
                    else
                        toUpdate.add(idx, item);

                    toDispose.remove(item);
                    if(canSubstitutePrevItem)
                        toDispose.remove(prevItem);
                    alreadyDeepUpdated.add(item);

                    CompletableFuture<Void> future = new CompletableFuture<>();
                    item.runOrScheduleOnFrameworkThread(() -> {
                        try {
                            if(canSubstitutePrevItem) {
                                item.substitute(prevItem);
                                item.updateComponent();
                                return;
                            }

                            item.updateOrCreateComponent();
                        } finally {
                            // Complete the future even if an exception is thrown here, the stacktrace will be printed
                            // in this thread and not on the one of the parent component (if it's not the same thread)
                            future.complete(null);
                        }
                    });
                    componentsCreations.add(future);
                    actions.add(() -> {
                        final C component = item.getComponent();
                        if (LOGGER.isLoggable(Level.FINE))
                            LOGGER.log(Level.FINE, "Inserting component {0} at idx {1} of {2}", new Object[]{component, idx, obj});
                        adder.add(obj, idx, supplier, component);
                    });
                },
                idx -> {
                    if (LOGGER.isLoggable(Level.FINE))
                        LOGGER.log(Level.FINE, "Removing component at idx {0} of {1}", new Object[]{idx, obj});
                    toDispose.add(toUpdate.remove(idx));
                    actions.add(() -> remover.remover(obj, idx));
                }));

        // This is shit but whatever I guess
        // It should allow components with a different framework thread to still execute actions in order
        @SuppressWarnings("unused")
        Void unused = CompletableFuture.allOf(componentsCreations.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    // TODO: do this on the thread of the component which owns this attribute
                    actions.forEach(Runnable::run);
                })
                // Get it now so in case stuff was run on this thread and an exception is thrown,
                // it would be rethrown in this thread and not swallowed by the CompletableFuture
                .getNow(null);

        // Deep updates
        for (int i = 0; i < toUpdate.size(); i++) {
            final StatefulDeclarativeComponent<?, C, ?, ?> currImpl = value.get(i);
            final StatefulDeclarativeComponent<?, C, ?, ?> prevImpl = toUpdate.get(i);
            if (alreadyDeepUpdated.contains(currImpl))
                continue;

            currImpl.runOrScheduleOnFrameworkThread(() -> {
                currImpl.substitute(prevImpl);
                currImpl.updateComponent();
            });
        }

        toDispose.forEach(c -> c.runOrScheduleOnFrameworkThread(c::disposeComponent));
    }

    @Override
    public void dispose() {
        value.forEach(c -> c.runOrScheduleOnFrameworkThread(c::disposeComponent));
    }
}
