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


#include "covering/bookkeeping/Options.H"

Options::Options() :
  cumulativeValueCounts(0),
  owningOptions(0) {}

Options::Options(Array<unsigned>values) :
  cumulativeValueCounts(values.getSize()) {
  unsigned cumulativeValueCount = 0;
  for (unsigned i = 0; i < values.getSize(); ++i) {
    cumulativeValueCount += values[i];
    cumulativeValueCounts[i] = cumulativeValueCount;
  }
  owningOptions = Array<unsigned>(cumulativeValueCount);
  for (unsigned i = 0, j = 0; i < values.getSize(); ++i) {
    for (unsigned k = values[i]; k--;) {
      owningOptions[j++] = i;
    }
  }
}

Options::Options(const Options&copy) :
  cumulativeValueCounts(copy.cumulativeValueCounts),
  owningOptions(copy.owningOptions) {}

unsigned Options::getSize() const {
  return cumulativeValueCounts.getSize();
}

unsigned Options::getFirstSymbol(unsigned option) const {
  return option ? cumulativeValueCounts[option - 1] : 0;
}

Array<unsigned>Options::getFirstSymbols() const {
  unsigned size = cumulativeValueCounts.getSize();
  Array<unsigned>result(size);
  for (unsigned i = size; i-- > 1;) {
    result[i] = cumulativeValueCounts[i - 1];
  }
  if (size) {
    result[0] = 0;
  }
  return result;
}

unsigned Options::getSymbolCount(unsigned option) const {
  return option ?
    (cumulativeValueCounts[option] - cumulativeValueCounts[option - 1]) :
    cumulativeValueCounts[0];
}

Array<unsigned>Options::getSymbolCounts() const {
  unsigned size = cumulativeValueCounts.getSize();
  Array<unsigned>result(size);
  for (unsigned i = size; i-- > 1;) {
    result[i] = cumulativeValueCounts[i] - cumulativeValueCounts[i - 1];
  }
  if (size) {
    result[0] = cumulativeValueCounts[0];
  }
  return result;
}

unsigned Options::getLastSymbol(unsigned option) const {
  return cumulativeValueCounts[option] - 1;
}

unsigned Options::getRandomSymbol(unsigned option) const {
  return rand() % getSymbolCount(option) + getFirstSymbol(option);
}

unsigned Options::getOtherRandomSymbol(unsigned option, unsigned exclude)
  const {
  unsigned count = getSymbolCount(option);
  if (count == 1) {
    return getFirstSymbol(option);
  }
  unsigned result = rand() % (count - 1) + getFirstSymbol(option);
  return (result >= exclude) ? (result + 1) : result;
}

unsigned Options::getOption(unsigned symbol) const {
  return owningOptions[symbol];
}
