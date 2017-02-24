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
#include "CombinadicIterator.H"

using namespace std;

void CoveringArray::SubRow::updateTracking(const Array<unsigned>values) {
  assert(values.getSize() == columns.getSize());
  const Options&options = owner.coverage.getOptions();
  unsigned strength = owner.coverage.getStrength();
  unsigned limit = options.getSize();
  unsigned changes = 0;
  Array<unsigned>oldRow(limit);
  Array<unsigned>newRow(limit);
  Array<unsigned>changedColumns(columns.getSize());
  for (unsigned i = limit; i--;) {
    oldRow[i] = owner(row, i);
  }
  for (unsigned i = limit, j = values.getSize(); i--;) {
    if (j && (columns[j - 1] == i)) {
      newRow[i] = values[--j];
    } else {
      newRow[i] = oldRow[i];
    }
  }
  for (unsigned i = 0; i < limit; ++i) {
    if (newRow[i] != oldRow[i]) {
      changedColumns[changes++] = i;
    }
  }
  changedColumns = Array<unsigned>(changedColumns, changes);
  Array<unsigned>firsts = options.getFirstSymbols();
  Array<unsigned>counts = options.getSymbolCounts();
  for (CombinadicIterator combo(limit, strength, changedColumns);
       combo;
       ++combo) {
    const Array<unsigned>updateColumns = *combo;
    Array<unsigned>oldSymbols(strength);
    Array<unsigned>newSymbols(strength);
    for (unsigned j = strength; j--;) {
      unsigned column = updateColumns[j];
      oldSymbols[j] = oldRow[column];
      newSymbols[j] = newRow[column];
    }
    Coverage<unsigned>::Entry lost =
      owner.coverage.hintGet(updateColumns, firsts, counts, oldSymbols);
    assert(lost); // Assert that what we lost is something we had to lose.
    if (--lost == 0) {
      --owner.coverageCount;
      if (owner.trackingNoncoverage) {
	Array<unsigned>separateCopyOfSymbols(oldSymbols.getSize());
	for (unsigned j = oldSymbols.getSize(); j--;) {
	  separateCopyOfSymbols[j] = oldSymbols[j];
	}
	bool successfulInsertion =
	  owner.noncoverage->insert(separateCopyOfSymbols).second;
	assert(successfulInsertion);
	(void)successfulInsertion; // This is unused without assertions.
      }
    } else {
      --owner.multipleCoverageCount;
    }
    Coverage<unsigned>::Entry gained =
      owner.coverage.hintGet(updateColumns, firsts, counts, newSymbols);
    if (++gained == 1) {
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

CoveringArray::SubRow::operator Array<unsigned>() const {
  typedef map<pair<unsigned, unsigned>, unsigned>::const_iterator Substitution;
  Substitution end = owner.substitutions->end();
  unsigned size = columns.getSize();
  Array<unsigned>result(size);
  pair<unsigned, unsigned>key(row,0);
  for (unsigned i = size; i--;) {
    key.second = columns[i];
    Substitution substitution = owner.substitutions->find(key);
    result[i] =
      (substitution == end) ?
      owner.array[row][key.second] :
      substitution->second;
  }
  return result;
}
CoveringArray::SubRow&CoveringArray::SubRow::operator =
  (const Array<unsigned>values) {
  assert(values.getSize() == columns.getSize());
  if (owner.trackingCoverage) {
    updateTracking(values);
  }
  pair<unsigned, unsigned>key(row, 0);
  for (unsigned i = columns.getSize(); i--;) {
    key.second = columns[i];
    (*owner.substitutions)[key] = values[i];
  }
  return *this;
}
