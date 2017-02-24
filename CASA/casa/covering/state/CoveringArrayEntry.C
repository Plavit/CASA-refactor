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


#include "covering/state/CoveringArray.H"

using namespace std;

void CoveringArray::Entry::updateTracking(unsigned value) {
  if (owner(row,option) == value) {
    return;
  }
  unsigned strength = owner.coverage.getStrength();
  unsigned limit = owner.coverage.getOptions().getSize();
  Array<unsigned>firsts = owner.coverage.getOptions().getFirstSymbols();
  Array<unsigned>counts = owner.coverage.getOptions().getSymbolCounts();
  unsigned hint = 0;
  for (Array<unsigned>
	 columns = combinadic.begin(strength),
	 symbols(strength);
       columns[strength - 1] < limit;
       combinadic.next(columns), ++hint) {
    for (unsigned i = strength; i--;) {
      if (columns[i] == option) {
	for (unsigned j = strength; j--;) {
	  symbols[j] = owner(row, columns[j]);
	}
	InputKnown oldKnown(symbols);
	if ((*owner.solver)(oldKnown) &&
	    --owner.coverage.hintGet(hint, columns, firsts, counts, symbols) ==
	    0) {
	  --owner.coverageCount;
	  if (owner.trackingNoncoverage) {
	    Array<unsigned>separateCopyOfSymbols(symbols.getSize());
	    for (unsigned j = symbols.getSize(); j--;) {
	      separateCopyOfSymbols[j] = symbols[j];
	    }
	    bool successfulInsertion =
	      owner.noncoverage->insert(separateCopyOfSymbols).second;
	    assert(successfulInsertion);
	    (void)successfulInsertion; // This is unused without assertions.
	  }
	} else {
	  --owner.multipleCoverageCount;
	}
	symbols[i] = value;
	InputKnown newKnown(symbols);
	if ((*owner.solver)(newKnown) &&
	    ++owner.coverage.hintGet(hint, columns, firsts, counts, symbols) ==
	    1) {
	  ++owner.coverageCount;
	  if (owner.trackingNoncoverage) {
	    Array<unsigned>separateCopyOfSymbols(symbols.getSize());
	    for (unsigned j = symbols.getSize(); j--;) {
	      separateCopyOfSymbols[j] = symbols[j];
	    }
	    bool successfulErasure =
	      (bool)owner.noncoverage->erase(separateCopyOfSymbols);
	    assert(successfulErasure);
	    (void)successfulErasure; // This is unused without assertions.
	  }
	} else {
	  ++owner.multipleCoverageCount;
	}
	break;
      }
    }
  }
  owner.autoFinalizeSubstitutions();
}

CoveringArray::Entry::operator unsigned() const {
  map<pair<unsigned, unsigned>, unsigned>::const_iterator
    substitution =
      owner.substitutions->find(pair<unsigned, unsigned>(row,option)),
    end = owner.substitutions->end();
  return (substitution == end) ?
    owner.array[row][option] :
    substitution->second;
}

CoveringArray::Entry&CoveringArray::Entry::operator =(unsigned value) {
  if (owner.trackingCoverage) {
    updateTracking(value);
  }
  (*owner.substitutions)[pair<unsigned, unsigned>(row, option)] = value;
  return *this;
}
