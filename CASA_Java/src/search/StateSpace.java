package search;

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


import covering.cost.CoverageCost;
import covering.state.CoveringArray;

import java.util.Set;

public interface StateSpace {
    // A description of a state space, namely the state interconnections and
    // distances (for pathfinding rather than statefinding).

    // Determine the distance incurred by starting at the given state.
    CoverageCost getTraveled(final CoveringArray start);

    // Determine the total distance traveled to reach state after taking the best
    // known path reaching parent.
    CoverageCost getTraveled(final Node parent, final CoveringArray state);

    // Enumerate a fixed fraction of the children of state, rounding up.
    Set<CoveringArray> getChildren(final CoveringArray state, float proportion);

    // Enumerate the children of state up to some limit.
    /* TODO: count was unsigned */
    Set<CoveringArray> getChildren(final CoveringArray state, int count);
};
