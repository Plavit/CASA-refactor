package covering.goal;

import covering.state.CoveringArray;
import search.Goal;

public class CoverageGoal implements Goal {

    long targetCoverage;

    public CoverageGoal(long targetCoverage) {
        this.targetCoverage = targetCoverage;
    }

    public long getTargetCoverage() {
        return targetCoverage;
    }

    @Override
    public boolean isGoal(CoveringArray array) {

        // TODO - is this ok?
//        C Code
//        assert(array.getCoverageCount() <= targetCoverage);
//        return array.getCoverageCount() == targetCoverage;

        return array.getCoverageCount() <= this.targetCoverage;
    }
}
