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


#ifndef COVERAGE_GOAL_H
#define COVERAGE_GOAL_H

#include "search/Goal.H"

#include "covering/state/CoveringArray.H"

class CoverageGoal : public Goal<CoveringArray> {
protected:
  unsigned				targetCoverage;
public:
  CoverageGoal(unsigned targetCoverage) :
    targetCoverage(targetCoverage) {}

  unsigned getTargetCoverage() {
    return targetCoverage;
  }

  bool isGoal(const CoveringArray&array) {
    assert(array.getCoverageCount() <= targetCoverage);
    return array.getCoverageCount() == targetCoverage;
  }
};

#endif
