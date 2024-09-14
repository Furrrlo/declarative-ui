package io.github.furrrlo.dui;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class FnAttribute<T, V> implements DeclarativeComponentImpl.Attr<T, FnAttribute<T, V>> {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final String key;
    private final int updatePriority;
    private final BiConsumer<T, V> setter;
    private final DeclarativeComponentSupplier<? extends V> valueSupplier;
    private StatefulDeclarativeComponent<? extends V, ?, ?> value;

    public FnAttribute(String key,
                       int updatePriority,
                       BiConsumer<T, V> setter,
                       DeclarativeComponentSupplier<? extends V> valueSupplier) {
        this.key = key;
        this.updatePriority = updatePriority;
        this.setter = setter;
        this.valueSupplier = valueSupplier;
        this.value = valueSupplier.doApplyInternal();
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
                       boolean checkDeps,
                       boolean wasSet,
                       @Nullable FnAttribute<T, V> prev,
                       @Nullable Object prevValue) {
        value = valueSupplier.doApplyInternal();

        updateAttribute(
                declarativeComponent,
                obj,
                wasSet,
                value,
                (StatefulDeclarativeComponent<V, ?, ?>) prevValue);
    }

    private void updateAttribute(DeclarativeComponentImpl<T, ?> declarativeComponent,
                                 T obj,
                                 boolean wasSet,
                                 StatefulDeclarativeComponent<? extends V, ?, ?> value,
                                 @Nullable StatefulDeclarativeComponent<V, ?, ?> prevValue) {
        updateDeclarativeComponent(
                declarativeComponent.getAppConfig(),
                wasSet,
                value,
                prevValue,
                created -> setter.accept(obj, created),
                null);
    }

    static <V> void updateDeclarativeComponent(ApplicationConfig appConfig,
                                               boolean wasSet,
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

            V created = value.updateOrCreateComponent(appConfig);
            if(createdComponent != null)
                createdComponent.accept(created);

            if(prevValue != null)
                prevValue.disposeComponent();
        });
    }

    @Override
    public void dispose() {
        value.runOrScheduleOnFrameworkThread(value::disposeComponent);
    }
}
