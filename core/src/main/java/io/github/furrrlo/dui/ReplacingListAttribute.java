package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

class ReplacingListAttribute<T, C, S extends DeclarativeComponentWithIdSupplier<? extends C>>
        implements DeclarativeComponentImpl.Attr<T, ReplacingListAttribute<T, C, S>> {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final String key;
    private final DeclarativeComponentContext.ListReplacer<T, C, S> replacer;
    private final List<S> suppliers;
    private final List<StatefulDeclarativeComponent<?, C, ?, ?>> value;

    public ReplacingListAttribute(String key,
                                  DeclarativeComponentContext.ListReplacer<T, C, S> replacer,
                                  List<S> suppliers,
                                  List<StatefulDeclarativeComponent<?, C, ?, ?>> value) {
        this.key = key;
        this.replacer = replacer;
        this.suppliers = suppliers;
        this.value = value;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(T obj, boolean wereSet, @Nullable ReplacingListAttribute<T, C, S> prev, @Nullable Object prevValues0) {
        final List<StatefulDeclarativeComponent<?, C, ?, ?>> prevValues = wereSet ?
                (List<StatefulDeclarativeComponent<?, C, ?, ?>>) Objects.requireNonNull(prevValues0) :
                Collections.emptyList();

        int idx;
        for (idx = 0; idx < value.size(); idx++) {
            final int idx0 = idx;
            final boolean wasSet = idx < prevValues.size();
            Attribute.updateDeclarativeComponent(
                    wasSet,
                    this.value.get(idx),
                    wasSet ? prevValues.get(idx) : null,
                    created -> replacer.replace(obj, idx0, suppliers.get(idx0), created),
                    null);
        }

        // These were all removed
        for (; idx < prevValues.size(); idx++) {
            final StatefulDeclarativeComponent<?, C, ?, ?> prevValue = prevValues.get(idx);
            // TODO: what to do with these?
//                replacer.replace(obj, idx, null, null);
            if (prevValue != null)
                prevValue.disposeComponent();
        }
    }
}
