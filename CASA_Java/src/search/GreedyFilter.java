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

import java.util.Iterator;
import java.util.Set;
import covering.cost.CoverageCost;
import covering.state.CoveringArray;

/**
 * A specialization of Filter where only the heuristically best child is
 * explored.  Furthermore, if given the option to treat the parent as a child,
 * the filter will only have the search explore children if at least one of them
 * is heuristically better than the parent.
 */

public class GreedyFilter extends Filter /* implements Comparable<CoverageCost> */ {

    public void filter(Set<CoveringArray> children, Heuristic heuristic, Goal<CoveringArray> goal) {

        // TODO - LAST ELEMENT NEEDED - BETTER SOLUTION AVAILABLE ??
        CoveringArray best = (CoveringArray) children.toArray()[children.size()-1];

        CoverageCost score = new CoverageCost();

        for(CoveringArray element : children){
            CoverageCost estimate = heuristic.estimate(element, goal);
            if(best.equals(children.toArray()[children.size()-1]) || estimate.compareTo(score) == -1){
                best = element;
                score = estimate;
            }
        }

        for(Iterator<CoveringArray> iter = children.iterator(); iter.hasNext();) {
            CoveringArray state = iter.next();
            if(state != best) children.remove(state);
        }

    }
}
