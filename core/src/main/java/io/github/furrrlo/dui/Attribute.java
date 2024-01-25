package io.github.furrrlo.dui;

import io.github.furrrlo.dui.DeclarativeRefComponentContext.AttributeEqualityFn;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

class Attribute<T, V> implements DeclarativeComponentImpl.Attr<T, Attribute<T, V>> {

    private static final Logger LOGGER = Logger.getLogger(Attribute.class.getName());

    private final String key;
    private final int updatePriority;
    private final BiConsumer<T, V> setter;
    private final Supplier<? extends V> valueSupplier;
    private final AttributeEqualityFn<T, V> equalityFn;
    private V value;

    public Attribute(String key,
                     int updatePriority,
                     BiConsumer<T, V> setter,
                     Supplier<? extends V> valueSupplier,
                     AttributeEqualityFn<T, V> equalityFn) {
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
        updateAttribute(obj, wasSet, value, (V) prevValue);
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

    @Override
    public void dispose() {
    }
}
