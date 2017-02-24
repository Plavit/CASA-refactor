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


#ifndef STATE_SPACE_H
#define STATE_SPACE_H

#include <set>

#include "search/Node.H"

// A description of a state space, namely the state interconnections and
// distances (for pathfinding rather than statefinding).

template<class STATE, class COST>class StateSpace {
public:
  virtual ~StateSpace() {}

  // Determine the distance incurred by starting at the given state.
  virtual COST getTraveled(const STATE&start) const = 0;

  // Determine the total distance traveled to reach state after taking the best
  // known path reaching parent.
  virtual COST getTraveled(const Node<STATE, COST>&parent, const STATE&state)
    const = 0;

  // Enumerate a fixed fraction of the children of state, rounding up.
  virtual std::set<STATE>getChildren(const STATE&state, float proportion)
    const = 0;

  // Enumerate the children of state up to some limit.
  virtual std::set<STATE>getChildren(const STATE&state, unsigned count)
    const = 0;
};

#endif
