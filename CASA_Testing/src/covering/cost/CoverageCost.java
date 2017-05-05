package covering.cost;

public class CoverageCost implements Comparable<CoverageCost> {

    protected long noncoverage;
    protected long multipleCoverage;

    public CoverageCost() {
        this.noncoverage = 0;
        this.multipleCoverage = 0;
    }

    public CoverageCost(long noncoverage, long multipleCoverage) {
        this.noncoverage = noncoverage;
        this.multipleCoverage = multipleCoverage;
    }

    public long getNoncoverage() {
        return noncoverage;
    }

    public long getMultipleCoverage() {
        return multipleCoverage;
    }

    public int compareTo(CoverageCost o) {
        if (noncoverage < o.getNoncoverage()) return -1;
        if (noncoverage > o.getNoncoverage()) return 1;
        if (multipleCoverage < o.getMultipleCoverage()) return -1;
        if (multipleCoverage > o.getMultipleCoverage()) return 1;
        return 0;
    }
}
