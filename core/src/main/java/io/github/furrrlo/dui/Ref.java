package io.github.furrrlo.dui;

public interface Ref<V> {

    V curr();

    void curr(V v);
}
