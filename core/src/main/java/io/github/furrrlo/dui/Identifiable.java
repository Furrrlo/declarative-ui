package io.github.furrrlo.dui;

import java.lang.invoke.MethodHandles;
import java.util.Collection;

public interface Identifiable {

    default Object[] deps() {
        return deps(StatefulDeclarativeComponent.currentLookups());
    }

    Object[] deps(Collection<MethodHandles.Lookup> lookup);

    default Class<?> getImplClass() {
        return getClass();
    }

    interface Explicit extends Identifiable {
    }
}
