package io.github.furrrlo.dui;

public interface Identifiable {

    Object[] deps();

    interface Explicit extends Identifiable {
    }
}
