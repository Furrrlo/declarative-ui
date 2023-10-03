package io.github.furrrlo.dui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
}
