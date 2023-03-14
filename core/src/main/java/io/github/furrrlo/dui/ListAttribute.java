package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class ListAttribute<T, C, S extends DeclarativeComponentWithIdSupplier<? extends C>>
        implements DeclarativeComponentImpl.Attr<T, ListAttribute<T, C, S>> {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final String key;
    private final DeclarativeComponentContext.ListSetter<T, C, S> setter;
    private final List<S> suppliers;
    private final List<StatefulDeclarativeComponent<?, C, ?, ?>> value;

    public ListAttribute(String key,
                         DeclarativeComponentContext.ListSetter<T, C, S> setter,
                         List<S> suppliers,
                         List<StatefulDeclarativeComponent<?, C, ?, ?>> value) {
        this.key = key;
        this.setter = setter;
        this.suppliers = suppliers;
        this.value = value;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(T obj, boolean wereSet, @Nullable ListAttribute<T, C, S> prev, @Nullable Object prevValues0) {
        final List<StatefulDeclarativeComponent<?, C, ?, ?>> prevValues = wereSet ?
                (List<StatefulDeclarativeComponent<?, C, ?, ?>>) Objects.requireNonNull(prevValues0) :
                Collections.emptyList();

        final List<C> children = new ArrayList<>();
        int idx;
        for (idx = 0; idx < value.size(); idx++) {
            final boolean wasSet = idx < prevValues.size();
            Attribute.updateDeclarativeComponent(
                    wasSet,
                    this.value.get(idx),
                    wasSet ? prevValues.get(idx) : null,
                    children::add,
                    children::add);
        }

        // These were all removed
        for (; idx < prevValues.size(); idx++) {
            final StatefulDeclarativeComponent<?, C, ?, ?> prevValue = prevValues.get(idx);
            if (prevValue != null)
                prevValue.disposeComponent();
        }

        setter.set(obj, suppliers, children);
    }
}
