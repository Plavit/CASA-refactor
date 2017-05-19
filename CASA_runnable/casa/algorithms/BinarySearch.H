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


#ifndef BINARY_SEARCH_H
#define BINARY_SEARCH_H

class Partitioner {
public:
  virtual ~Partitioner() {}
  virtual unsigned operator ()(unsigned offset, unsigned size) = 0;
};

class ExpensiveUnaryPredicate {
public:
  virtual ~ExpensiveUnaryPredicate() {}
  virtual bool operator ()(unsigned index) const = 0;
};

class BinarySearch {
protected:
  const ExpensiveUnaryPredicate&	predicate;
  Partitioner&				partitioner;
public:
  BinarySearch
    (const ExpensiveUnaryPredicate&predicate, Partitioner&partitioner) :
      predicate(predicate),
      partitioner(partitioner) {}
  virtual ~BinarySearch() {}
  // Returns the smallest index for which the predicate returns true, using the
  // partitioner to reduce the comparison cost.
  unsigned operator ()(unsigned offset, unsigned size);
};

#endif
