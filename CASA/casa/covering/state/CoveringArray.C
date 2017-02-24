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

#ifndef MAXIMUM_COVERING_ARRAY_SUBSTITUTION
#define MAXIMUM_COVERING_ARRAY_SUBSTITUTION 0x40
#endif

CoveringArray::CoveringArray
  (unsigned rows, unsigned strength, Options options, SATSolver&solver) :
    array(rows),
    substitutions(new map<pair<unsigned, unsigned>, unsigned>()),
    solver(&solver),
    trackingCoverage(false),
    trackingNoncoverage(false),
    coverageCount(0),
    multipleCoverageCount(0),
    coverage(strength, options),
    noncoverage(new set<Array<unsigned>, ArrayComparator<unsigned> >()) {
  for (unsigned i = rows; i--;) {
    array[i] = Array<unsigned>(options.getSize());
  }
  coverage.fill(0);
}

CoveringArray::CoveringArray(const CoveringArray&copy) :
  array(copy.array),
  substitutions(copy.substitutions),
  solver(copy.solver),
  trackingCoverage(copy.trackingCoverage),
  trackingNoncoverage(copy.trackingNoncoverage),
  coverageCount(copy.coverageCount),
  multipleCoverageCount(copy.multipleCoverageCount),
  coverage(copy.coverage),
  noncoverage(copy.noncoverage) {
  assert(array);
}

void CoveringArray::setBackingArrayEntry
  (unsigned row, unsigned option, unsigned value) {
  assert(!substitutions->size());
  array[row][option] = value;
}

void CoveringArray::setBackingArrayRow(unsigned row, Array<unsigned>value) {
  assert(!substitutions->size());
  array[row] = value;
}

unsigned CoveringArray::getCoverageCount() const {
  return coverageCount;
}

unsigned CoveringArray::getMultipleCoverageCount() const {
  return multipleCoverageCount;
}

Array<unsigned>CoveringArray::countDistinctCoverage() const {
  assert(trackingCoverage);
  Array<unsigned>result(array.getSize());
  result.fill(0);
  unsigned strength = coverage.getStrength();
  unsigned limit = coverage.getOptions().getSize();
  Array<unsigned>firsts = coverage.getOptions().getFirstSymbols();
  Array<unsigned>counts = coverage.getOptions().getSymbolCounts();
  unsigned hint = 0;
  for (Array<unsigned>
	 columns = combinadic.begin(strength),
	 symbols(strength);
       columns[strength - 1]<limit;
       combinadic.next(columns), ++hint) {
    for (unsigned i = array.getSize(); i--;) {
      for (unsigned j = strength; j--;) {
	symbols[j] = (*this)(i, columns[j]);
      }
      if (coverage.hintGet(hint, columns, firsts, counts, symbols) == 1) {
	++result[i];
      }
    }
  }
  return result;
}

bool CoveringArray::operator <(const CoveringArray&other) const {
  return this < &other;
}
bool CoveringArray::operator >(const CoveringArray&other) const {
  return this > &other;
}
bool CoveringArray::operator ==(const CoveringArray&other) const {
  return this == &other;
}
bool CoveringArray::operator !=(const CoveringArray&other) const {
  return this != &other;
}

void CoveringArray::finalizeSubstitutions() {
  unsigned outer = getRows();
  unsigned inner = getOptions();
  Array<Array<unsigned> >replacement(outer);
  for (unsigned i = outer; i--;) {
    replacement[i] = Array<unsigned>(inner);
    for (unsigned j = inner; j--;) {
      replacement[i][j] = array[i][j];
    }
  }
  for (map<pair<unsigned, unsigned>, unsigned>::const_iterator
	 iterator = substitutions->begin(),
	 end = substitutions->end();
       iterator != end;
       ++iterator) {
    const pair<unsigned, unsigned>&location = iterator->first;
    replacement[location.first][location.second] = iterator->second;
  }
  substitutions->clear();
  array = replacement;
}

void CoveringArray::autoFinalizeSubstitutions() {
  if (substitutions->size() > MAXIMUM_COVERING_ARRAY_SUBSTITUTION) {
    finalizeSubstitutions();
  }
}

