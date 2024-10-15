package io.github.furrrlo.dui;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

class ListDiff {

    private ListDiff() {
    }

    public static <T> List<OutputMove<T>> diff(
            List<T> oldList,
            List<T> newList,
            Function<? super T, @Nullable String> extractTypeFn,
            Function<? super T, @Nullable String> extractKeyFn,
            List<OutputMove<T>> outputMoves
    ) {
        diff(oldList, newList, extractTypeFn, extractKeyFn, new OutputMoves<>(outputMoves));
        return outputMoves;
    }

    private static <T> void diff(
            List<T> oldList,
            List<T> newList,
            Function<? super T, @Nullable String> extractTypeFn,
            Function<? super T, @Nullable String> extractKeyFn,
            OutputMoves<T> outputMoves
    ) {
        final Map<String, T> oldKeys = extractKeys(oldList, extractKeyFn);

        final Map<String, T> newKeys = extractKeys(newList, extractKeyFn);
        final List<T> newFree = extractFree(newList, extractKeyFn);

        // Build a list to simulate what needs to happen
        final AtomicInteger freeIdx = new AtomicInteger(0);
        final SimulateList<T> simulate = new SimulateList<>(extractTypeFn, extractKeyFn, oldList, newKeys, oldV -> {
            final String oldKey = extractKeyFn.apply(oldV);
            // Value with key is no longer present, mark index to be removed
            if (oldKey != null && !newKeys.containsKey(oldKey))
                return null;
            // Found previous value, put it in place
            if (oldKey != null /* && newKeys.containsKey(oldKey) */)
                return newKeys.get(oldKey);
            // Either free item or newly inserted keyed item, put a random value
            if (freeIdx.get() < newFree.size())
                return newFree.get(freeIdx.getAndIncrement());
            // We have fewer values than before, mark index to be removed
            return null;
        });

        // Remove marked values
        for(int i = simulate.size() - 1; i >= 0; i--) {
            if(simulate.get(i) == null) {
                outputMoves.removeMove(i);
                simulate.remove(i);
            }
        }

        // We got the old list to be the same size or smaller than the new one,
        // now move and add stuff to go from oldList -> newList
        int newListIdx, simulateIdx = 0;
        for(newListIdx = 0; newListIdx < newList.size() && simulateIdx < simulate.size(); newListIdx++) {

            final T newItem = newList.get(newListIdx);
            final String key = extractKeyFn.apply(newItem);
            final String type = extractTypeFn.apply(newItem);

            final T simulateItem = simulate.get(simulateIdx);
            final String simulateItemKey = extractKeyFn.apply(simulateItem);
            final String simulateItemType = extractTypeFn.apply(simulateItem);

            boolean isSameType = Objects.equals(type, simulateItemType);
            // Item is already in place and has the same type
            if (Objects.equals(key, simulateItemKey) && isSameType) {
                simulateIdx++;
                continue;
            }
            // Item with a new key, insert it
            final T maybeOldKeyedItem = key != null ? oldKeys.get(key) : null;
            if (key != null && maybeOldKeyedItem == null) {
                final T replacedOldItem = simulate.findItemToBeReplacedFor(newItem, null);
                outputMoves.insertMove(newListIdx, newItem, replacedOldItem);
                continue;
            }
            // If the item is at the next position, just remove the current item
            if(simulateIdx + 1 < simulate.size()) {
                String nextItemKey = extractKeyFn.apply(simulate.get(simulateIdx + 1));
                String nextItemType = extractTypeFn.apply(simulateItem);

                if (Objects.equals(key, nextItemKey) && Objects.equals(type, nextItemType)) {
                    outputMoves.removeMove(newListIdx);
                    simulate.remove(simulateIdx); // Remove the current one
                    simulateIdx++; // We just checked that this one is correct, we can just skip
                    continue;
                }
            }

            // Item is not in this position and not in the next one, so insert it (remove it first if not already)
            // If it's a free item, we have no way to search for it, so ignore them
            boolean wasKeyedItemAlreadyRemoved = key != null && simulate.keyedToBeReAddedByKey.remove(key) != null;
            if(key != null && !wasKeyedItemAlreadyRemoved) {
                // TODO: remove this somehow
                for (int offset = 2; simulateIdx + offset < simulate.size(); offset++) {
                    String nextItemKey = extractKeyFn.apply(simulate.get(simulateIdx + offset));
                    if (Objects.equals(key, nextItemKey)) {
                        outputMoves.removeMove(newListIdx + offset);
                        simulate.remove(simulateIdx + offset);
                        break;
                    }
                }
            }

            // Types are different, need to replace it by removing the previous one
            if(key == null && !isSameType)
                outputMoves.removeMove(newListIdx);
            final T replacedOldItem = simulate.findItemToBeReplacedFor(newItem, maybeOldKeyedItem);
            outputMoves.insertMove(newListIdx, newItem, replacedOldItem);
        }

        // If the old list was smaller than the new one (simulateIdx >= simulate.size()),
        // we have fewer values than what we need, just get them from the new ones
        for(; newListIdx < newList.size(); newListIdx++) {
            final T newItem = newList.get(newListIdx);
            final T replacedOldItem = simulate.findItemToBeReplacedFor(newItem, null);
            outputMoves.insertMove(newListIdx, newItem, replacedOldItem);
        }

        // If simulate is still longer than newList, remove items until both are the same length
        final int newListFinalSize = newListIdx;
        for (int i = simulateIdx; i < simulate.size(); i++)
            outputMoves.removeMove(newListFinalSize);
    }

