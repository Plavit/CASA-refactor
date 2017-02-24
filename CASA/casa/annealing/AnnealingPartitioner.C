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


#include "annealing/AnnealingPartitioner.H"
#include "io/Usage.H"

using namespace std;

unsigned AnnealingPartitioner::operator ()(unsigned offset, unsigned size) {
  if (guess && guess - offset < size) {
    return guess;
  }
  return offset + (unsigned)(searchPartition * size);
}

void AnnealingPartitioner::signal
  (const SearchFinish<CoveringArray, CoverageCost>&finish) {
  set<const Node<CoveringArray, CoverageCost>*>best = finish.source.getBest();
  if (!best.size()) {
    guess = 0;
    return;
  }
  const CoveringArray&state = (*best.begin())->getState();
  assert(state.isTrackingNoncoverage());
  if (finish.results.size()) {
    if (finish.iterations < 128) {
      lastGuess = guess;
      guess = 0;
    } else {
      unsigned ratio = finish.maxIterations / finish.iterations;
      unsigned delta = guess - lastGuess;
	lastGuess = guess;
      if (delta > 0){
	for (guess = state.getRows(); ratio; --guess, ratio >>= 1);
      } else {
	for (guess = state.getRows() + delta; ratio; --guess, ratio >>= 2);
      }
    }
  } else {
    lastGuess = guess;
    guess = state.getRows() + state.getNoncoverage().size() / 2;
  }
}
