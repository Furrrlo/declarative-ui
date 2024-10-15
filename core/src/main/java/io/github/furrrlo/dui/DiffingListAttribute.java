package io.github.furrrlo.dui;

import io.github.furrrlo.dui.DeclarativeRefComponentContext.ListAdder;
import io.github.furrrlo.dui.DeclarativeRefComponentContext.ListRemover;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class DiffingListAttribute<T, C, S extends DeclarativeComponentWithIdSupplier<? extends C>>
        implements DeclarativeComponentImpl.Attr<T, DiffingListAttribute<T, C, S>> {

    private static final Logger LOGGER = Logger.getLogger(DiffingListAttribute.class.getName());

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final String key;
    private final int updatePriority;
    private final ListAdder<T, C, S> adder;
    private final ListRemover<T> remover;
    private final Supplier<List<S>> valueSuppliersSupplier;
    private final Function<List<S>, List<ValueAndSupplier<C, S>>> valueFn;

    private List<ValueAndSupplier<C, S>> value;

    public DiffingListAttribute(String key,
                                int updatePriority,
                                ListAdder<T, C, S> adder,
                                ListRemover<T> remover,
                                Supplier<List<S>> suppliers,
                                Function<S, StatefulDeclarativeComponent<C, ?, ?>> value) {
        this.key = key;
        this.updatePriority = updatePriority;
        this.adder = adder;
        this.remover = remover;
        this.valueSuppliersSupplier = suppliers;
        this.valueFn = ss -> ss.stream()
                .map(s -> new ValueAndSupplier<>(s, value.apply(s)))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        this.value = valueFn.apply(suppliers.get());
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
    public void update(DeclarativeComponentImpl<T, ?> declarativeComponent,
                       T obj,
                       boolean checkDeps,
                       @Nullable DiffingListAttribute<T, C, S> prev) {
        // prev might be this attribute itself, so we need to save the values before we replace them
        final List<ValueAndSupplier<C, S>> prevValue = prev != null ? prev.value : Collections.emptyList();
        this.value = valueFn.apply(valueSuppliersSupplier.get());

        final List<ListDiff.OutputMove<ValueAndSupplier<C, S>>> outputMoves = ListDiff.diff(
                prevValue,
                value,
                c -> c.value.getDeclarativeType(),
                c -> c.supplier.getId(),
                new ArrayList<>());
        final List<StatefulDeclarativeComponent<C, ?, ?>> toUpdate = prevValue.stream()
                .map(v -> v.value)
                .collect(Collectors.toList());
        final Set<StatefulDeclarativeComponent<?, ?, ?>> alreadyDeepUpdated = new LinkedHashSet<>();
        final Set<StatefulDeclarativeComponent<?, ?, ?>> toDispose = new LinkedHashSet<>();

        final List<Runnable> actions = new ArrayList<>();
        final List<CompletableFuture<?>> componentsCreations = new ArrayList<>();

        outputMoves.forEach(move -> move.doMove(
                (idx, itemAndSupplier, prevItemAndSupplier) -> {
                    final StatefulDeclarativeComponent<C, ?,?> item = itemAndSupplier.value;
                    final S supplier = itemAndSupplier.supplier;

                    final StatefulDeclarativeComponent<C, ?, ?> prevItem = prevItemAndSupplier != null ? prevItemAndSupplier.value : null;
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

                            item.updateOrCreateComponent(declarativeComponent.getAppConfig(), declarativeComponent.lookups());
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
                            LOGGER.log(Level.FINE, "{0}: Inserting component at idx ({1}: {2}) of {3}", new Object[]{key, idx, component, obj});
                        adder.add(obj, idx, supplier, component);
                    });
                },
                idx -> {
                    if (LOGGER.isLoggable(Level.FINE))
                        LOGGER.log(Level.FINE, "{0}: Removing component at idx {1} of {2}", new Object[]{key, idx, obj});
                    toDispose.add(toUpdate.remove(idx));
                    actions.add(() -> remover.remover(obj, idx));
                }));

        // This is shit but whatever I guess
        // It should allow components with a different framework thread to still execute actions in order
        @SuppressWarnings({
                "unused", // Void result
                "rawtypes", // arrays sigh
                "RedundantSuppression" /* IntelliJ complains about rawtypes */})
        Void unused = CompletableFuture.allOf(componentsCreations.toArray(new CompletableFuture[0]))
                .thenRun(() -> declarativeComponent
                        .runOrScheduleOnFrameworkThread(() -> actions.forEach(Runnable::run)))
                // Get it now so in case stuff was run on this thread and an exception is thrown,
                // it would be rethrown in this thread and not swallowed by the CompletableFuture
                .getNow(null);

        // Deep updates
        for (int i = 0; i < toUpdate.size(); i++) {
            final StatefulDeclarativeComponent<C, ?, ?> currImpl = value.get(i).value;
            final StatefulDeclarativeComponent<C, ?, ?> prevImpl = toUpdate.get(i);
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
        value.forEach(c -> c.value.runOrScheduleOnFrameworkThread(c.value::disposeComponent));
    }

    private static class ValueAndSupplier<C, S extends DeclarativeComponentWithIdSupplier<? extends C>> {

        final S supplier;
        final StatefulDeclarativeComponent<C, ?, ?> value;

        public ValueAndSupplier(S supplier, StatefulDeclarativeComponent<C, ?, ?> value) {
            this.supplier = supplier;
            this.value = value;
        }
    }
}
