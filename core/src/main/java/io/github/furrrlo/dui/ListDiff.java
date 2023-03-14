package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;

class ListDiff {

    private ListDiff() {
    }

    public static <T> List<OutputMove<T>> diff(
            List<T> oldList,
            List<T> newList,
            Function<T, @Nullable String> extractTypeFn,
            Function<T, @Nullable String> extractKeyFn,
            List<OutputMove<T>> outputMoves
    ) {
        final List<T> building = new ArrayList<>(Math.max(newList.size(), oldList.size()));
        building.addAll(oldList);

        final AtomicInteger currSize = new AtomicInteger();
        final ObjIntConsumer<OutputMove<T>> checkItemsTillIndex = (currMove, idx) -> {
            for(; currSize.get() <= idx; currSize.getAndIncrement()) {
                final int currIdx = currSize.get();

                final T oldItem = building.get(currIdx);
                final String oldItemType = extractTypeFn.apply(oldItem);
                final T newItem = newList.get(currIdx);
                final String newItemType = extractTypeFn.apply(newItem);

                if(Objects.equals(oldItemType, newItemType))
                    continue;

                // Different types, need to replace it
                if(currIdx != idx || currMove == null) {
                    outputMoves.add((__, remove) -> remove.accept(currIdx));
                    outputMoves.add((insert, __) -> insert.insert(currIdx, newItem));
                } else {
                    // If it's the one I'm currently adding, no need to remove it
                    // I just need to change the move
                    currMove = (insert, __) -> insert.insert(currIdx, newItem);
                }
            }

            if(currMove != null)
                outputMoves.add(currMove);
        };

        reorder(oldList, newList, extractKeyFn, new ArrayList<>()).forEach(move -> move.doMove(
                (idx, child) -> {
                    if(idx >= building.size())
                        building.add(child);
                    else
                        building.add(idx, child);
                    // Check till this index
                    checkItemsTillIndex.accept(move, idx);
                },
                idx -> {
                    building.remove(idx);
                    outputMoves.add(move);
                }
        ));
        // Check remaining
        checkItemsTillIndex.accept(null, newList.size() - 1);

        return outputMoves;
    }

    static <T> List<OutputMove<T>> reorder(
            List<T> oldList,
            List<T> newList,
            Function<T, @Nullable String> extractKeyFn,
            List<OutputMove<T>> outputMoves
    ) {
        final Map<String, T> oldKeys = extractKeys(oldList, extractKeyFn);

        final Map<String, T> newKeys = extractKeys(newList, extractKeyFn);
        final List<T> newFree = extractFree(newList, extractKeyFn);

        // Build a list to simulate what needs to happen
        final List<T> simulate = new ArrayList<>();
        int freeIdx = 0;

        for (final T oldV : oldList) {
            final String oldKey = extractKeyFn.apply(oldV);
            // Value with key is no longer present, mark index to be removed
            if (oldKey != null && !newKeys.containsKey(oldKey)) {
                simulate.add(null);
                continue;
            }
            // Found previous value, put it in place
            if (oldKey != null /* && newKeys.containsKey(oldKey) */) {
                simulate.add(newKeys.get(oldKey));
                continue;
            }
            // We have fewer values than before, mark index to be removed
            if (freeIdx >= newFree.size()) {
                simulate.add(null);
                continue;
            }

            simulate.add(newFree.get(freeIdx++));
        }

        // Remove marked values
        for(int i = simulate.size() - 1; i >= 0; i--) {
            if(simulate.get(i) == null) {
                final int idx = i;
                outputMoves.add((__, remove) -> remove.accept(idx));
                simulate.remove(i);
            }
        }

        // Move and add stuff to go from oldList -> newList
        int newListIdx0, simulateIdx = 0;
        for(newListIdx0 = 0; newListIdx0 < newList.size(); newListIdx0++) {
            final int newListIdx = newListIdx0;
            final T newItem = newList.get(newListIdx);
            final String key = extractKeyFn.apply(newItem);
            // We have fewer values than what we need, we need to add new ones
            if (simulateIdx >= simulate.size()) {
                outputMoves.add((insert, __) -> insert.insert(newListIdx, newItem));
                continue;
            }

            final T simulateItem = simulate.get(simulateIdx);
            final String simulateItemKey = extractKeyFn.apply(simulateItem);
            // Item is already in place
            if (Objects.equals(key, simulateItemKey)) {
                simulateIdx++;
                continue;
            }
            // Item with a new key, insert it
            if (key != null && !oldKeys.containsKey(key)) {
                outputMoves.add((insert, __) -> insert.insert(newListIdx, newItem));
                continue;
            }
            // If the item is at the next position, just remove the current item
            if(simulateIdx + 1 < simulate.size()) {
                String nextItemKey = extractKeyFn.apply(simulate.get(simulateIdx + 1));
                if (Objects.equals(key, nextItemKey)) {
                    outputMoves.add((__, remove) -> remove.accept(newListIdx));
                    simulate.remove(simulateIdx); // Remove the current one
                    simulateIdx++; // We just checked that this one is correct, we can just skip
                    continue;
                }
            }

            // Item is not in this position and not in the next one, so insert it (remove it first if present)
            for(int offset = 2; simulateIdx + offset < simulate.size(); offset++) {
                String nextItemKey = extractKeyFn.apply(simulate.get(simulateIdx + offset));
                if (Objects.equals(key, nextItemKey)) {
                    final int offset0 = offset;
                    outputMoves.add((__, remove) -> remove.accept(newListIdx + offset0));
                    simulate.remove(simulateIdx + offset);
                    break;
                }
            }

            outputMoves.add((insert, __) -> insert.insert(newListIdx, newItem));
        }

        // if simulate is still longer than newList, remove items until both are the same length
        final int newListFinalSize = newListIdx0;
        for (int i = simulateIdx; i < simulate.size(); i++)
            outputMoves.add((__, remove) -> remove.accept(newListFinalSize));

        return outputMoves;
    }

    public interface OutputMove<T> {

        void doMove(Insert<T> insert, IntConsumer remove);

        interface Insert<T> {

            void insert(int idx, T val);
        }
    }

    private static <T> Map<String, T> extractKeys(List<T> list,
                                                  Function<T, @Nullable String> extractKeyFn) {
        return list.stream()
                .map(v -> new AbstractMap.SimpleEntry<>(extractKeyFn.apply(v), v))
                .filter(e -> e.getKey() != null)
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue));
    }

    private static <T> List<T> extractFree(List<T> list, Function<T, @Nullable String> extractKeyFn) {
        return list.stream()
                .filter(v -> extractKeyFn.apply(v) == null)
                .collect(Collectors.toList());
    }
}
