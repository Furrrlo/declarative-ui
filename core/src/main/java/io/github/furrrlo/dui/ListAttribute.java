package io.github.furrrlo.dui;

import io.github.furrrlo.dui.DeclarativeRefComponentContext.ListSetter;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

class ListAttribute<T, C, S extends DeclarativeComponentWithIdSupplier<? extends C>>
        implements DeclarativeComponentImpl.Attr<T, ListAttribute<T, C, S>> {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final String key;
    private final int updatePriority;
    private final ListSetter<T, C, S> setter;
    private final Supplier<List<S>> valueSuppliersSupplier;
    private final Function<List<S>, List<StatefulDeclarativeComponent<C, ?, ?>>> valueFn;

    private List<S> suppliers;
    private List<StatefulDeclarativeComponent<C, ?, ?>> value;

    public ListAttribute(String key,
                         int updatePriority,
                         ListSetter<T, C, S> setter,
                         Supplier<List<S>> suppliers,
                         Function<List<S>, List<StatefulDeclarativeComponent<C, ?, ?>>> value) {
        this.key = key;
        this.updatePriority = updatePriority;
        this.setter = setter;
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
    public void update(DeclarativeComponentImpl<T, ?> declarativeComponent,
                       T obj,
                       boolean checkDeps,
                       @Nullable ListAttribute<T, C, S> prev) {
        // prev might be this attribute itself, so we need to save the values before we replace them
        final List<StatefulDeclarativeComponent<C, ?, ?>> prevValues = prev != null ? prev.value : Collections.emptyList();
        this.suppliers = valueSuppliersSupplier.get();
        this.value = valueFn.apply(this.suppliers);

        final List<C> children = new ArrayList<>();
        int idx;
        for (idx = 0; idx < value.size(); idx++) {
            final boolean wasSet = idx < prevValues.size();
            FnAttribute.updateDeclarativeComponent(
                    declarativeComponent.getAppConfig(),
                    declarativeComponent.lookups(),
                    wasSet,
                    this.value.get(idx),
                    wasSet ? prevValues.get(idx) : null,
                    children::add,
                    children::add);
        }

        // These were all removed
        for (; idx < prevValues.size(); idx++) {
            final StatefulDeclarativeComponent<? extends C, ?, ?> prevValue = prevValues.get(idx);
            if (prevValue != null)
                prevValue.runOrScheduleOnFrameworkThread(prevValue::disposeComponent);
        }

        setter.set(obj, suppliers, children);
    }

    @Override
    public void dispose() {
        value.forEach(c -> c.runOrScheduleOnFrameworkThread(c::disposeComponent));
    }
}
