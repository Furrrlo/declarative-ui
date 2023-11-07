package io.github.furrrlo.dui.gradle;

import com.squareup.javapoet.*;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import javax.swing.*;
import java.awt.*;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class JavaBeanWorkAction implements WorkAction<JavaBeanWorkAction.JavaBeanParams> {

    private static final String DUI_CORE_PKG = "io.github.furrrlo.dui";
    private static final String DUI_SWING_PKG = DUI_CORE_PKG + ".swing";

    public interface JavaBeanParams extends WorkParameters {

        MapProperty<String, String> getBeansToTargetPackages();

        DirectoryProperty getTargetDirectory();
    }

    @Inject
    public JavaBeanWorkAction() {
    }

    @Override
    public void execute() {
        try {
            execute0();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void execute0() throws Exception {
        final Map<String, String> beansToTargetPackages = getParameters().getBeansToTargetPackages().get();
        final Path targetDirectory = getParameters().getTargetDirectory().get().getAsFile().toPath();

        final Collection<String> packagesToCheck = beansToTargetPackages.keySet();
        System.out.println("Searching for Java Beans in packages " + packagesToCheck);

        final List<? extends Class<?>> candidates;
        try (ScanResult scanResult = new ClassGraph()
                .verbose()
                .enableClassInfo()
                .enableSystemJarsAndModules()
                .acceptPackages(packagesToCheck.toArray(String[]::new))
                .scan()) {
            final ClassInfoList subclasses = scanResult.getSubclasses(JComponent.class);
            System.out.println("Found " + subclasses.size() + " candidates");

            candidates = Stream.concat(
                            packagesToCheck.contains("javax.swing")
                                    ? Stream.of(JComponent.class)
                                    : Stream.empty(),
                            subclasses.stream().map(routeClassInfo -> {
                                try {
                                    return Class.forName(routeClassInfo.getName());
                                } catch (Exception ex) {
                                    throw new RuntimeException("Failed to resolve class " + routeClassInfo.getName());
                                }
                            }))
                    .toList();
        }

        System.out.println("Found " + candidates.size() + " candidate classes");
        for (Class<?> candidateClass : candidates) {
            final BeanInfo beanInfo = Introspector.getBeanInfo(candidateClass, candidateClass.getSuperclass());
            // TODO: what should I do with these?
            if(candidateClass.getDeclaringClass() != null)
                continue;

            final String matchedPackage = packagesToCheck.stream()
                    .filter(p -> candidateClass.getPackageName().startsWith(p))
                    .findFirst()
                    .orElseThrow();
            final String targetPackage = beansToTargetPackages.get(matchedPackage);
            final String subpackage = candidateClass.getPackageName().substring(matchedPackage.length());

            final ClassName targetClassName = ClassName.get(targetPackage + subpackage, getTargetClassFor(candidateClass));
            final ClassName superDecoratorName = !candidateClass.equals(JComponent.class)
                    ? ClassName.get(targetPackage, getTargetClassFor(candidateClass.getSuperclass())).nestedClass("Decorator")
                    : ClassName.get(DUI_SWING_PKG, "SwingDecorator");

            Path outputDirectory = targetDirectory;
            if (!targetClassName.packageName().isEmpty()) {
                for (String packageComponent : targetClassName.packageName().split("\\."))
                    outputDirectory = outputDirectory.resolve(packageComponent);
                Files.createDirectories(outputDirectory);
            }

            Path outputPath = outputDirectory.resolve(targetClassName.simpleName() + ".java");
            if(!Files.exists(outputPath)) {
                System.out.println("Generating declarative component for " + candidateClass.getName() + " in " + outputPath.toAbsolutePath());
                final JavaFile targetJavaFile = JavaFile
                        .builder(targetClassName.packageName(), getTargetClass(candidateClass, targetClassName, superDecoratorName, beanInfo))
                        .build();

                try (Writer writer = new OutputStreamWriter(Files.newOutputStream(outputPath), StandardCharsets.UTF_8)) {
                    targetJavaFile.writeTo(writer);
                }
                continue;
            }

            final String fileContent = Files.readString(outputPath);

            List<MethodSpec> methodSpecs = generateMethods(candidateClass, targetClassName, beanInfo);
            if(methodSpecs != null)
                methodSpecs = methodSpecs.stream()
                        .filter(generatedMethod -> !fileContent.contains('"' + generatedMethod.name + '"'))
                        .collect(Collectors.toList());
            if(methodSpecs != null && methodSpecs.isEmpty()) {
                System.out.println("Skipping declarative component for " + candidateClass.getName() + " as it was already generated");
                continue;
            }

            System.out.println("Generating methods for " + candidateClass.getName() + " in " + outputPath.toAbsolutePath());
            final int endDecoratorIdx = fileContent.lastIndexOf("}", fileContent.lastIndexOf("}") - 1);
            if(endDecoratorIdx < 0)
                throw new UnsupportedOperationException("Couldn't handle already written file " + outputPath.toAbsolutePath());

            try (Writer writer = new OutputStreamWriter(Files.newOutputStream(outputPath), StandardCharsets.UTF_8)) {
                writer.write(fileContent, 0, endDecoratorIdx);

                if(methodSpecs == null) {
                    writer.write("\n");
                    writer.write("// TODO: BeanInfo inspection broke down");
                    writer.write("\n");
                } else {
                    for (MethodSpec generatedMethod : methodSpecs) {
                        writer.write("\n");
                        writer.write(generatedMethod.toString());
                    }
                }

                writer.write(fileContent, endDecoratorIdx, fileContent.length() - endDecoratorIdx);
            }
        }
    }

    private TypeSpec getTargetClass(Class<?> candidateClass,
                                    ClassName targetClassName,
                                    ClassName superDecoratorName,
                                    BeanInfo beanInfo) {
        final ClassName decoratorClassName = targetClassName.nestedClass("Decorator");
        TypeSpec.Builder targetClass = TypeSpec.classBuilder(targetClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                        .addMember("value", "$S", "unused")
                        .build());

        if((candidateClass.getModifiers() & java.lang.reflect.Modifier.ABSTRACT) == 0)
            targetClass.addMethods(getTargetClassMethods(candidateClass, targetClassName, decoratorClassName));

        return targetClass
                .addType(getTargetDecoratorClass(candidateClass, targetClassName, decoratorClassName, superDecoratorName, beanInfo))
                .build();
    }

    private Iterable<MethodSpec> getTargetClassMethods(Class<?> candidateClass,
                                                       ClassName targetClassName,
                                                       ClassName decoratorClassName) {
        final ClassName candidateClassName = ClassName.get(candidateClass);
        return Arrays.asList(
                MethodSpec.methodBuilder("fn")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ParameterizedTypeName.get(
                                ClassName.get(DUI_CORE_PKG, "DeclarativeComponent"),
                                candidateClassName))
                        .addParameter(
                                ParameterizedTypeName.get(
                                        ClassName.get(DUI_CORE_PKG, "IdentifiableConsumer"),
                                        ParameterizedTypeName.get(decoratorClassName, candidateClassName)),
                                "body")
                        .addStatement("return fn($T.class, $T::new, body)", candidateClassName, candidateClassName)
                        .build(),
                MethodSpec.methodBuilder("fn")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ParameterizedTypeName.get(
                                ClassName.get(DUI_CORE_PKG, "DeclarativeComponent"),
                                candidateClassName))
                        .addParameter(
                                ParameterizedTypeName.get(ClassName.get(Supplier.class), candidateClassName),
                                "factory")
                        .addParameter(
                                ParameterizedTypeName.get(
                                        ClassName.get(DUI_CORE_PKG, "IdentifiableConsumer"),
                                        ParameterizedTypeName.get(decoratorClassName, candidateClassName)),
                                "body")
                        .addStatement("return fn($T.class, factory, body)", candidateClassName)
                        .build(),
                MethodSpec.methodBuilder("fn")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addTypeVariable(TypeVariableName.get("T", ClassName.get(candidateClass)))
                        .returns(ParameterizedTypeName.get(
                                ClassName.get(DUI_CORE_PKG, "DeclarativeComponent"),
                                TypeVariableName.get("T")))
                        .addParameter(
                                ParameterizedTypeName.get(ClassName.get(Class.class), TypeVariableName.get("T")),
                                "type")
                        .addParameter(
                                ParameterizedTypeName.get(ClassName.get(Supplier.class), TypeVariableName.get("T")),
                                "factory")
                        .addParameter(
                                ParameterizedTypeName.get(
                                        ClassName.get(DUI_CORE_PKG, "IdentifiableConsumer"),
                                        ParameterizedTypeName.get(decoratorClassName, TypeVariableName.get("T"))),
                                "body")
                        .addStatement("return $T.INSTANCE.of(() -> new $T<>(type, factory), body)",
                                ClassName.get(DUI_CORE_PKG, "DeclarativeComponentFactory"),
                                decoratorClassName)
                        .build()
        );
    }

    private TypeSpec getTargetDecoratorClass(Class<?> candidateClass,
                                             ClassName targetClassName,
                                             ClassName decoratorClassName,
                                             ClassName superDecoratorName,
                                             BeanInfo beanInfo) {
        final TypeVariableName genericType = TypeVariableName.get("T");
        final TypeSpec.Builder decoratorClass = TypeSpec.classBuilder(decoratorClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addTypeVariable(TypeVariableName.get(genericType.name, ClassName.get(candidateClass)))
                .superclass(ParameterizedTypeName.get(superDecoratorName, genericType))
                .addField(FieldSpec
                        .builder(String.class, "PREFIX", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "__" + targetClassName.simpleName() + "__")
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(
                                ParameterizedTypeName.get(ClassName.get(Class.class), genericType),
                                "type")
                        .addParameter(
                                ParameterizedTypeName.get(ClassName.get(Supplier.class), genericType),
                                "factory")
                        .addStatement("super(type, factory)")
                        .build());

        final List<MethodSpec> methods = generateMethods(candidateClass, targetClassName, beanInfo);
        if(methods == null) {
            decoratorClass.addStaticBlock(CodeBlock.builder()
                    .addStatement("// TODO: BeanInfo inspection broke down")
                    .build());
        } else {
            decoratorClass.addMethods(methods);
        }

        return decoratorClass.build();
    }

    private @Nullable List<MethodSpec> generateMethods(Class<?> candidateClass,
                                                       ClassName targetClassName,
                                                       BeanInfo beanInfo) {
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        if(propertyDescriptors == null)
            return null;

        final List<MethodSpec> methods = new ArrayList<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            final Class<?> type = propertyDescriptor.getPropertyType();
            final Method setter = propertyDescriptor.getWriteMethod();
            // We only care about non-deprecated stuff we can set
            if(setter == null || setter.getAnnotation(Deprecated.class) != null)
                continue;
            // Ignore overrides
            if(!candidateClass.equals(JComponent.class) && isOverridingMethod(setter))
                continue;

            String name = propertyDescriptor.getName();
            // If it's all uppercase, make it lowercase
            if(name.equals(name.toUpperCase(Locale.ROOT)))
                name = name.toLowerCase(Locale.ROOT);

            if(List.class.isAssignableFrom(type) || type.isArray())
                methods.add(generateListAttributeMethod(
                        name,
                        type,
                        propertyDescriptor.getReadMethod(),
                        setter));
            else if(Component.class.isAssignableFrom(type))
                methods.add(generateFnAttributeMethod(
                        name,
                        type,
                        propertyDescriptor.getReadMethod(),
                        setter));
            // Action is not an EventListener
            else if(EventListener.class.isAssignableFrom(type) && !Action.class.isAssignableFrom(type))
                methods.add(generateListenerAttribute(
                        targetClassName,
                        name,
                        type,
                        setter));
            else
                methods.add(generateAttributeMethod(
                        name,
                        type,
                        propertyDescriptor.getReadMethod(),
                        setter));
        }

        return methods;
    }

    private boolean isOverridingMethod(Method method) {
        Class<?> parent = method.getDeclaringClass().getSuperclass();
        while (parent != null && !parent.equals(Object.class)) {
            try {
                parent.getDeclaredMethod(method.getName(), method.getParameterTypes());
                return true;
            } catch (NoSuchMethodException e) {
                // Doesn't have it
            }

            if(parent.equals(JComponent.class))
                break;
            parent = parent.getSuperclass();
        }

        return false;
    }

    private MethodSpec generateAttributeMethod(String attrName,
                                               Class<?> type,
                                               @Nullable Method getter,
                                               Method setter) {
        MethodSpec.Builder method = MethodSpec.methodBuilder(attrName)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(
                        ParameterizedTypeName.get(
                                ClassName.get(Supplier.class),
                                type.isPrimitive() ?
                                        TypeName.get(type).box() :
                                        (type.getModifiers() & java.lang.reflect.Modifier.FINAL) != 0
                                                ? ClassName.get(type)
                                                : WildcardTypeName.subtypeOf(type)),
                        attrName);

        final StringBuilder statement = new StringBuilder();
        final List<Object> args = new ArrayList<>();

        statement.append("attribute(PREFIX + $S, ");
        args.add(attrName);

        if(getter != null) {
            statement.append("$T::$L, ");
            args.add(ClassName.get(getter.getDeclaringClass()));
            args.add(getter.getName());
        }

        statement.append("$T::$L, $L)");
        args.add(ClassName.get(setter.getDeclaringClass()));
        args.add(setter.getName());
        args.add(attrName);

        return method.addStatement(statement.toString(), args.toArray()).build();
    }

    private MethodSpec generateFnAttributeMethod(String attrName,
                                                 Class<?> type,
                                                 @Nullable Method getter,
                                                 Method setter) {
        MethodSpec.Builder method = MethodSpec.methodBuilder(attrName)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(
                        ParameterizedTypeName.get(
                                        ClassName.get(DUI_CORE_PKG, "DeclarativeComponentSupplier"),
                                        WildcardTypeName.subtypeOf(type))
                                .annotated(AnnotationSpec.builder(Nullable.class).build()),
                        attrName);

        final StringBuilder statement = new StringBuilder();
        final List<Object> args = new ArrayList<>();

        statement.append("fnAttribute(PREFIX + $S, ");
        args.add(attrName);

        if(getter != null) {
            statement.append("$T::$L, ");
            args.add(ClassName.get(getter.getDeclaringClass()));
            args.add(getter.getName());
        }

        statement.append("$T::$L, $L)");
        args.add(ClassName.get(setter.getDeclaringClass()));
        args.add(setter.getName());
        args.add(attrName);

        return method.addStatement(statement.toString(), args.toArray()).build();
    }

    private MethodSpec generateListenerAttribute(ClassName targetClassName,
                                                 String attrName,
                                                 Class<?> type,
                                                 Method setter) {
        return MethodSpec.methodBuilder(attrName)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(type), attrName)
                .addStatement("eventListener(PREFIX + $S, $T.class, $T::new, $T::$L, $L)",
                        attrName,
                        ClassName.get(type),
                        ClassName.get(targetClassName.packageName(), type.getSimpleName() + "Wrapper"),
                        ClassName.get(setter.getDeclaringClass()), setter.getName(),
                        attrName)
                .build();
    }

    private MethodSpec generateListAttributeMethod(String attrName,
                                                   Class<?> type,
                                                   Method getter,
                                                   Method setter) {
        return MethodSpec.methodBuilder(attrName)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addComment("TODO: implement $S", attrName)
                .build();
    }

    private String getTargetClassFor(Class<?> clazz) {
        final String candidateClassSimpleName = clazz.getSimpleName();
        return candidateClassSimpleName.startsWith("J")
                ? "JD" + candidateClassSimpleName.substring(1)
                : "D" + candidateClassSimpleName;
    }
}
