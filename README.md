# DAA Assignment 2: Data Structures

An in-memory workload engine implemented with primitive `int` values. It contains a dynamic array, a singly linked list, and an array-based min heap. Production structure code does not use Java collection classes.

## Requirements

- JDK 17 or newer and Maven 3.9 or newer.
- Python 3 and Matplotlib for regenerating the PNG charts.

## Build and test

Run from the repository root:

```bash
mvn clean test
```

The JUnit 5 suite checks normal operations, boundaries, invalid inputs, random operations against Java collections, heap order after operations, sorted extraction, and representative metric counts.

## Benchmark

```bash
bash scripts/run_benchmark.sh
```

This one command builds, runs the tests, and regenerates `results/results.csv`. The benchmark uses `Random(42)` input, one warm-up run, five measured runs, and the median time for each case. It runs four workloads at `n = 100, 1,000, 10,000, 100,000`. It can take several minutes because random access on the linked list requires many node traversals.

## Charts

```bash
python3 -m venv .venv
.venv/bin/python -m pip install -r requirements-plots.txt
.venv/bin/python scripts/plot_results.py
```

The chart script reads the CSV and writes ten PNG charts to `results/plots/`: time and physical-operation charts for W1, W2, W3 head, W3 middle, and W4.

## Layout

- `src/main/java/`: structures, shared interface, counters, and benchmark.
- `src/test/java/`: JUnit 5 tests.
- `results/results.csv`: measured output.
- `results/plots/`: generated PNG charts.
- `REPORT.md`: complexity, invariants, results, and discussion.
