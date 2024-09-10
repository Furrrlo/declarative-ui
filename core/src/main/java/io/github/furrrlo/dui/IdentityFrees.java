package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.IntStream;

class IdentityFrees {

    private static final @Nullable MethodHandle LOOKUP_PRIVATE_LOOKUP_IN;
    static {
        MethodHandle lookupPrivateLookupIn = null;

        try {
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            lookupPrivateLookupIn = lookup.unreflect(MethodHandles.class.getMethod(
                    "privateLookupIn", Class.class, MethodHandles.Lookup.class));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            // Couldn't access stuff
        }

        LOOKUP_PRIVATE_LOOKUP_IN = lookupPrivateLookupIn;
    }

    private IdentityFrees() {
    }

    public static Object[] computeDependencies(Collection<MethodHandles.Lookup> lookupsIn, Object identifiable) {
        if (identifiable instanceof IdentityFree.Explicit)
            return ((IdentityFree) identifiable).deps(lookupsIn);

        final Collection<MethodHandles.Lookup> lookups;
        if(lookupsIn.isEmpty()) {
            lookups = Collections.singleton(MethodHandles.lookup());
        } else {
            Set<MethodHandles.Lookup> newLookups = new LinkedHashSet<>(lookupsIn);
            newLookups.add(MethodHandles.lookup());
            lookups = Collections.unmodifiableSet(newLookups);
        }

        final List<Throwable> exs = new ArrayList<>();
        Object[] res;

        if (identifiable instanceof Serializable
                && (res = computeSerializableDependencies(lookups, (Serializable) identifiable, exs)) != null)
            return res;

        if((res = computeUnserializableLambdaDependencies(lookups, identifiable, exs)) != null)
            return res;

        RuntimeException ex = new UnsupportedOperationException("Failed to extract dependencies");
        exs.forEach(ex::addSuppressed);
        throw ex;
    }

    private static Object @Nullable [] computeSerializableDependencies(Collection<MethodHandles.Lookup> lookups,
                                                                       Serializable identifiable,
                                                                       List<Throwable> exs) {
        final Class<?> claz = identifiable.getClass();
        MethodHandles.@Nullable Lookup clazLookup;
        try {
            clazLookup = selectLookupFor(lookups, claz);
        } catch (IllegalAccessException e) {
            exs.add(new Exception(
                    "Failed to find Lookup which can access lambda. Did you grant access with a valid Lookup?", e));
            return null;
        }

        Object maybeSerializedLambda;
        try {
            final Method method = claz.getDeclaredMethod("writeReplace");
            if(clazLookup != null) {
                maybeSerializedLambda = clazLookup.unreflect(method).invoke(identifiable);
            } else {
                method.setAccessible(true);
                maybeSerializedLambda = method.invoke(identifiable);
            }
        } catch (Throwable e) {
            exs.add(new Exception("Failed to extract SerializedLambda from " + identifiable, e));
            maybeSerializedLambda = null;
        }

        if(maybeSerializedLambda instanceof SerializedLambda) {
            final SerializedLambda serializedLambda = (SerializedLambda) maybeSerializedLambda;
            return IntStream.range(0, serializedLambda.getCapturedArgCount())
                    .mapToObj(serializedLambda::getCapturedArg)
                    .toArray();
        }

        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try(ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(identifiable);
                oos.flush();
            }

            return new Object[] { baos.toByteArray() };
        } catch (IOException e) {
            exs.add(new Exception("Failed to serialize object " + identifiable, e));
        }

