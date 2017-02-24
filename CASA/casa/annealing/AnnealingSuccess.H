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


#ifndef ANNEALING_SUCCESS_H
#define ANNEALING_SUCCESS_H

#include "algorithms/BinarySearch.H"
#include "search/Search.H"

#include "covering/state/CoveringArray.H"
#include "covering/cost/CoverageCost.H"

class AnnealingSuccess : public ExpensiveUnaryPredicate {
protected:
  typedef Node<CoveringArray, CoverageCost>
					NodeT;

  Search<CoveringArray, CoverageCost>&	search;
  unsigned				iterations;
  mutable CoveringArray			solution;

public:
  AnnealingSuccess
    (Search<CoveringArray, CoverageCost>&search,
     unsigned iterations,
     const CoveringArray&initialSolution) :
      search(search),
      iterations(iterations),
      solution(initialSolution) {}

  bool operator ()(unsigned rows) const;

  void setIterations(unsigned iterations) {
    this->iterations = iterations;
  }

  const CoveringArray&getSolution() const {
    return solution;
  }
};

#endif
