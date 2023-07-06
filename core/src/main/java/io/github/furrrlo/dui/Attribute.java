package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

class Attribute<T, V> implements DeclarativeComponentImpl.Attr<T, Attribute<T, V>> {

    private static final Logger LOGGER = Logger.getLogger(Attribute.class.getName());

    private final String key;
    private final BiConsumer<T, V> setter;
    private final Object value;

    public Attribute(String key, BiConsumer<T, V> setter, Object value) {
        this.key = key;
        this.setter = setter;
        this.value = value;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(T obj, boolean wasSet, @Nullable Attribute<T, V> prev, @Nullable Object prevValue) {
        if (value instanceof DeclarativeComponentSupplier) {
            updateAttribute(obj,
                    wasSet,
                    (StatefulDeclarativeComponent<?, V, ?, ?>) value,
                    (StatefulDeclarativeComponent<?, V, ?, ?>) prevValue);
            return;
        }

        updateAttribute(obj, wasSet, (V) value, (V) prevValue);
    }

    private void updateAttribute(T obj, boolean wasSet, V value, V prevValue) {
        if (!wasSet || !Objects.equals(value, prevValue)) {
            setter.accept(obj, value);
            if (LOGGER.isLoggable(Level.FINE))
                LOGGER.log(Level.FINE, "Updated attribute {}: {} -> {} of {}",
                        new Object[]{key, prevValue, value, obj});
        }
    }

    private void updateAttribute(T obj,
                                 boolean wasSet,
                                 StatefulDeclarativeComponent<?, V, ?, ?> value,
                                 @Nullable StatefulDeclarativeComponent<?, V, ?, ?> prevValue) {
        updateDeclarativeComponent(
                wasSet,
                value,
                prevValue,
                created -> setter.accept(obj, created),
                null);
    }

    static <V> void updateDeclarativeComponent(boolean wasSet,
                                               StatefulDeclarativeComponent<?, V, ?, ?> value,
                                               @Nullable StatefulDeclarativeComponent<?, V, ?, ?> prevValue,
                                               @Nullable Consumer<V> createdComponent,
                                               @Nullable Consumer<V> updatedComponent) {
        value.runOrScheduleOnFrameworkThread(() -> {
            // Wrappers need to invoke their body before they can say declarativeType
            if(value instanceof DeclarativeComponentWrapper) {
                if(wasSet)
                    value.copy(Objects.requireNonNull(prevValue));
                value.updateComponent(false);
            }

            if(wasSet && Objects.equals(value.getDeclarativeType(), Objects.requireNonNull(prevValue).getDeclarativeType())) {
                value.copy(prevValue);
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
}
