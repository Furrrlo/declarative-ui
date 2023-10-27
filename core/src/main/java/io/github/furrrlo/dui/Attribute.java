package io.github.furrrlo.dui;

import io.github.furrrlo.dui.DeclarativeRefComponentContext.AttributeEqualityFn;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

class Attribute<T, V> implements DeclarativeComponentImpl.Attr<T, Attribute<T, V>> {

    private static final Logger LOGGER = Logger.getLogger(Attribute.class.getName());

    private final String key;
    private final int updatePriority;
    private final BiConsumer<T, V> setter;
    private final Supplier<?> valueSupplier;
    private final AttributeEqualityFn<T, V> equalityFn;
    private Object value;

    public Attribute(String key, int updatePriority, BiConsumer<T, V> setter, Supplier<?> valueSupplier, AttributeEqualityFn<T, V> equalityFn) {
        this.key = key;
        this.updatePriority = updatePriority;
        this.setter = setter;
        this.valueSupplier = valueSupplier;
        this.equalityFn = equalityFn;
        this.value = valueSupplier.get();
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
    public void update(DeclarativeComponentImpl<T, ?> declarativeComponent,
                       T obj,
                       boolean wasSet,
                       @Nullable Attribute<T, V> prev,
                       @Nullable Object prevValue) {
        value = valueSupplier.get();

        if (value instanceof DeclarativeComponentSupplier) {
            updateAttribute(obj,
                    wasSet,
                    (StatefulDeclarativeComponent<V, ?, ?>) value,
                    (StatefulDeclarativeComponent<V, ?, ?>) prevValue);
            return;
        }

        updateAttribute(obj, wasSet, (V) value, (V) prevValue);
    }

    private void updateAttribute(T obj, boolean wasSet, V value, V prevValue) {
        if (!wasSet || !equalityFn.equals(obj, prevValue, value)) {
            if(obj == null)
                throw new NullPointerException(String.format(
                        "Attribute '%s' with old value '%s' and new value '%s'",
                        key, prevValue, value));

            setter.accept(obj, value);

            if (LOGGER.isLoggable(Level.FINE))
                LOGGER.log(Level.FINE, "Updated attribute {0}: {1} -> {2} of {3}",
                        new Object[]{key, prevValue, value, obj});
        }
    }

    private void updateAttribute(T obj,
                                 boolean wasSet,
                                 StatefulDeclarativeComponent<V, ?, ?> value,
                                 @Nullable StatefulDeclarativeComponent<V, ?, ?> prevValue) {
        updateDeclarativeComponent(
                wasSet,
                value,
                prevValue,
                created -> setter.accept(obj, created),
                null);
    }

    static <V> void updateDeclarativeComponent(boolean wasSet,
                                               StatefulDeclarativeComponent<? extends V, ?, ?> value,
                                               @Nullable StatefulDeclarativeComponent<V, ?, ?> prevValue,
                                               @Nullable Consumer<V> createdComponent,
                                               @Nullable Consumer<V> updatedComponent) {
        value.runOrScheduleOnFrameworkThread(() -> {
            if(wasSet && Objects.equals(value.getDeclarativeType(), Objects.requireNonNull(prevValue).getDeclarativeType())) {
                value.substitute(prevValue);
                value.updateComponent();

                if(updatedComponent != null)
                    updatedComponent.accept(value.getComponent());

                return;
            }

            V created = value.updateOrCreateComponent();
            if(createdComponent != null)
                createdComponent.accept(created);

            if(prevValue != null)
                prevValue.disposeComponent();
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void dispose() {
        if (!(value instanceof DeclarativeComponentSupplier))
            return;

        StatefulDeclarativeComponent<V, ?, ?> comp = (StatefulDeclarativeComponent<V, ?, ?>) value;
        comp.runOrScheduleOnFrameworkThread(comp::disposeComponent);
    }
}
