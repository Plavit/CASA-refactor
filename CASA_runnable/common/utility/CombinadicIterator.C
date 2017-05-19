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


#include <cassert>
#include <algorithm>

#include "CombinadicIterator.H"

using namespace std;

CombinadicIterator::CombinadicIterator
  (unsigned populationSize, unsigned sampleSize, Array<unsigned>relevant) :
  populationSize(populationSize),
  relevant(relevant),
  notRelevant(populationSize - relevant.getSize()),
  minimumRelevance(max((int)sampleSize - (int)notRelevant.getSize(), 1)),
  maximumRelevance(min(relevant.getSize(), sampleSize)),
  choiceFromRelevant(combinadic.begin(maximumRelevance)),
  choiceFromNotRelevant(combinadic.begin(sampleSize - maximumRelevance)),
  relevantCombination(sampleSize),
  combination(sampleSize) {
  assert(sampleSize <= populationSize);
  for (unsigned i = 0, j = 0, k = 0; i < notRelevant.getSize(); ++i, ++j) {
    while (k < relevant.getSize() && relevant[k] == j) {
      ++j;
      ++k;
    }
    notRelevant[i] = j;
  }
  updateCombinationFromRelevant();
  updateCombination();
}

void CombinadicIterator::updateCombinationFromRelevant() {
  for (unsigned i = maximumRelevance; i--;) {
    relevantCombination[i] = relevant[choiceFromRelevant[i]];
  }
}

void CombinadicIterator::updateCombination() {
  for (unsigned i = combination.getSize(); i-- > maximumRelevance;) {
    combination[i] = notRelevant[choiceFromNotRelevant[i - maximumRelevance]];
  }
  for (unsigned i = maximumRelevance; i--;) {
    combination[i] = relevantCombination[i];
  }
  sort(combination + 0, combination + combination.getSize());
}

const Array<unsigned>CombinadicIterator::operator *() const {
#ifndef NDEBUG
  for (unsigned i = combination.getSize(); --i;) {
    assert(combination[i - 1] < combination[i]);
  }
#endif
  return combination;
}

CombinadicIterator::operator bool() const {
  return combination.getSize();
}

CombinadicIterator&CombinadicIterator::operator ++() {
  if (!combination.getSize()) {
    return *this;
  }
  bool someFromNotRelevant = choiceFromNotRelevant.getSize();
  if (someFromNotRelevant) {
    combinadic.next(choiceFromNotRelevant);
  }
  if (!someFromNotRelevant ||
      choiceFromNotRelevant[choiceFromNotRelevant.getSize() - 1] >=
      populationSize - relevant.getSize()) {
    combinadic.next(choiceFromRelevant);
    if (choiceFromRelevant[maximumRelevance - 1] >= relevant.getSize()) {
      --maximumRelevance;
      if (maximumRelevance < minimumRelevance) {
	combination = Array<unsigned>(0);
	return *this;
      }
      choiceFromRelevant = combinadic.begin(maximumRelevance);
    }
    updateCombinationFromRelevant();
    choiceFromNotRelevant =
      combinadic.begin(combination.getSize() - maximumRelevance);
  }
  updateCombination();
  return *this;
}
