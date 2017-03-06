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


/**
 * Decides the order in which states are explored.  Usual policies include
 * visiting the heuristically best states first, visiting states whose distance
 * from the start state added to the heuristic is minimal, visiting states
 * randomly, etc.  There is provision to treat start states specially.
 */
public interface Guide<STATE extends Comparable<STATE>, COST> {
    COST rankStart(final Node<STATE, COST> start);
    COST rank(final Node<STATE, COST> node);
};
