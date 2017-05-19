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


#ifndef COMBINADIC_ITERATOR_H
#define COMBINADIC_ITERATOR_H

#include "Combinadic.H"

class CombinadicIterator {
protected:
  unsigned				populationSize;
  // The iteration will skip sets whose intersection with relevant is empty.
  Array<unsigned>			relevant;
  Array<unsigned>			notRelevant;

  unsigned				minimumRelevance;
  unsigned				maximumRelevance;

  Array<unsigned>			choiceFromRelevant;
  Array<unsigned>			choiceFromNotRelevant;

  Array<unsigned>			relevantCombination;
  Array<unsigned>			combination;
public:
  CombinadicIterator
    (unsigned populationSize, unsigned sampleSize, Array<unsigned>relevant);
protected:
  void updateCombinationFromRelevant();
  void updateCombination();
public:
  const Array<unsigned>operator *() const;
  operator bool() const;
  CombinadicIterator&operator ++();
};

#endif
