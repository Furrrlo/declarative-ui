package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.IntStream;

class Identifiables {

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

    private Identifiables() {
    }

    public static Object[] computeDependencies(Collection<MethodHandles.Lookup> lookupsIn, Serializable identifiable) {
        if (identifiable instanceof Identifiable.Explicit)
            return ((Identifiable) identifiable).deps(lookupsIn);

        final Collection<MethodHandles.Lookup> lookups;
        if(lookupsIn.isEmpty()) {
            lookups = Collections.singleton(MethodHandles.lookup());
        } else {
            Set<MethodHandles.Lookup> newLookups = new LinkedHashSet<>(lookupsIn);
            newLookups.add(MethodHandles.lookup());
            lookups = Collections.unmodifiableSet(newLookups);
        }

        final List<Throwable> exs = new ArrayList<>();

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

        RuntimeException ex = new UnsupportedOperationException("Failed to extract dependencies");
        exs.forEach(ex::addSuppressed);
        throw ex;
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
                    if(dep instanceof IdentifiableRunnable)
                        return IdentifiableRunnable.explicit(lookups, (IdentifiableRunnable) dep);
                    if(dep instanceof IdentifiableThrowingRunnable)
                        return IdentifiableThrowingRunnable.explicit(lookups, (IdentifiableThrowingRunnable) dep);
                    if(dep instanceof IdentifiableSupplier)
                        return IdentifiableSupplier.explicit(lookups, (IdentifiableSupplier<?>) dep);
                    if(dep instanceof IdentifiableConsumer)
                        return IdentifiableConsumer.explicit(lookups, (IdentifiableConsumer<?>) dep);
                    if(dep instanceof IdentifiableThrowingConsumer)
                        return IdentifiableThrowingConsumer.explicit(lookups, (IdentifiableThrowingConsumer<?>) dep);
                    if(dep instanceof IdentifiableFunction)
                        return IdentifiableFunction.explicit(lookups, (IdentifiableFunction<?, ?>) dep);
                    if(dep instanceof IdentifiableBiFunction)
                        return IdentifiableBiFunction.explicit(lookups, (IdentifiableBiFunction<?, ?, ?>) dep);
                    return dep;
                })
                .toArray();
    }

    public static boolean equals(@Nullable Object o1, @Nullable Object o2) {
        // This is to avoid potential infinite cycles with Objects.equals
        if(o1 != o2 && (o1 == null || o2 == null))
            return false;
        if(!(o1 instanceof Identifiable) || !(o2 instanceof Identifiable))
            return o1 instanceof Identifiable ? Objects.equals(o2, o1) : Objects.equals(o1, o2);

        final Identifiable obj = (Identifiable) o1;
        final Identifiable that = (Identifiable) o2;
        if (obj == that)
            return true;
        if (obj.getImplClass() != that.getImplClass())
            return false;
        return Arrays.deepEquals(obj.deps(), that.deps());
    }

    public static int hashCode(@Nullable Identifiable o) {
        if(o == null)
            return 0;

        int result = Objects.hash(o.getImplClass());
        result = 31 * result + Arrays.hashCode(o.deps());
        return result;
    }
}
