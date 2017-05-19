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

#include "Combinadic.H"

using namespace std;

static double TWO_PI = 2 * M_PI;
static double INVERSE_E = 1 / M_E;

// We want result to approximately satisfy
//  cardinality! * encoding = result * (result-1) * ... * (result-(cardinality-1))
// The right-hand side usually has degree >= 5, so we need to trivialize it a bit.
// It can be replaced with the overapproximation:
//  cardinality! * encoding = (result - (cardinality-1) / 2) ^ cardinality
// Factorials are also expensive, so we use Stirling's approximation:
//  sqrt(TWO_PI * cardinality) * ((cardinality / e) ^ cardinality) * encoding =
//    (result - (cardinality-1) / 2) ^ cardinality
// Now, to solve for result, take the cardinality'th root of both sides:
//  (cardinality / e) *
//  (encoding * sqrt(TWO_PI * cardinality)) ^ (1 / cardinality) =
//    result - (cardinality - 1)/2
// And then rearrange that and add a half to make the flooring round to nearest:
//  result =
//    (cardinality / e) *
//    (encoding * sqrt(TWO_PI * cardinality)) ^ (1 / cardinality) +
//    cardinality / 2 =
//      ((1 / e) *
//        (encoding * sqrt(TWO_PI * cardinality)) ^ (1 / cardinality) + 0.5) *
//      cardinality
unsigned Combinadic::guessLastMember(unsigned encoding, unsigned cardinality) {
  double scaledEncoding = encoding * sqrt(TWO_PI * cardinality);
  double rootOfEncoding = pow(scaledEncoding, 1.0 / cardinality);
  return (unsigned)((INVERSE_E * rootOfEncoding + 0.5) * (double)cardinality);
}

pair<unsigned, unsigned>Combinadic::getLastMemberAndContribution
  (unsigned encoding, unsigned cardinality) {
  unsigned member = guessLastMember(encoding, cardinality);
  unsigned contribution = triangle.nCr(member, cardinality);
  if (contribution > encoding) {
    do {
      contribution = triangle.nCr(--member, cardinality);
    } while (contribution > encoding);
  } else {
    unsigned nextContribution = triangle.nCr(member + 1, cardinality);
    while (nextContribution <= encoding) {
      ++member;
      contribution = nextContribution;
      nextContribution = triangle.nCr(member + 1, cardinality);
    }
  }
  return pair<unsigned, unsigned>(member, contribution);
}

unsigned Combinadic::encode(Array<unsigned>sortedSubset) {
  unsigned result = 0;
  for (unsigned i = 0; i < sortedSubset.getSize(); ++i) {
    result += triangle.nCr(sortedSubset[i], i + 1);
  }
  return result;
}

Array<unsigned>Combinadic::decode(unsigned encoding, unsigned cardinality) {
  Array<unsigned>result(cardinality);
  for (unsigned i = cardinality; i;) {
    pair<unsigned, unsigned>memberAndContribution =
      getLastMemberAndContribution(encoding, i);
    result[--i] = memberAndContribution.first;
    encoding -= memberAndContribution.second;
  }
  return result;
}

Array<unsigned>Combinadic::begin(unsigned size) const {
  Array<unsigned>result(size);
  for(unsigned i = size; i-- ;) {
    result[i]=i;
  }
  return result;
}

void Combinadic::previous(Array<unsigned>sortedSubset) const {
  assert(sortedSubset.getSize());
  unsigned limit = sortedSubset.getSize();
  for(unsigned i = 0; i < limit; ++i) {
    unsigned entry = sortedSubset[i];
    if (entry > i) {
      do {
	sortedSubset[i] = --entry;
      } while (i-- > 0);
      return;
    }
  }
}

void Combinadic::next(Array<unsigned>sortedSubset) const {
  assert(sortedSubset.getSize());
  unsigned limit = sortedSubset.getSize() - 1, ceiling = sortedSubset[0];
  for (unsigned i = 0; i < limit; ++i) {
    unsigned entry = ceiling + 1;
    ceiling = sortedSubset[i + 1];
    if (entry < ceiling) {
      sortedSubset[i] = entry;
      return;
    }
    sortedSubset[i] = i;
  }
  ++sortedSubset[limit];
}

Combinadic combinadic;
