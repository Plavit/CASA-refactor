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


#include <algorithm>

#include "utility/igreater.H"

#include "covering/space/GraftSpace.H"

using namespace std;

#ifndef MAXIMUM_SUBROW_CHANGE_FAILED_ATTEMPTS
#define MAXIMUM_SUBROW_CHANGE_FAILED_ATTEMPTS 32
#endif

Array<unsigned>GraftSpace::createRandomMatchingRow(InputKnown&known) const {
  Array<unsigned>result(options.getSize());
  for (unsigned i = options.getSize(); i--;) {
    vector<unsigned>candidates;
    for (unsigned
	   j = options.getSymbolCount(i),
	   base = options.getFirstSymbol(i);
	 j--;) {
      candidates.push_back(base + j);
    }
    unsigned index = rand() % candidates.size();
    unsigned symbol = candidates[index];
    known.append(InputTerm(false, symbol));
    while (!solver(known)) {
      known.undoAppend();
      candidates[index] = candidates[candidates.size() - 1];
      candidates.pop_back();
      index = rand() % candidates.size();
      symbol = candidates[index];
      known.append(InputTerm(false, symbol));
    }
    result[i] = symbol;
  }
  return result;
}

CoveringArray GraftSpace::createStartState(unsigned rows) const {
  CoveringArray result(rows, strength, options, solver);
  unsigned i = rows;
  while (i-->resurrectionBuffer.size()) {
    InputKnown known;
    result.setBackingArrayRow(i, createRandomMatchingRow(known));
  }
  if (resurrectionBuffer.size()) {
    for (++i;i--;) {
      Array<unsigned>row = resurrectionBuffer[i];
      // We must deep copy to preserve the resurrection buffer.
      for (unsigned j = options.getSize(); j--;) {
	result.setBackingArrayEntry(i, j, row[j]);
      }
    }
  }
  result.setTrackingCoverage(true);
  result.setTrackingNoncoverage(true);
  return result;
}

CoverageCost GraftSpace::getTraveled(const CoveringArray&start) const {
  return 0;
}

CoverageCost GraftSpace::getTraveled
  (const Node<CoveringArray, CoverageCost>&parent,const CoveringArray&state)
  const {
  return 0;
}

set<CoveringArray>GraftSpace::getChildren
  (const CoveringArray&state, float proportion) const {
  assert(false);  // Unimplemented.
  return getChildren(state, (unsigned)1);
}

set<CoveringArray>GraftSpace::getChildren
  (const CoveringArray&state, unsigned count) const {
  set<CoveringArray>result;
  unsigned rows = state.getRows();
  assert(options.getSize() == state.getOptions());
  assert(state.isTrackingNoncoverage());
  const set<Array<unsigned>, ArrayComparator<unsigned> >&noncoverage =
    state.getNoncoverage();
  if (noncoverage.empty()){
    return result;
  }
  unsigned attempts = 0;
  for (unsigned i = count; i; ++attempts) {
    unsigned row = rand() % rows;
    set<Array<unsigned>, ArrayComparator<unsigned> >::const_iterator
      combinationIterator = noncoverage.begin();
    unsigned combinationIndex = rand() % noncoverage.size();
    while (combinationIndex--) {
      ++combinationIterator;
    }
    Array<unsigned>combination = *combinationIterator;
    Array<unsigned>columns(strength);
    InputKnown known;
    if (attempts < MAXIMUM_SUBROW_CHANGE_FAILED_ATTEMPTS) {
      for (unsigned j = strength; j--;) {
	columns[j] = options.getOption(combination[j]);
      }
      for (unsigned j = options.getSize(), k = strength; j--;) {
	if (k && columns[k-1] == j) {
	  unsigned symbol = combination[--k];
	  known.append(InputTerm(false, symbol));
	} else {
	  assert(!k || columns[k - 1] < j);
	  unsigned symbol = state(row, j);
	  known.append(InputTerm(false, symbol));
	}
      }
      if (solver(known)) {
	CoveringArray child = state;
	child(row, columns) = combination;
	result.insert(child);
	--i;
	attempts = 0;
      }
    } else {
      cout << "Considering a full row change" << endl;
      for (unsigned k = strength; k--;) {
	known.append(InputTerm(false, combination[k]));
      }
      CoveringArray child = state;
      child(row) = createRandomMatchingRow(known);
      result.insert(child);
      --i;
      attempts = 0;
    }
  }
  return result;
}

void GraftSpace::signal(const SearchFinish<CoveringArray, CoverageCost>&finish) {
  const set<const Node<CoveringArray, CoverageCost>*>&best =
    finish.source.getBest();
  if (best.empty()) {
    return;
  }
  const CoveringArray&solution = (*best.begin())->getState();
  unsigned rowCount = solution.getRows();
  unsigned optionCount = solution.getOptions();
  resurrectionBuffer.reserve(rowCount);
  while (resurrectionBuffer.size() < rowCount) {
    resurrectionBuffer.push_back(Array<unsigned>());
  }
  cout << "Sorting rows for distinct coverage" << endl;
  Array<unsigned>distinctCoverage = solution.countDistinctCoverage();
  Array<unsigned>permutation(rowCount);
  for (unsigned i = rowCount; i--;) {
    permutation[i] = i;
  }
  igreater<unsigned>betterDistinctCoverage(distinctCoverage);
  sort(permutation + 0, permutation + rowCount, betterDistinctCoverage);
  cout << "Saving rows in resurrection buffer" << endl;
  for (unsigned i = rowCount; i--;) {
    unsigned p = permutation[i];
    Array<unsigned>row = Array<unsigned>(optionCount);
    for (unsigned j = optionCount; j--;) {
      row[j] = solution(p, j);
    }
    resurrectionBuffer[i] = row;
  }
}

void GraftSpace::clearResurrectionBuffer() {
  resurrectionBuffer.clear();
}
