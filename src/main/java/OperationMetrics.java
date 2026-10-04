public class OperationMetrics {
    private long steps;
    private long moves;
    private long comparisons;

    public void addStep() {
        steps++;
    }

    public void addMove() {
        moves++;
    }

    public void addComparison() {
        comparisons++;
    }

    public long getSteps() {
        return steps;
    }

    public long getMoves() {
        return moves;
    }

    public long getComparisons() {
        return comparisons;
    }

    public void reset() {
        steps = 0;
        moves = 0;
        comparisons = 0;
    }
}