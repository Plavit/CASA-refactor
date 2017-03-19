package covering.cost;


public class CoverageCost {

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

    public boolean op_thisLessThenArg(CoverageCost coverageCost){
        if(this.noncoverage < coverageCost.getNoncoverage()) return true;
        if(this.noncoverage > this.getNoncoverage()) return false;
        return this.multipleCoverage < coverageCost.getMultipleCoverage();
    }

    public boolean op_thisLessEqualThenArg(CoverageCost coverageCost){
        if(this.noncoverage < coverageCost.getNoncoverage()) return true;
        if(this.noncoverage > this.getNoncoverage()) return false;
        return this.multipleCoverage <= coverageCost.getMultipleCoverage();
    }

    public boolean op_thisEqualToArg(CoverageCost coverageCost){
        return this.noncoverage == coverageCost.getNoncoverage() && this.multipleCoverage == coverageCost.getMultipleCoverage();
    }

}
