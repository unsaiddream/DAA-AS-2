import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int MEASURED_RUNS = 5;
    private static volatile long checksum;

    private record Result(
            long timeNs,
            long steps,
            long moves,
            long comparisons
    ) {}

    public static void main(String[] args) throws IOException {
        StringBuilder csv = new StringBuilder();
        csv.append("workload,variant,structure,n,time_ms,steps,moves,comparisons\n");

        for (int n : SIZES) {
            int[] data = createData(n);
            int[] indexes = createIndexes(n);
            int[] queries = createQueries(data);

            measure(csv, "W1", "-", "DynamicArray", n, data, indexes);
            measure(csv, "W1", "-", "MyLinkedList", n, data, indexes);
            measure(csv, "W2", "-", "DynamicArray", n, data, queries);
            measure(csv, "W2", "-", "MyLinkedList", n, data, queries);
            measure(csv, "W3", "head", "DynamicArray", n, data, null);
            measure(csv, "W3", "head", "MyLinkedList", n, data, null);
            measure(csv, "W3", "middle", "DynamicArray", n, data, null);
            measure(csv, "W3", "middle", "MyLinkedList", n, data, null);
            measure(csv, "W4", "-", "MinHeap", n, data, null);
        }

        Files.createDirectories(Path.of("results"));
        Files.writeString(Path.of("results/results.csv"), csv);
        System.out.println("Saved results/results.csv");
    }

    private static int[] createData(int n) {
        Random random = new Random(42);
        int[] data = new int[n];

        for (int i = 0; i < n; i++) {
            data[i] = random.nextInt(1_000_000);
        }
        return data;
    }

    private static int[] createIndexes(int n) {
        Random random = new Random(42);
        int[] indexes = new int[10_000];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = random.nextInt(n);
        }
        return indexes;
    }

    private static int[] createQueries(int[] data) {
        Random random = new Random(42);
        int[] queries = new int[1_000];

        for (int i = 0; i < queries.length; i++) {
            queries[i] = i % 2 == 0
                    ? data[random.nextInt(data.length)]
                    : -i;
        }
        return queries;
    }

    private static void measure(
            StringBuilder csv,
            String workload,
            String variant,
            String structureName,
            int n,
            int[] data,
            int[] operations
    ) {
        runOnce(workload, variant, structureName, data, operations);

        Result[] runs = new Result[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            runs[i] = runOnce(workload, variant, structureName, data, operations);
        }

        Arrays.sort(runs, Comparator.comparingLong(Result::timeNs));
        Result median = runs[MEASURED_RUNS / 2];

        csv.append(String.format(Locale.ROOT,
                "%s,%s,%s,%d,%.3f,%d,%d,%d%n",
                workload,
                variant,
                structureName,
                n,
                median.timeNs() / 1_000_000.0,
                median.steps(),
                median.moves(),
                median.comparisons()
        ));

        System.out.printf("%s %s %s n=%d done%n",
                workload, variant, structureName, n);
    }

    private static Result runOnce(
            String workload,
            String variant,
            String structureName,
            int[] data,
            int[] operations
    ) {
        if (workload.equals("W4")) {
            return runHeap(data);
        }

        IntSequence structure = structureName.equals("DynamicArray")
                ? new DynamicArray()
                : new MyLinkedList();

        for (int value : data) {
            structure.add(value);
        }

        structure.metrics().reset();
        long localChecksum = 0;
        long start = System.nanoTime();

        switch (workload) {
            case "W1" -> {
                for (int index : operations) {
                    localChecksum += structure.get(index);
                }
            }
            case "W2" -> {
                for (int value : operations) {
                    if (structure.contains(value)) {
                        localChecksum++;
                    }
                }
            }
            case "W3" -> {
                int index = variant.equals("head") ? 0 : data.length / 2;

                for (int i = 0; i < 1_000; i++) {
                    structure.add(index, data[i % data.length]);
                }
                for (int i = 0; i < 1_000; i++) {
                    localChecksum += structure.remove(index);
                }

                if (structure.size() != data.length) {
                    throw new IllegalStateException("W3 changed final size");
                }
            }
            default -> throw new IllegalArgumentException(workload);
        }

        long elapsed = System.nanoTime() - start;
        checksum = localChecksum;
        return result(elapsed, structure.metrics());
    }

    private static Result runHeap(int[] data) {
        MinHeap heap = new MinHeap();
        long localChecksum = 0;
        int previous = Integer.MIN_VALUE;
        long start = System.nanoTime();

        for (int value : data) {
            heap.insert(value);
        }

        for (int i = 0; i < data.length; i++) {
            int current = heap.extractMin();
            if (current < previous) {
                throw new IllegalStateException("Heap output is not sorted");
            }
            previous = current;
            localChecksum += current;
        }

        long elapsed = System.nanoTime() - start;
        checksum = localChecksum;
        return result(elapsed, heap.metrics());
    }

    private static Result result(long elapsed, OperationMetrics metrics) {
        return new Result(
                elapsed,
                metrics.getSteps(),
                metrics.getMoves(),
                metrics.getComparisons()
        );
    }
}