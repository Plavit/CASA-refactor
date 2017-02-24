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


#include <iostream>

#include "covering/space/CoveringArraySpace.H"
#include "annealing/AnnealingSuccess.H"

using namespace std;

bool AnnealingSuccess::operator ()(unsigned rows) const {
  bool result;
  cout << "Trying " << rows << " rows" << endl;
  const CoveringArraySpace*space =
    dynamic_cast<const CoveringArraySpace*>(search.getSpace());
  assert(space);
  cout << "Building start state" << endl;
  CoveringArray startState = space->createStartState(rows);
  search.addStartState(startState);
  cout << "Searching" << endl;
  set<NodeT*>solutions = search.search(iterations, false);
  if (solutions.empty()) {
    cout << "Failed to meet coverage with " << rows << " rows" << endl;
    result = false;
  } else {
    cout << "Met coverage with " << rows << " rows" << endl;
    result = true;
    solution = (*solutions.begin())->getState();
  }
  search.clear();
  return result;
}
