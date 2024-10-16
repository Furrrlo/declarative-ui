package io.github.furrrlo.dui;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListDiffTest {

    static Stream<Arguments> testReorderSource() {
        return Stream.<@Nullable String>of(
                "type",
                null
        ).flatMap(type -> Stream.of(
                testReorderArgs("only keys (" + type + ')',
                        t -> type,
                        Item::id,
                        Arrays.asList(new Item("a"), new Item("b"), new Item("c"), new Item("d"), new Item("e")),
                        Arrays.asList(new Item("c"), new Item("a"), new Item("b"), new Item("e"), new Item("f"))),
                testReorderArgs("no keys (" + type + ')',
                        t -> type,
                        i -> null,
                        Arrays.asList(new Item("a"), new Item("b"), new Item("c"), new Item("d"), new Item("e")),
                        Arrays.asList(new Item("c"), new Item("a"), new Item("b"), new Item("e"), new Item("f"))),
                testReorderArgs("key moved afterward (" + type + ')',
                        t -> type,
                        obj -> (obj instanceof Item) ? ((Item) obj).id() : null,
                        Arrays.asList(new NoKey("a"), new NoKey("b"), new Item("c"), new NoKey("d"), new NoKey("e")),
                        Arrays.asList(new Item("c"), new NoKey("a"), new NoKey("b"), new NoKey("e"), new NoKey("f"))),
                testReorderArgs("removed item in the middle (" + type + ')',
                        t -> type,
                        obj -> (obj instanceof Item) ? ((Item) obj).id() : null,
                        Arrays.asList(new Item("a"), new NoKey("b"), new Item("c")),
                        Arrays.asList(new Item("a"), new Item("c"))),
                testReorderArgs("removed 2 items in the middle (" + type + ')',
                        t -> type,
                        obj -> (obj instanceof Item) ? ((Item) obj).id() : null,
                        Arrays.asList(new Item("a"), new NoKey("b"), new NoKey("c"), new Item("d")),
                        Arrays.asList(new Item("a"), new Item("d"))),
                testReorderArgs("swapped keyed items (" + type + ')',
                        t -> type,
                        Item::id,
                        Arrays.asList(new Item("a"), new Item("b"), new Item("c")),
                        Arrays.asList(new Item("a"), new Item("c"), new Item("b")))
//                testReorderArgs("Move free (" + type + ')',
//                        t -> type,
//                        obj -> (obj instanceof Item) ? ((Item) obj).id() : null,
//                        Arrays.asList(new Item("a"), new Item("b"), new NoKey("c"), new NoKey("d"), new Item("e")),
//                        Arrays.asList(new NoKey("d"), new Item("a"), new Item("b"), new NoKey("c"), new Item("e"))),
//                testReorderArgs("Move free (with other free) (" + type + ')',
//                        t -> type,
//                        obj -> (obj instanceof Item) ? ((Item) obj).id() : null,
//                        Arrays.asList(new Item("a"), new Item("b"), new NoKey("c"), new Item("d"), new Item("e")),
//                        Arrays.asList(new NoKey("c"), new Item("a"), new Item("b"), new Item("d"), new Item("e")))
        ));
    }

    static <T> Arguments testReorderArgs(String msg,
                                         Function<? super T, @Nullable String> typeFn,
                                         Function<? super T, @Nullable String> keyFn,
                                         List<T> oldL,
                                         List<T> newList) {
        return Arguments.of(msg, typeFn, keyFn, oldL, newList);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testReorderSource")
    <T> void testReorder(String msg,
                         Function<? super T, @Nullable String> typeFn,
                         Function<? super T, @Nullable String> keyFn,
                         List<T> oldL,
                         List<T> newList) {
        System.out.println(">> " + msg);

        final List<T> oldList = new ArrayList<>(oldL);
        final List<ListDiff.OutputMove<T>> outputMoves = ListDiff.diff(oldList, newList, typeFn, keyFn, new ArrayList<>());
        outputMoves.forEach(m -> m.doMove(
                (i, v, oldV) -> {
                    System.out.println("    Inserting " + v + " at " + i + " to " + oldList);
                    if(oldList.contains(v))
                        throw new UnsupportedOperationException("Item " + v + " was already contained: " + oldList);
                    oldList.add(i, v);
                },
                (i) -> {
                    System.out.println("    Removing " + i + " from " + oldList);
                    oldList.remove(i);
                }
        ));

        assertEquals(
                newList.stream().map(keyFn).collect(Collectors.toList()),
                oldList.stream().map(keyFn).collect(Collectors.toList()),
                msg);
    }

    @Test
    void testSingleKeyChangeNoMoves() {
        System.out.println(">> single key changed");

        final List<Item> oldList = new ArrayList<>(Arrays.asList(new Item("a"), new Item("b"), new Item("c")));
        final List<Item> newList = new ArrayList<>(Arrays.asList(new Item("a"), new Item("d"), new Item("c")));

        final List<ListDiff.OutputMove<Item>> outputMoves = ListDiff.diff(oldList, newList, t -> "type", Item::id, new ArrayList<>());
        outputMoves.forEach(m -> m.doMove(
                (i, v, oldV) -> {
                    System.out.println("    Inserting " + v + " at " + i + " to " + oldList);
                    if(oldList.contains(v))
                        throw new UnsupportedOperationException("Item " + v + " was already contained: " + oldList);
                    oldList.add(i, v);
                },
                (i) -> {
                    System.out.println("    Removing " + i + " from " + oldList);
                    oldList.remove(i);
                }
        ));

        assertTrue(
                outputMoves.isEmpty(),
                "When a single key of an item changes, it makes no sense to remove the old item and add a new " +
                        "one of the same type, we can just reuse the previous one and change its key");
    }

    private static class Item {

        private final String id;

        public Item(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Item)) return false;
            Item item = (Item) o;
            return id.equals(item.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "Item{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }

    private static class NoKey {

        private final String name;

        public NoKey(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NoKey)) return false;
            NoKey noKey = (NoKey) o;
            return name.equals(noKey.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "NoKey{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}