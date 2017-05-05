package covering.heuristic;

// Copyright 2008, 2009 Brady J. Garvin

// This file is part of Covering Arrays by Simulated Annealing (CASA).

// CASA is free software: you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// CASA is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with CASA.  If not, see <http://www.gnu.org/licenses/>.

import covering.cost.CoverageCost;
import covering.goal.CoverageGoal;
import covering.state.CoveringArray;
import search.Goal;
import search.Heuristic;

//C_CODE
//class CoveringArrayHeuristic : public Heuristic<CoveringArray, CoverageCost> {
//  CoverageCost estimate (const CoveringArray&state, const Goal<CoveringArray>&goal) const {
//      unsigned targetCoverage = ((CoverageGoal&)goal).getTargetCoverage();
//      return CoverageCost
//          (targetCoverage - state.getCoverageCount(),
//          state.getMultipleCoverageCount());
//  }
//};

//TODO ??? is this ok?
public class CoveringArrayHeuristic implements Heuristic {

    @Override
    public CoverageCost estimate(CoveringArray state, Goal<CoveringArray> goal) { //TODO ???
        long targetCoverage = ((CoverageGoal) goal).getTargetCoverage(); //TODO ???
        return new CoverageCost(targetCoverage - state.getCoverageCount(), state.getMultipleCoverageCount());
    }
}
