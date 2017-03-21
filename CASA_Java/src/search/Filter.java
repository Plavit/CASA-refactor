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

import java.util.Set;

/**
 * Controls which children (immediately reachable neighbors) are explored by a
 * search. The filtering happens after children are enumerated; limitations that
 * apply to the enumeration process itself (such as only computing a fixed
 * number of random children) are better managed by the parameters in a
 * SearchConfiguration, which are passed to the getChildren() method of the
 * StateSpace.
 */

public class Filter {

    private Set<CoveringArray> children;
    private Heuristic heuristic;
    private Goal<CoveringArray> goal;

    public Filter() {
    }

    /**
     * Mutates the children set; the heuristic and goal may guide the mutation.
     */
    public Filter(Set<CoveringArray> children, Heuristic heuristic, Goal<CoveringArray> goal) {
        this.children = children;
        this.heuristic = heuristic;
        this.goal = goal;
    }

    /**
     * Just as the other operator(), but, at the filter's option, the parent may
     * be considered to be its own child.  This is useful, for example, when the
     * SearchConfiguration is sampling a random subset of the children and the
     * filter would prefer a different pool to choose from.
     */
    public Filter(Set<CoveringArray> children,CoveringArray parent, Heuristic heuristic, Goal<CoveringArray> goal) {
        this.children = children;
        this.children.add(parent);
        this.heuristic = heuristic;
        this.goal = goal;
    }

    /** ---- GETTERS AND SETTERS ---- **/

    public Set<CoveringArray> getChildren() {
        return children;
    }

    public void setChildren(Set<CoveringArray> children) {
        this.children = children;
    }

    public Heuristic getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    public Goal<CoveringArray> getGoal() {
        return goal;
    }

    public void setGoal(Goal<CoveringArray> goal) {
        this.goal = goal;
    }
}