bool CoveringArray::isTrackingCoverage() const {
  return trackingCoverage;
}

void CoveringArray::setTrackingCoverage(bool trackingCoverage) {
  if (this->trackingCoverage) {
    this->trackingCoverage = trackingCoverage;
    return;
  }
  this->trackingCoverage = trackingCoverage;
  if (trackingCoverage) {
    unsigned strength = coverage.getStrength();
    unsigned limit = coverage.getOptions().getSize();
    Array<unsigned>firsts = coverage.getOptions().getFirstSymbols();
    Array<unsigned>counts = coverage.getOptions().getSymbolCounts();
    coverage.fill(0);
    coverageCount = 0;
    multipleCoverageCount = 0;
    if (substitutions->size()) {
      unsigned hint = 0;
      for (Array<unsigned>
	     columns = combinadic.begin(strength),
	     symbols(strength);
	   columns[strength - 1] < limit;
	   combinadic.next(columns), ++hint) {
	for (unsigned i = array.getSize();i--;) {
	  for (unsigned j = strength;j--;) {
	    symbols[j] = (*this)(i, columns[j]);
	  }
	  unsigned newCoverage =
	    ++coverage.hintGet(hint, columns, firsts, counts, symbols);
	  if (newCoverage == 1) {
	    ++coverageCount;
	  }
	  if (newCoverage>1) {
	    ++multipleCoverageCount;
	  }
	}
      }
    } else {
      // A special common case where we can bypass the () operator:
      unsigned hint = 0;
      for (Array<unsigned>
	     columns = combinadic.begin(strength),
	     symbols(strength);
	   columns[strength - 1] < limit;
	   combinadic.next(columns), ++hint) {
	for (unsigned i = array.getSize(); i--;) {
	  for (unsigned j = strength; j--;) {
	    symbols[j] = array[i][columns[j]];
	  }
	  unsigned newCoverage =
	    ++coverage.hintGet(hint, columns, firsts, counts, symbols);
	  if (newCoverage == 1) {
	    ++coverageCount;
	  }
	  if (newCoverage>1) {
	    ++multipleCoverageCount;
	  }
	}
      }
    }
  }
}

bool CoveringArray::isTrackingNoncoverage() const {
  return trackingNoncoverage;
}

void CoveringArray::setTrackingNoncoverage(bool trackingNoncoverage) {
  if (this->trackingNoncoverage) {
    this->trackingNoncoverage = trackingNoncoverage;
    if (!trackingNoncoverage) {
      noncoverage->clear();
    }
    return;
  }
  this->trackingNoncoverage = trackingNoncoverage;
  if (trackingNoncoverage) {
    assert(trackingCoverage);
    assert(noncoverage->empty());
#ifndef NDEBUG
    unsigned impossible = 0;
#endif
    for (Coverage<unsigned>::iterator
	   iterator = coverage.begin(),
	   end = coverage.end();
	 iterator != end;
	 ++iterator) {
      if (!*iterator) {
	InputKnown known;
	Array<unsigned>combination = iterator.getCombination();
	for (unsigned i = combination.getSize(); i--;) {
	  known.append(InputTerm(false, combination[i]));
	}
	if ((*solver)(known)) {
	  noncoverage->insert(combination);
	}
#ifndef NDEBUG
	else {
	  ++impossible;
	}
#endif
      }
    }
    assert(coverageCount + noncoverage->size() + impossible ==
	   coverage.getSize());
  }
}

const set<Array<unsigned>, ArrayComparator<unsigned> >&
  CoveringArray::getNoncoverage() const {
  return *noncoverage;
}

ostream&operator <<(ostream&out, const CoveringArray&array) {
  out << '{';
  for (unsigned i = 0; i < array.getRows(); ++i) {
    out << '[';
    for (unsigned j = 0; j < array.getOptions(); ++j) {
      out << array(i,j) << ',';
    }
    out << "X],";
  }
  out << "X} -> ";
  if (array.isTrackingCoverage()) {
    out << array.getCoverageCount();
  }else{
    out << '?';
  }
  return out;
}
