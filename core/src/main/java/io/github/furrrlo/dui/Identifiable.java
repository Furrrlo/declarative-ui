package io.github.furrrlo.dui;

public interface Identifiable {

    Object[] deps();

    default Class<?> getImplClass() {
        return getClass();
    }

    interface Explicit extends Identifiable {
    }
}
