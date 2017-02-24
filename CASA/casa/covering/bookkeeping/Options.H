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


#ifndef OPTIONS_H
#define OPTIONS_H

#include <cstdlib>
#include <utility>

#include "Array.H"

class Options {
protected:
  // The entry cumulativeValueCounts[i] is the number of values in options 0
  // through i, inclusive.
  Array<unsigned>			cumulativeValueCounts;
  // The entry owningOptions[i] is the option that the value i belongs to.
  Array<unsigned>			owningOptions;

public:
  Options();
  Options(Array<unsigned>valueCounts);
  Options(const Options&copy);
  unsigned getSize() const;
  unsigned getFirstSymbol(unsigned option) const;
  Array<unsigned>getFirstSymbols() const;
  unsigned getSymbolCount(unsigned option) const;
  Array<unsigned>getSymbolCounts() const;
  unsigned getLastSymbol(unsigned option) const;
  unsigned getRandomSymbol(unsigned option) const;
  unsigned getOtherRandomSymbol(unsigned option, unsigned exclude) const;
  unsigned getOption(unsigned symbol) const;
};

#endif
