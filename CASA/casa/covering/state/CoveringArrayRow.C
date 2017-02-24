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

void CoveringArray::Row::updateTracking(const Array<unsigned>values) {
  unsigned size = values.getSize();
  assert(size == owner.getOptions());
  unsigned strength = owner.coverage.getStrength();
  unsigned limit = owner.coverage.getOptions().getSize();
  Array<unsigned>oldRow(size);
  for (unsigned i = size; i--;) {
    oldRow[i] = owner(row, i);
  }
  Array<unsigned>firsts = owner.coverage.getOptions().getFirstSymbols();
  Array<unsigned>counts = owner.coverage.getOptions().getSymbolCounts();
  unsigned hint = 0;
  for (Array<unsigned>
	 columns = combinadic.begin(strength),
	 oldSymbols(strength),
	 newSymbols(strength);
       columns[strength - 1] < limit;
       combinadic.next(columns), ++hint) {
    bool unchanged = true;
    for (unsigned j = strength; j--;) {
      unsigned column = columns[j];
      oldSymbols[j] = oldRow[column];
      newSymbols[j] = values[column];
      if (oldSymbols[j] != newSymbols[j]) {
	unchanged = false;
      }
    }
    if (unchanged) {
      continue;
    }
    InputKnown oldKnown(oldSymbols);
    InputKnown newKnown(newSymbols);
    if (--owner.coverage.hintGet(hint, columns, firsts, counts, oldSymbols) ==
	0) {
      --owner.coverageCount;
      if (owner.trackingNoncoverage) {
	Array<unsigned>separateCopyOfSymbols(oldSymbols.getSize());
	for (unsigned j = oldSymbols.getSize(); j--;) {
	  separateCopyOfSymbols[j] = oldSymbols[j];
	}
	bool successfulInsertion =
	  owner.noncoverage->insert(separateCopyOfSymbols).second;
	assert(successfulInsertion);
	(void)successfulInsertion; //This is unused without assertions.
      }
    } else {
      --owner.multipleCoverageCount;
    }
    if (++owner.coverage.hintGet(hint, columns, firsts, counts, newSymbols) ==
	1) {
      ++owner.coverageCount;
      if (owner.trackingNoncoverage) {
	Array<unsigned>separateCopyOfSymbols(newSymbols.getSize());
	for (unsigned j = newSymbols.getSize(); j--;) {
	  separateCopyOfSymbols[j] = newSymbols[j];
	}
	bool successfulErasure =
	  (bool)owner.noncoverage->erase(separateCopyOfSymbols);
	assert(successfulErasure);
	(void)successfulErasure; // This is unused without assertions.
      }
    } else {
      ++owner.multipleCoverageCount;
    }
  }
  owner.autoFinalizeSubstitutions();
}

CoveringArray::Row::operator Array<unsigned>() const {
  typedef map<pair<unsigned,unsigned>,unsigned>::const_iterator Substitution;
  Substitution end = owner.substitutions->end();
  Array<unsigned>result(owner.getOptions());
  for (pair<unsigned, unsigned>key(row, result.getSize()); key.second--;) {
    Substitution substitution = owner.substitutions->find(key);
    result[key.second] =
      (substitution == end) ?
      owner.array[row][key.second] :
      substitution->second;
  }
  return result;
}

CoveringArray::Row&CoveringArray::Row::operator =(const Array<unsigned>values) {
  if (owner.trackingCoverage) {
    updateTracking(values);
  }
  for (pair<unsigned, unsigned>key(row, owner.getOptions()); key.second--;) {
    (*owner.substitutions)[key] = values[key.second];
  }
  return *this;
}
