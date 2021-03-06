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
 * Controls behaviors of the search that are parameterized by value, not code.
 */
public class SearchConfiguration {
    /**
     * // Should we keep a history of where we've been?
     // Defaults to true.
     // If false:
     //  Nodes are always without parent, so the search cannot find paths.
     //  States that have been visited and expanded may be visited and expanded
     //    again.
     */
    boolean useClosed;

    /**
     * // Should we allow a parent to consider itself its own child?
     // Defaults to false.
     // If true:
     //  Node's states are forcibly added to their own sets of children.
     //  Filters can therefore choose the trivial (no-move) transition, like in
     //    hill-climbing.
     //  The search may spin at local maxima.
     */
    boolean retryChildren;

    /**
     * // Should we limit the number of children by a proportion rather than a count?
     // Defaults to true.
     // If true:
     //  The state space is asked to enumerate a fixed proportion of children.
     //  States with more children will have longer enumerations.
     // If false:
     //  The state space is asked to enumerate a fixed number of children.
     //  States with too few children will have all of them listed.
     //  States with too many children will not.
     */
    boolean proportionChildren;

    /**
     * // How many children should we ask for?
     // (See the documentation of proportionChildren above.)
      */
    ChildrenAsk childrenAsk;

    /**
     * // How many iterations before each pruning of the space?
     // Defaults to zero, which is treated as infinity.
     // If nonzero:
     //  After the given number of iterations, the search will forget all visits
     //    except for the best nodes in the closed and open sets.
     //  This is sometimes an appropriate way to trade performance for memory.
     */
    long prunePeriod;

    public SearchConfiguration() {
        this.useClosed = true;
        this.retryChildren = false;
        this.proportionChildren = true;
        this.prunePeriod = 0;
        this.childrenAsk.setProportion(0);
    }

    public SearchConfiguration(boolean useClosed, boolean retryChildren,float proportion, long prunePeriod) {
        this.useClosed = useClosed;
        this.retryChildren = retryChildren;
        this.proportionChildren = true;
        this.prunePeriod = prunePeriod;
        childrenAsk.setProportion(proportion);
    }

    public SearchConfiguration(boolean useClosed, boolean retryChildren,long count, long prunePeriod) {
        this.useClosed = useClosed;
        this.retryChildren = retryChildren;
        this.proportionChildren = false;
        this.prunePeriod = prunePeriod;
        childrenAsk.setCount(count);
    }


}
