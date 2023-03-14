package io.github.furrrlo.dui;

public class DNull {

    public static <T> DeclarativeComponent<T> nullFn() {
        return DeclarativeComponentFactory.INSTANCE.of(Decorator::new);
    }

    public static <T> DeclarativeComponent<T> fn(@SuppressWarnings("unused") Class<T> clazz) {
        return DeclarativeComponentFactory.INSTANCE.of(Decorator::new);
    }

    private static class Decorator<T> extends DeclarativeComponentContextDecorator<T> {

        protected Decorator() {
            super(null, () -> null);
        }
    }
}
