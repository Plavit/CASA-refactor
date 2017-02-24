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


#ifndef COVERING_ARRAY_ANNEALING_FILTER_H
#define COVERING_ARRAY_ANNEALING_FILTER_H

#include "annealing/AnnealingFilter.H"

#include "covering/state/CoveringArray.H"
#include "covering/cost/CoverageCost.H"

#ifndef MULTIPLE_COVERAGE_WEIGHT
#define MULTIPLE_COVERAGE_WEIGHT 0.1L
#endif

class CoveringArrayAnnealingFilter :
  public AnnealingFilter<CoveringArray, CoverageCost> {
public:
  CoveringArrayAnnealingFilter(double temperature, double decay) :
    AnnealingFilter<CoveringArray, CoverageCost>(temperature, decay) {}

  double convertToDelta(CoverageCost childEstimate, CoverageCost parentEstimate)
    const {
    unsigned childNoncoverage = childEstimate.getNoncoverage();
    unsigned parentNoncoverage = parentEstimate.getNoncoverage();
    unsigned childMultipleCoverage = childEstimate.getMultipleCoverage();
    unsigned parentMultipleCoverage = parentEstimate.getMultipleCoverage();
    return -(double)(childNoncoverage - parentNoncoverage) -
      MULTIPLE_COVERAGE_WEIGHT *
        (childMultipleCoverage - parentMultipleCoverage);
  }
};

#endif
