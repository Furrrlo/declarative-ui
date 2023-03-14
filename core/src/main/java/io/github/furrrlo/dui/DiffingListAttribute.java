package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.*;
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
    private final DeclarativeComponentContext.ListAdder<T, C, S> adder;
    private final DeclarativeComponentContext.ListRemover<T> remover;
    private final List<S> suppliers;
    private final List<StatefulDeclarativeComponent<?, C, ?, ?>> value;

    public DiffingListAttribute(String key,
                                DeclarativeComponentContext.ListAdder<T, C, S> adder,
                                DeclarativeComponentContext.ListRemover<T> remover,
                                List<S> suppliers,
                                List<StatefulDeclarativeComponent<?, C, ?, ?>> value) {
        this.key = key;
        this.adder = adder;
        this.remover = remover;
        this.suppliers = suppliers;
        this.value = value;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(T obj, boolean wasSet, @Nullable DiffingListAttribute<T, C, S> prev, @Nullable Object prevValue0) {
        final List<StatefulDeclarativeComponent<?, C, ?, ?>> prevValue = wasSet ?
                (List<StatefulDeclarativeComponent<?, C, ?, ?>>) Objects.requireNonNull(prevValue0) :
                Collections.emptyList();
        final Map<StatefulDeclarativeComponent<?, C, ?, ?>, S> implToSuppliers = Stream
                .of(IntStream.range(0, suppliers.size())
                                .boxed()
                                .collect(Collectors.toMap(value::get, suppliers::get)),
                        prev != null ?
                                IntStream.range(0, prev.suppliers.size())
                                        .boxed()
                                        .collect(Collectors.toMap(prevValue::get, prev.suppliers::get)) :
                                Collections.<StatefulDeclarativeComponent<?, C, ?, ?>, S>emptyMap())
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Wrappers need to invoke their body before they can say declarativeType
        for (int i = 0; i < value.size(); i++) {
            final StatefulDeclarativeComponent<?, C, ?, ?> currImpl = value.get(i);
            if (!(currImpl instanceof DeclarativeComponentWrapper))
                continue;

            final StatefulDeclarativeComponent<?, C, ?, ?> prevImpl = i < prevValue.size() ? prevValue.get(i) : null;
            if (prevImpl != null)
                currImpl.copy(prevImpl);
            currImpl.updateComponent(false);
        }

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
        outputMoves.forEach(move -> move.doMove(
                (idx, item) -> {
                    final S supplier = Objects.requireNonNull(implToSuppliers.get(item),
                            "Missing supplier for impl " + item);
                    if (idx >= toUpdate.size())
                        toUpdate.add(item);
                    else
                        toUpdate.add(idx, item);

                    toDispose.remove(item);
                    alreadyDeepUpdated.add(item);

                    final C component = item.updateOrCreateComponent();
                    if (LOGGER.isLoggable(Level.FINE))
                        LOGGER.log(Level.FINE, "Inserting component {} at idx {} of {}", new Object[]{component, idx, obj});
                    adder.add(obj, idx, supplier, component);
                },
                idx -> {
                    if (LOGGER.isLoggable(Level.FINE))
                        LOGGER.log(Level.FINE, "Removing component at idx {} of {}", new Object[]{idx, obj});
                    toDispose.add(toUpdate.remove(idx));
                    remover.remover(obj, idx);
                }));

        // Deep updates
        for (int i = 0; i < toUpdate.size(); i++) {
            final StatefulDeclarativeComponent<?, C, ?, ?> currImpl = value.get(i);
            final StatefulDeclarativeComponent<?, C, ?, ?> prevImpl = toUpdate.get(i);
            if (alreadyDeepUpdated.contains(currImpl))
                continue;

            currImpl.copy(prevImpl);
            currImpl.updateComponent();
        }

        toDispose.forEach(StatefulDeclarativeComponent::disposeComponent);
    }
}
