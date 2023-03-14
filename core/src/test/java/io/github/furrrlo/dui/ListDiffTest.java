package io.github.furrrlo.dui;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListDiffTest {

    @Test
    void test() {
        doTest("only keys",
                Item::id,
                Arrays.asList(new Item("a"), new Item("b"), new Item("c"), new Item("d"), new Item("e")),
                Arrays.asList(new Item("c"), new Item("a"), new Item("b"), new Item("e"), new Item("f")));
        doTest("no keys",
                i -> null,
                Arrays.asList(new Item("a"), new Item("b"), new Item("c"), new Item("d"), new Item("e")),
                Arrays.asList(new Item("c"), new Item("a"), new Item("b"), new Item("e"), new Item("f")));
        doTest("key moved afterward",
                obj -> (obj instanceof Item) ? ((Item) obj).id() : null,
                Arrays.asList(new NoKey("a"), new NoKey("b"), new Item("c"), new NoKey("d"), new NoKey("e")),
                Arrays.asList(new Item("c"), new NoKey("a"), new NoKey("b"), new NoKey("e"), new NoKey("f")));
    }

    private <T> void doTest(String msg, Function<T, String> keyFn, List<T> oldL, List<T> newList) {
        System.out.println(">> " + msg);

        final List<T> oldList = new ArrayList<>(oldL);
        final List<ListDiff.OutputMove<T>> outputMoves = ListDiff.reorder(oldList, newList, keyFn, new ArrayList<>());
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
        public String toString() {
            return "NoKey{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}