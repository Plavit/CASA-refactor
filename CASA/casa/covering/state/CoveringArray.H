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


#ifndef COVERING_ARRAY_H
#define COVERING_ARRAY_H

#include <cassert>
#include <iostream>
#include <set>
#include <map>

#include "Lazy.H"
#include "Array.H"

#include "covering/bookkeeping/Coverage.H"

#include "sat/SAT.H"


class CoveringArray {
protected:
  // The first index is the test configuration (row).
  // The second index is the option (column).
  Array<Array<unsigned> >		array;
  Lazy<std::map<std::pair<unsigned, unsigned>, unsigned> >
					substitutions;

  SATSolver*				solver;

  bool					trackingCoverage;
  bool					trackingNoncoverage;

  unsigned				coverageCount;
  unsigned				multipleCoverageCount;
  Coverage<unsigned>			coverage;
  Lazy<std::set<Array<unsigned>, ArrayComparator<unsigned> > >
					noncoverage;

public:
  CoveringArray
    (unsigned rows, unsigned strength, Options options, SATSolver&solver);
  CoveringArray(const CoveringArray&copy);

  unsigned getRows() const {
    return array.getSize();
  }
  unsigned getOptions() const {
    return array.getSize() ? array[0].getSize() : 0;
  }

  void setBackingArrayEntry(unsigned row, unsigned option, unsigned value);
  void setBackingArrayRow(unsigned row, Array<unsigned>value);

  class Entry {
  protected:
    CoveringArray&			owner;
    unsigned				row;
    unsigned				option;
  public:
    Entry(CoveringArray&owner, unsigned row, unsigned option) :
      owner(owner),
      row(row),
      option(option) {}
  protected:
    void updateTracking(unsigned value);
  public:
    operator unsigned() const;
    Entry&operator =(unsigned value);
  };

  // Warning: CoveringArray::Row assumes that constraints are always satisfied.
  class Row {
  protected:
    CoveringArray&			owner;
    unsigned				row;
  public:
    Row(CoveringArray&owner, unsigned row) :
      owner(owner),
      row(row) {}
  protected:
    void updateTracking(const Array<unsigned>values);
  public:
    operator Array<unsigned>() const;
    Row&operator =(const Array<unsigned>values);
  };

  // Warning: CoveringArray::SubRow assumes that constraints are always
  // satisfied.
  class SubRow {
  protected:
    CoveringArray&			owner;
    unsigned				row;
    Array<unsigned>			columns;
  public:
    SubRow(CoveringArray&owner, unsigned row, Array<unsigned>columns) :
      owner(owner),
      row(row),
      columns(columns) {}
  protected:
    void updateTracking(const Array<unsigned>values);
  public:
    operator Array<unsigned>() const;
    SubRow&operator =(const Array<unsigned>values);
  };

  Entry operator ()(unsigned row, unsigned option) {
    return Entry(*this, row, option);
  }
  const Entry operator ()(unsigned row, unsigned option) const {
    return Entry(*const_cast<CoveringArray*>(this), row, option);
  }

  Row operator ()(unsigned row) {
    return Row(*this, row);
  }
  const Row operator ()(unsigned row) const {
    return Row(*const_cast<CoveringArray*>(this), row);
  }

  SubRow operator ()(unsigned row, Array<unsigned>columns) {
    return SubRow(*this, row, columns);
  }
  const SubRow operator ()(unsigned row, Array<unsigned>columns) const {
    return SubRow(*const_cast<CoveringArray*>(this), row, columns);
  }

  unsigned getCoverageCount() const;
  unsigned getMultipleCoverageCount() const;
  Array<unsigned>countDistinctCoverage() const;

  bool operator <(const CoveringArray&other) const;
  bool operator >(const CoveringArray&other) const;
  bool operator ==(const CoveringArray&other) const;
  bool operator !=(const CoveringArray&other) const;

  void finalizeSubstitutions();
  void autoFinalizeSubstitutions();

  bool isTrackingCoverage() const;
  void setTrackingCoverage(bool trackingCoverage);

  bool isTrackingNoncoverage() const;
  void setTrackingNoncoverage(bool trackingNoncoverage);

  const std::set<Array<unsigned>, ArrayComparator<unsigned> >&getNoncoverage()
    const;
};

std::ostream&operator <<(std::ostream&out,const CoveringArray&array);

#endif