        return null;
    }

    private static boolean isLambda(Object lambda) {
        final Class<?> claz = lambda.getClass();
        // Hotspot specific, see:
        // - https://github.com/openjdk/jdk/blob/88cccc14db168876a60b5ea2ae9d0fda7969af9a/src/java.base/share/classes/java/lang/invoke/InnerClassLambdaMetafactory.java#L194C42-L194C50
        // - https://github.com/openjdk/jdk/blob/88cccc14db168876a60b5ea2ae9d0fda7969af9a/src/java.base/share/classes/java/lang/invoke/InnerClassLambdaMetafactory.java#L309
        return claz.isSynthetic() && !claz.isAnonymousClass() && claz.getName().contains("$$Lambda");
    }

    private static Object @Nullable [] computeUnserializableLambdaDependencies(Collection<MethodHandles.Lookup> lookups,
                                                                               Object lambda,
                                                                               List<Throwable> exs) {
        if(!isLambda(lambda))
            return null;

        final Class<?> claz = lambda.getClass();
        MethodHandles.@Nullable Lookup clazLookup;
        try {
            clazLookup = selectLookupFor(lookups, claz);
        } catch (IllegalAccessException e) {
            exs.add(new Exception(
                    "Failed to find Lookup which can access lambda. Did you grant access with a valid Lookup?", e));
            return null;
        }

        try {
            List<Object> list = new ArrayList<>();
            for (Field f : claz.getDeclaredFields()) {
                if(clazLookup != null) {
                    list.add(clazLookup.unreflectGetter(f).invoke(lambda));
                    continue;
                }

                f.setAccessible(true);
                list.add(f.get(lambda));
            }
            return list.toArray();
        } catch (Throwable e) {
            exs.add(new Exception("Failed to access fields for lambda " + lambda, e));
            return null;
        }
    }

    private static MethodHandles.@Nullable Lookup selectLookupFor(
            Collection<MethodHandles.Lookup> lookups, Class<?> targetClaz) throws IllegalAccessException {

        if(LOOKUP_PRIVATE_LOOKUP_IN == null)
            return null;

        List<IllegalAccessException> exs = new ArrayList<>();
        for (MethodHandles.Lookup lookup : lookups) {
            try {
                return (MethodHandles.Lookup) LOOKUP_PRIVATE_LOOKUP_IN.invokeExact(targetClaz, lookup);
            } catch (RuntimeException | Error ex) {
                throw ex;
            } catch (IllegalAccessException ex) {
                exs.add(ex);
                // continue;
            }  catch (Throwable t) {
                throw new AssertionError(
                        "MethodHandles.privateLookupIn(...) unexpectedly failed with checked exception", t);
            }
        }

        if(exs.size() == 1)
            throw exs.get(0);

        IllegalAccessException ex = new IllegalAccessException();
        exs.forEach(ex::addSuppressed);
        throw ex;
    }

    public static Object[] makeDependenciesExplicit(Collection<MethodHandles.Lookup> lookups, Object[] deps) {
        return Arrays.stream(deps)
                .map(dep -> {
                    if(dep instanceof IdentityFreeRunnable)
                        return IdentityFreeRunnable.explicit(lookups, (IdentityFreeRunnable) dep);
                    if(dep instanceof IdentityFreeThrowingRunnable)
                        return IdentityFreeThrowingRunnable.explicit(lookups, (IdentityFreeThrowingRunnable) dep);
                    if(dep instanceof IdentityFreeSupplier)
                        return IdentityFreeSupplier.explicit(lookups, (IdentityFreeSupplier<?>) dep);
                    if(dep instanceof IdentityFreeConsumer)
                        return IdentityFreeConsumer.explicit(lookups, (IdentityFreeConsumer<?>) dep);
                    if(dep instanceof IdentityFreeThrowingConsumer)
                        return IdentityFreeThrowingConsumer.explicit(lookups, (IdentityFreeThrowingConsumer<?>) dep);
                    if(dep instanceof IdentityFreeFunction)
                        return IdentityFreeFunction.explicit(lookups, (IdentityFreeFunction<?, ?>) dep);
                    if(dep instanceof IdentityFreeBiFunction)
                        return IdentityFreeBiFunction.explicit(lookups, (IdentityFreeBiFunction<?, ?, ?>) dep);
                    if(isLambda(dep))
                        return new ExplicitIdentityFreeLambda(lookups, dep, computeDependencies(lookups, dep));
                    return dep;
                })
                .toArray();
    }

    private static class ExplicitIdentityFreeLambda implements IdentityFree, IdentityFree.Explicit {

        private final Object lambda;
        private final IdentityFreeDeps deps;

        ExplicitIdentityFreeLambda(Collection<MethodHandles.Lookup> lookups, Object lambda, Object[] deps) {
            this.lambda = lambda;
            this.deps = IdentityFreeDeps.immediatelyExplicit(lookups, deps);
        }

        @Override
        public Object[] deps(Collection<MethodHandles.Lookup> lookups) {
            return deps.get(lookups);
        }

        @Override
        public Class<?> getImplClass() {
            return lambda.getClass();
        }

        @Override
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        public boolean equals(Object o) {
            return IdentityFrees.equals(this, o);
        }

        @Override
        public int hashCode() {
            return IdentityFrees.hashCode(this);
        }
    }

    public static boolean equals(@Nullable Object o1, @Nullable Object o2) {
        // This is to avoid potential infinite cycles with Objects.equals
        if(o1 != o2 && (o1 == null || o2 == null))
            return false;
        if(!(o1 instanceof IdentityFree) || !(o2 instanceof IdentityFree))
            return o1 instanceof IdentityFree ? Objects.equals(o2, o1) : Objects.equals(o1, o2);

        final IdentityFree obj = (IdentityFree) o1;
        final IdentityFree that = (IdentityFree) o2;
        if (obj == that)
            return true;
        if (obj.getImplClass() != that.getImplClass())
            return false;
        return Arrays.deepEquals(obj.deps(), that.deps());
    }

    public static int hashCode(@Nullable IdentityFree o) {
        if(o == null)
            return 0;

        int result = Objects.hash(o.getImplClass());
        result = 31 * result + Arrays.hashCode(o.deps());
        return result;
    }
}
