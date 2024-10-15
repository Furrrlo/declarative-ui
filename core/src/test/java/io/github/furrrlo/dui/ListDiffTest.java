package io.github.furrrlo.dui;

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

class ListDiffTest {

    static Stream<Arguments> testReorderSource() {
        return Stream.of(
                testReorderArgs("only keys",
                        Item::id,
                        Arrays.asList(new Item("a"), new Item("b"), new Item("c"), new Item("d"), new Item("e")),
                        Arrays.asList(new Item("c"), new Item("a"), new Item("b"), new Item("e"), new Item("f"))),
                testReorderArgs("no keys",
                        i -> null,
                        Arrays.asList(new Item("a"), new Item("b"), new Item("c"), new Item("d"), new Item("e")),
                        Arrays.asList(new Item("c"), new Item("a"), new Item("b"), new Item("e"), new Item("f"))),
                testReorderArgs("key moved afterward",
                        obj -> (obj instanceof Item) ? ((Item) obj).id() : null,
                        Arrays.asList(new NoKey("a"), new NoKey("b"), new Item("c"), new NoKey("d"), new NoKey("e")),
                        Arrays.asList(new Item("c"), new NoKey("a"), new NoKey("b"), new NoKey("e"), new NoKey("f"))),
                testReorderArgs("removed item in the middle",
                        obj -> (obj instanceof Item) ? ((Item) obj).id() : null,
                        Arrays.asList(new Item("a"), new NoKey("b"), new Item("c")),
                        Arrays.asList(new Item("a"), new Item("c"))),
                testReorderArgs("removed 2 items in the middle",
                        obj -> (obj instanceof Item) ? ((Item) obj).id() : null,
                        Arrays.asList(new Item("a"), new NoKey("b"), new NoKey("c"), new Item("d")),
                        Arrays.asList(new Item("a"), new Item("d"))),
                testReorderArgs("swapped keyed items",
                        Item::id,
                        Arrays.asList(new Item("a"), new Item("b"), new Item("c")),
                        Arrays.asList(new Item("a"), new Item("c"), new Item("b")))
//                testReorderArgs("Move free",
//                        obj -> (obj instanceof Item) ? ((Item) obj).id() : null,
//                        Arrays.asList(new Item("a"), new Item("b"), new NoKey("c"), new NoKey("d"), new Item("e")),
//                        Arrays.asList(new NoKey("d"), new Item("a"), new Item("b"), new NoKey("c"), new Item("e"))),
//                testReorderArgs("Move free (with other free)",
//                        obj -> (obj instanceof Item) ? ((Item) obj).id() : null,
//                        Arrays.asList(new Item("a"), new Item("b"), new NoKey("c"), new Item("d"), new Item("e")),
//                        Arrays.asList(new NoKey("c"), new Item("a"), new Item("b"), new Item("d"), new Item("e")))
        );
    }

    static <T> Arguments testReorderArgs(String msg, Function<T, String> keyFn, List<T> oldL, List<T> newList) {
        return Arguments.of(msg, keyFn, oldL, newList);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testReorderSource")
    <T> void testReorder(String msg, Function<T, String> keyFn, List<T> oldL, List<T> newList) {
        System.out.println(">> " + msg);

        final List<T> oldList = new ArrayList<>(oldL);
        final List<ListDiff.OutputMove<T>> outputMoves = ListDiff.diff(oldList, newList, i -> "type", keyFn, new ArrayList<>());
        outputMoves.forEach(m -> m.doMove(
                (i, v) -> {
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