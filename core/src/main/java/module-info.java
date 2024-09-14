module io.github.furrrlo.dui {
    requires java.logging;
    requires transitive io.leangen.geantyref;

    requires static transitive org.jspecify;
    requires static transitive org.jetbrains.annotations;
    requires static transitive org.checkerframework.checker.qual;

    exports io.github.furrrlo.dui;
}