    public interface OutputMove<T> {

        void doMove(Insert<T> insert, IntConsumer remove);

        interface Insert<T> {

            void insert(int idx, T val, @Nullable T oldVal);
        }
    }

    public static class OutputMoves<T> {

        private final List<OutputMove<T>> moves;

        public OutputMoves(List<OutputMove<T>> moves) {
            this.moves = moves;
        }

        void insertMove(int idx, T val, @Nullable T oldVal) {
            moves.add((insert, __) -> insert.insert(idx, val, oldVal));
        }

        void removeMove(int idx) {
            moves.add((__, remove) -> remove.accept(idx));
        }
    }

    private static class SimulateList<T> {

        private final List<T> newSimulation;
        private final List<T> oldSimulation;
        private final Map<String, T> newKeys;

        private final Function<? super T, @Nullable String> extractTypeFn;
        private final Function<? super T, @Nullable String> extractKeyFn;

        final Map<String, T> keyedToBeReAddedByKey = new HashMap<>();
        final Map<String, List<T>> keyedToBeReUsedByType = new HashMap<>();
        final Map<String, List<T>> freeToBeReUsedByType = new HashMap<>();

        public SimulateList(Function<? super T, @Nullable String> extractTypeFn,
                            Function<? super T, @Nullable String> extractKeyFn,
                            List<T> oldList,
                            Map<String, T> newKeys,
                            Function<T, T> buildSimulation) {
            this.extractTypeFn = extractTypeFn;
            this.extractKeyFn = extractKeyFn;
            this.newKeys = newKeys;
            this.oldSimulation = new ArrayList<>(oldList);
            this.newSimulation = oldList.stream()
                    .map(buildSimulation)
                    .collect(Collectors.toList());
        }

        public T get(int i) {
            return newSimulation.get(i);
        }

        public int size() {
            return newSimulation.size();
        }

        public void remove(int i) {
            T removed = oldSimulation.remove(i);
            String key = extractKeyFn.apply(removed);
            if(key != null && newKeys.containsKey(key)) {
                keyedToBeReAddedByKey.put(key, removed);
            } else {
                String type = extractTypeFn.apply(removed);
                if(key != null /* && !newKeys.containsKey(key) */)
                    keyedToBeReUsedByType.computeIfAbsent(type, t -> new ArrayList<>()).add(removed);
                else
                    freeToBeReUsedByType.computeIfAbsent(type, t -> new ArrayList<>()).add(removed);
            }

            newSimulation.remove(i);
        }

        public @Nullable T findItemToBeReplacedFor(T item, @Nullable T firstCandidate) {
            final String key = extractKeyFn.apply(item);
            final String type = extractTypeFn.apply(item);

            List<T> candidates;
            final T candidate;
            if(firstCandidate != null && Objects.equals(type, extractTypeFn.apply(firstCandidate))) {
                candidate = firstCandidate;
            } else if(key != null && !(candidates = keyedToBeReUsedByType.getOrDefault(type, Collections.emptyList())).isEmpty()) {
                candidate = candidates.remove(0);
            } else if(!(candidates = freeToBeReUsedByType.getOrDefault(type, Collections.emptyList())).isEmpty()) {
                candidate = candidates.remove(0);
            } else {
                candidate = null;
            }

            return candidate;
        }
    }

    private static <T> Map<String, T> extractKeys(List<T> list,
                                                  Function<? super T, @Nullable String> extractKeyFn) {
        return list.stream()
                .map(v -> new AbstractMap.SimpleEntry<>(extractKeyFn.apply(v), v))
                .filter(e -> e.getKey() != null)
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue));
    }

    private static <T> List<T> extractFree(List<T> list, Function<? super T, @Nullable String> extractKeyFn) {
        return list.stream()
                .filter(v -> extractKeyFn.apply(v) == null)
                .collect(Collectors.toList());
    }
}
