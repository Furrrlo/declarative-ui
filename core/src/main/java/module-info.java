// The automatic module is geantyref, which uses the same module name,
// in the next version which is properly modularized, however we can't use that
// because it's Java9+. For some reason, both flags are needed to suppress it
@SuppressWarnings({ "requires-transitive-automatic", "requires-automatic" })
module io.github.furrrlo.dui {
    requires java.logging;
    requires transitive io.leangen.geantyref;

    requires static transitive org.jspecify;
    requires static transitive org.jetbrains.annotations;
    requires static transitive org.checkerframework.checker.qual;

    exports io.github.furrrlo.dui;
}