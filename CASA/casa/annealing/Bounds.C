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


#include <cmath>
#include <algorithm>
#include <functional>

#include "annealing/Bounds.H"

using namespace std;

#ifndef UPPER_BOUND_SCALING_FACTOR
#define UPPER_BOUND_SCALING_FACTOR 5
#endif

unsigned computeLowerBound(unsigned strength, const Options&options) {
  if (lowerBound) {
    return lowerBound;
  }
  static greater<unsigned>backwards;
  unsigned result = 1;
  Array<unsigned>symbolCounts(options.getSize());
  for (unsigned i = symbolCounts.getSize(); i--;) {
    symbolCounts[i] = options.getSymbolCount(i);
  }
  partial_sort
    (symbolCounts + 0,
     symbolCounts + strength,
     symbolCounts + symbolCounts.getSize(),
     backwards);
  for (unsigned i = strength; i--;) {
    result *= symbolCounts[i];
  }
  return result;
}

unsigned computeUpperBound(unsigned strength, const Options&options) {
  if (upperBound) {
    return upperBound;
  }
  unsigned max = 0;
  Array<unsigned>symbolCounts(options.getSize());
  for (unsigned i = symbolCounts.getSize(); i--;) {
    unsigned candidate = options.getSymbolCount(i);
    if (candidate > max) {
      max = candidate;
    }
  }
  return (unsigned)(UPPER_BOUND_SCALING_FACTOR *
		    pow((double)max, (double)strength));
}
