package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

class Identifiables {

    private Identifiables() {
    }

    public static Object[] computeDependencies(Serializable identifiable) {
        if(identifiable instanceof Identifiable.Explicit)
            return ((Identifiable) identifiable).deps();

        final List<Throwable> exs = new ArrayList<>();

        Object maybeSerializedLambda;
        try {
            final Method method = identifiable.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            maybeSerializedLambda = method.invoke(identifiable);
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

    public static boolean equals(@Nullable Object o1, @Nullable Object o2) {
        // This is to avoid potential infinite cycles with Objects.equals
        if(o1 != o2 && (o1 == null || o2 == null))
            return false;
        if(!(o1 instanceof Identifiable) || !(o2 instanceof Identifiable))
            return Objects.equals(o1, o2);

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
