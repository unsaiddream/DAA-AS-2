# Assignment 2 — Data Structures

**Sanzhar Karaulov**  
**Design and Analysis of Algorithms**  
**Implementation:** Java 17 language level; benchmark run on an ARM64 Mac with OpenJDK 25.

## 1. Structures and operation counts

`DynamicArray` and `MinHeap` store primitive values in `int[]`. `MyLinkedList` stores primitive `int` fields in nodes and keeps both a head and a tail reference. The array and list implement the same `IntSequence` interface. In every structure, counters are updated inside the operations: a **step** is an array-cell read or traversal of a `next` link, a **move** is an element transfer within/between arrays or an update to a list link, and a **comparison** compares two stored values. Index checks and loop-condition checks are excluded. Counters are reset after pre-filling W1–W3, so those rows measure only the requested workload; W4 includes both heap insertion and extraction.

## 2. Asymptotic complexity

Here `n` is the current number of elements. Average bounds for indexed operations assume uniformly chosen valid indexes; average search assumes a uniformly placed present item or an absent item. Heap insertion's average bound assumes independent random keys and is amortized over array growth. Auxiliary space means temporary space used by one operation, beyond the structure's existing storage.

| Structure and operation | Best | Average | Worst | Auxiliary space | Justification |
|---|---:|---:|---:|---:|---|
| DynamicArray `add(x)` | Θ(1) | Θ(1) amortized | Θ(n) | O(n) on growth | Most appends write once; doubling copies all `n` cells only when full. |
| DynamicArray `add(index,x)` | Θ(1) | Θ(n) | Θ(n) | O(n) on growth | Appending without growth shifts none; other indexes shift a suffix. |
| DynamicArray `remove(index)` | Θ(1) | Θ(n) | Θ(n) | Θ(1) | Removing the last item shifts none; earlier items shift a suffix. |
| DynamicArray `get(index)` | Θ(1) | Θ(1) | Θ(1) | Θ(1) | Direct array addressing reads one cell. |
| DynamicArray `contains(x)` | Θ(1) | Θ(n) | Θ(n) | Θ(1) | Linear scan can stop at the first cell or examine all `n`. |
| MyLinkedList `add(x)` | Θ(1) | Θ(1) | Θ(1) | Θ(1) | The tail reference permits a constant number of link updates. |
| MyLinkedList `add(index,x)` | Θ(1) | Θ(n) | Θ(n) | Θ(1) | Head and tail insertion are constant; middle insertion traverses from head. |
| MyLinkedList `remove(index)` | Θ(1) | Θ(n) | Θ(n) | Θ(1) | Removing head is constant; other indexes require finding the predecessor. |
| MyLinkedList `get(index)` | Θ(1) | Θ(n) | Θ(n) | Θ(1) | Access follows `index` links from head. |
| MyLinkedList `contains(x)` | Θ(1) | Θ(n) | Θ(n) | Θ(1) | It compares nodes sequentially until a match or the end. |
| MinHeap `insert(x)` | Θ(1) | Θ(1) expected, amortized | Θ(n) | O(n) on growth | Random-key bubble-up has constant expected work; a full array requires an `n`-cell copy, while bubble-up alone is O(log n). |
| MinHeap `peekMin()` | Θ(1) | Θ(1) | Θ(1) | Θ(1) | The minimum is always at index zero. |
| MinHeap `extractMin()` | Θ(1) | Θ(log n) | Θ(log n) | Θ(1) | A singleton needs no bubble-down; otherwise the replacement can descend a heap height. |

