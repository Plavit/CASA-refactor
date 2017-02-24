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


#include "covering/space/CoveringArraySpace.H"

CoveringArraySpace::CoveringArraySpace(unsigned strength, Options options) :
  strength(strength), options(options) {
  assert(strength <= options.getSize());
  for (unsigned i = options.getSize(); i--;) {
    // The clause atLeast requires that each column be filled.
    InputClause atLeast;
    for (unsigned
	   j = options.getFirstSymbol(i),
	   limit = options.getLastSymbol(i);
	 j <= limit;
	 ++j) {
      atLeast.append(InputTerm(false, j));
    }
    solver.addClause(atLeast);
    // The clauses atMost require that each pairing of symbols from an option
    // show at least one absence.
    for (unsigned
	   j = options.getFirstSymbol(i),
	   limit = options.getLastSymbol(i);
	 j <= limit; ++j) {
      for (unsigned k = j + 1; k <= limit; ++k) {
	InputClause atMost;
	atMost.append(InputTerm(true, j));
	atMost.append(InputTerm(true, k));
	solver.addClause(atMost);
      }
    }
  }
}

unsigned CoveringArraySpace::computeTargetCoverage() const {
  unsigned result = 0;
  for (Array<unsigned>columns = combinadic.begin(strength);
       columns[strength - 1] < options.getSize();
       combinadic.next(columns)) {
    // Initialization:
    Array<InputTerm>combination(columns.getSize());
    for (unsigned i = columns.getSize(); i--;) {
      combination[i] = InputTerm(false, options.getFirstSymbol(columns[i]));
    }
    // Body:
  loop:
    {
      InputKnown known(combination);
      if (solver(known)) {
	++result;
      }
    }
    // Advance:
    for (unsigned i = columns.getSize(); i--;) {
      unsigned next = combination[i].getVariable() + 1;
      if (next <= options.getLastSymbol(columns[i])) {
	combination[i] = InputTerm(false, next);
	goto loop;
      }
      combination[i] = InputTerm(false, options.getFirstSymbol(columns[i]));
    }
  }
  return result;
}
