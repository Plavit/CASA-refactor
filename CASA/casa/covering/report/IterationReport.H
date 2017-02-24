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


#ifndef ITERATION_REPORT_H
#define ITERATION_REPORT_H

#include <iostream>

class IterationReport :
  public Listener<SearchFinish<CoveringArray, CoverageCost> > {
protected:
  unsigned				total;
public:
  IterationReport() :
    total(0) {}
  void signal(const SearchFinish<CoveringArray, CoverageCost>&finish) {
    std::set<const Node<CoveringArray, CoverageCost>*>best =
      finish.source.getBest();
    if (best.size()) {
      const CoveringArray&state = (*best.begin())->getState();
      assert(state.isTrackingNoncoverage());
      std::cout << "Search stopped after " << finish.iterations << '/' <<
	finish.maxIterations << " iteration(s) with " <<
	state.getNoncoverage().size() << " uncovered t-sets and " <<
	state.getMultipleCoverageCount() << " multicovered t-sets" << std::endl;
    } else {
      std::cout << "Search stopped after " << finish.iterations << '/' <<
	finish.maxIterations << " iteration(s) with no results" << std::endl;
    }
    total += finish.iterations;
    std::cout << "Used " << total << " total iterations thus far" << std::endl;
  }
  unsigned getTotal() const {
    return total;
  }
};

#endif