Stored-space bounds are Θ(n) for all three structures, though the list has one node object and a reference per value, while the arrays have unused capacity after doubling. The heap insertion average is a distribution-dependent expectation, not a guarantee for arbitrary sequences; a study of repeated random insertion also finds a bounded average number of swaps ([Bollobás and Simon, 1985](https://digitalcommons.memphis.edu/facpubs/5607/)).

## 3. Loop-invariant proofs

### 3.1 `DynamicArray.contains(x)`

**Invariant.** Immediately before iteration `i`, none of `elements[0..i-1]` equals `x`. **Initialization.** At `i = 0`, that prefix is empty, so the claim is true. **Maintenance.** The method reads `elements[i]`; if it equals `x`, returning `true` is correct, and otherwise the prefix through `i` has no match, establishing the invariant for `i + 1`. **Termination.** If the loop finishes with `i = size`, every stored element has been checked and none equals `x`, so returning `false` is correct. **Conclusion.** The early-return case and the completed-scan case cover every possible result.

### 3.2 `MinHeap.extractMin()` bubble-down loop

**Invariant.** At the start of each bubble-down iteration, every subtree except possibly the one rooted at `index` satisfies the min-heap property; all ancestors of `index` already satisfy it, and any violation is between the value at `index` and its children. **Initialization.** The root is replaced by the last value, leaving the two subtrees below the root unchanged, so only the root may violate the property. **Maintenance.** If the parent is too large, the method swaps it with the smaller child: the smaller value now satisfies both child relationships at the old position, and any remaining violation moves to the chosen child's position. **Termination.** At a leaf, or when the parent is no greater than the smaller child, the remaining subtree satisfies heap order; by the invariant the entire heap does too. **Conclusion.** The original root was the minimum before removal, and the loop restores a valid heap over all remaining elements.

## 4. Benchmark and plots

The input for each size is generated afresh with `new Random(42)`. W1 executes 10,000 random indexed reads. W2 executes 1,000 searches, alternating present and absent values. W3 performs 1,000 indexed insertions followed by 1,000 removals at the head or fixed middle index. W4 inserts all `n` values into a heap, extracts all of them, and checks non-decreasing output. Each case has one warm-up run and five measured runs; the CSV records the median time and the counters from that median run. JVM compilation, garbage collection, and machine load can affect times, so the physical counters are useful independent evidence.

| Workload / variant | Time chart | Operation chart |
|---|---|---|
| W1 random access | [PNG](results/plots/w1_time.png) | [PNG](results/plots/w1_metrics.png) |
| W2 search | [PNG](results/plots/w2_time.png) | [PNG](results/plots/w2_metrics.png) |
| W3 head | [PNG](results/plots/w3_head_time.png) | [PNG](results/plots/w3_head_metrics.png) |
| W3 middle | [PNG](results/plots/w3_middle_time.png) | [PNG](results/plots/w3_middle_metrics.png) |
| W4 priority | [PNG](results/plots/w4_time.png) | [PNG](results/plots/w4_metrics.png) |

At `n = 100,000`, W1 took 0.038 ms for `DynamicArray` and 791.871 ms for `MyLinkedList`, with 10,000 versus 502,489,208 steps. W2 took 25.585 ms versus 123.307 ms, although both made 73,728,608 value comparisons. In W3 head, the list made zero traversals and 3,000 link updates, whereas the array made 200,999,000 element moves. At W3 middle, the list made 99,999,000 traversals, while the array made 100,999,000 moves. W4 at `n = 100,000` took 9.631 ms and recorded 3,059,125 element comparisons. All raw values are in [`results/results.csv`](results/results.csv).

## 5. Discussion

`DynamicArray.get(i)` is faster because it computes an address directly and reads one cell. Its `int[]` stores neighboring values contiguously, so CPU cache lines often bring several useful values in one transfer. Sequential array iteration benefits from that spatial locality even when its asymptotic cost is the same as list iteration. `MyLinkedList.get(i)` must follow `i` references, which creates a dependency chain called pointer chasing. Nodes can be scattered across memory, so each link can incur a cache miss. Node object headers and references also increase memory traffic, and allocating many nodes adds garbage-collector work. Those factors explain why W2 has almost equal comparison counts but a much slower list time. The list is the better choice for head insertions and removals because no array suffix is shifted. It is less attractive for middle operations in this singly linked implementation because finding the predecessor takes linear time. `DynamicArray` is preferable for frequent random indexed access and compact primitive storage. `MinHeap` is appropriate when the next job must always be the smallest-priority value, since `peekMin()` is constant time and updates are logarithmic apart from occasional growth. Finally, nanosecond-scale W3 head timings should be treated cautiously because JIT optimization and timer noise can strongly affect very short runs.
