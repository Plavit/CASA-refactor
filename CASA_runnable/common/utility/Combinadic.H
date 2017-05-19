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


#ifndef COMBINADIC_H
#define COMBINADIC_H

#include <math.h>

#include "Array.H"
#include "PascalTriangle.H"

class Combinadic {
protected:
  unsigned guessLastMember(unsigned encoding, unsigned cardinality);
  std::pair<unsigned, unsigned>getLastMemberAndContribution
    (unsigned encoding, unsigned cardinality);
public:
  unsigned encode(Array<unsigned>sortedSubset);
  Array<unsigned>decode(unsigned encoding, unsigned cardinality);
  Array<unsigned>begin(unsigned size) const;
  void previous(Array<unsigned>sortedSubset) const;
  void next(Array<unsigned>sortedSubset) const;
};

extern Combinadic combinadic;

#endif
