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

package search;

// A highly parameterized searching object.

// Clients of this code mostly need to understand the objects passed at
// construction time and, if pathfinding, fulfill the assumption that shortening
// a path prefix will shorten an entire path.  The complexity here is merely for
// careful bookkeeping and memory management.

// The general calling pattern is:
//  <constructor>
//  foreach search {
//    addStartState(...);
//    // Possibly more calls to addStartState(...)
//    search(...);
//    ... = getBest() // If we care about the best results
//    // Possibly more calls to search(...) if the search is restartable
//    clear();
//  }
//  <destructor>

import common.utility.Relation;
import covering.cost.CoverageCost;
import covering.state.CoveringArray;
import events.EventSource;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Search
{
    SearchConfiguration	    configuration;
    StateSpace				space;
    Heuristic				heuristic;
    Guide				    guide;
    Goal<CoveringArray>		goal;
    Filter				    filter;
    boolean					oneBest;
    Relation                open;
    Relation                closed;
    Set<Node>               best;
    CoverageCost            bestRank;

    // See the classes SearchConfiguration, StateSpace, Heuristic, Guide, Goal,
    // and Filter for documentation on their role in search.  The parameter
    // oneBest does not affect the method of search, but merely how many solutions
    // are remembered in the case of a tie in the guide's ranking.
    public Search(SearchConfiguration configuration, StateSpace space, Heuristic heuristic, Guide guide, Goal<CoveringArray> goal, Filter filter, boolean oneBest) {
        this.configuration = configuration;
        this.space = space;
        this.heuristic = heuristic;
        this.guide = guide;
        this.goal = goal;
        this.filter = filter;
        this.oneBest = oneBest;
    }

    public SearchConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(SearchConfiguration configuration) {
        this.configuration = configuration;
    }

    public StateSpace getSpace() {
        return space;
    }

    public void setSpace(StateSpace space) {
        this.space = space;
    }

    public Heuristic getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    public Guide getGuide() {
        return guide;
    }

    public void setGuide(Guide guide) {
        this.guide = guide;
    }

    public Goal<CoveringArray> getGoal() {
        return goal;
    }

    public void setGoal(Goal<CoveringArray> goal) {
        this.goal = goal;
    }

    public Set<Node> getBest() {
        return best;
    }

    public void clear() {
        clearBest();
        open.clear();
        closed.clear();
    }

    //Add a newly seen node to the set of seen but not yet explored nodes.
    private void addNew(Node node) {
        CoverageCost rank = guide.rank(node);
        open.key_insert(node,rank);
        updateBest(node,rank);
    }

    // A leak-free way to clear the set of best nodes.
    private  void clearBest() {
        if (!configuration.useClosed) {
            for (Node n : best) {
                if (open.key_find(n) == open.by_key.end()) {
                    delete node;
                }
            }
        }
        best.clear();
    }


    // See if the given node with the given guide ranking should be entered into
    // the set of best nodes.
    private void updateBest(final Node node, CoverageCost rank) {
        if (rank.compareTo(bestRank) < 0) {
            clearBest();
        }
        if (best.isEmpty()) {
            best.add(node);
            bestRank = rank;
        } else if (rank == bestRank) {
            if (oneBest) clearBest();
            best.add(node);
        }
    }

    // Add a start state before searching.
    public void addStartState(final CoveringArray start) {
        Node node = new Node(null, start, space->getTraveled(start), heuristic->estimate(start, goal));
        addNew(node);
    }

    public EventSource<SearchIteration> asSearchIterationEventSource() {
        return new EventSource<SearchIteration>() {
            /* TODO */
        };
    }

    public EventSource<SearchFinish> asSearchFinishEventSource() {
        return new EventSource<SearchFinish>() {
            /* TODO */
        };
    }

    // Pop the best ranked node from the set of nodes that have been seen but not
    // explored.
    private Node popBestOpen() {
        Iterator<Map.Entry<CoverageCost, Node>> dataEntries = open.getDataEntries().iterator();
        assert (!dataEntries.hasNext());
        Map.Entry<CoverageCost, Node> bestOpen = dataEntries.next();
        Node bestOpenNode = bestOpen.getValue();
        if (configuration.useClosed) {
            closed.key_insert(bestOpenNode, bestOpen.getKey());
        }
        dataEntries.remove();
        return bestOpenNode;
    }

    // Get the children (immediately reachable neighbors) according to the
    // SearchConfiguration.
    private Set<CoveringArray> getChildren(Node parent) {
        if (configuration.proportionChildren) {
            return space.getChildren(parent.getState(), configuration.childrenAsk.getProportion());
        }
        return space.getChildren(parent.getState(), configuration.childrenAsk.getCount());
    }

    // Return true if a node should be discarded because we have seen a better one
    // representing the same state, but not explored it yet.  Also, forget about
    // any nodes that we have seen but not explored if they represent the same
    // state but are worse.
    private boolean replaceInOpen(Node parent, Node node, CoverageCost traveled) {
        Iterator<Map.Entry<Node, CoverageCost>> similar = open.key_find(node).iterator();
        if (similar.hasNext()) {
            // The node does not have an already seen state.
            return false;
        }
        Map.Entry<Node, CoverageCost> similarEntry = similar.next();
        Node visited = similarEntry.getKey();
        if (visited.getTraveled().compareTo(traveled) <= 0) {
            // The node has an already seen state and cannot improve a path; discard it.
            return true;
        }
        // The node has an already seen state, but may improve some paths; use it
        // instead.
        if (configuration.useClosed) {
            parent.addChild(visited);
        }
        visited.setTraveled(traveled);
        /* TODO: examine what should be removed here (single node or all nodes) */
        similar.remove();
        CoverageCost rank = guide.rank(visited);
        open.key_insert(visited, rank);
        updateBest(visited, rank);
        return true;
    }
}
/*

public:
protected:

  // Correct any out-of-date distance calculations when we change a path prefix.
  // The arguments are the newly connected parent and child nodes.
  void updateTraveled(Node&parent, Node<CoveringArray, CoverageCost>&visited) {
    // Setup to DFS from the visited node.
    std::set<Node*>parentSet;
    std::set<Node*>visitedSet;
    parentSet.insert(&parent);
    visitedSet.insert(&visited);
    std::vector<const std::set<Node*>*>extrusion;
    std::vector<typename std::set<Node*>::const_iterator>path;
    extrusion.push_back(&parentSet);
    path.push_back(extrusion.back()->begin());
    extrusion.push_back(&visitedSet);
    path.push_back(extrusion.back()->begin());
    // Run the DFS, updating traveled distances and resorting.
    for (;;) {
      if (path.back() == extrusion.back()->end()) {
	path.pop_back();
	extrusion.pop_back();
	if (path.empty()) {
	  break;
	}
	++path.back();
      } else {
	typename
	  std::vector<typename std::set<Node*>::const_iterator>::
	  const_reverse_iterator back = path.rbegin();
	Node&update = ***back;
	assert(&update);
	++back;
	Node&source = ***back;
	assert(&source);
	update.setTraveled(space->getTraveled(source, update.getState()));
	CoverageCost rank = guide->rank(update);
	typename Relation<Node*, CoverageCost, true, false, Pless<Node<CoveringArray, CoverageCost>> >::key_iterator moribund = open.key_find(&update);
	if (moribund != open.key_end()) {
	  open.key_erase(moribund);
	  open.key_insert(&update, rank);
	  updateBest(update, rank);
	  ++path.back();
	} else {
	  moribund = closed.key_find(&update);
	  assert(moribund != closed.key_end());
	  closed.key_erase(moribund);
	  closed.key_insert(&update, rank);
	  updateBest(update, rank);
	  // Push children.
	  extrusion.push_back(&update.getChildren());
	  path.push_back(extrusion.back()->begin());
	}
      }
    }
  }

  // Return true if a node should be discarded because we have explored a better
  // node representing the same state.  Also, forget about any nodes that we
  // have explored if they represent the same state but are worse.
  bool replaceInClosed(Node&parent, Node<CoveringArray, CoverageCost>&node, CoverageCost traveled) {
    typename Relation<Node*, CoverageCost, true, false, Pless<Node<CoveringArray, CoverageCost>> >::key_iterator similar = closed.key_find(&node);
    if (similar == closed.key_end()) {
      // The node does not have an already explored state.
      return false;
    }
    Node*visited = similar->first;
    assert(visited);
    if (visited->getTraveled() <= traveled) {
      // The node has an already explored state and cannot improve a path;
      // discard it.
      return true;
    }
    // The node has an already explored state, but will improve some paths; use
    // it instead.
    parent.addChild(visited);
    updateTraveled(parent, *visited);
    return true;
  }

public:
  // Try to find a goal in the given budget.  If restartable is true the search
  // can be resumed by a later call to this method.
  std::set<Node*>search(unsigned iterations, bool restartable) {
    std::set<Node*>result;
    if (open.empty()) {
      return result;
    }
    for (unsigned i = iterations, j = configuration.prunePeriod;
	 i-- && result.empty();) {
#ifdef SEARCH_PROGRESS
      if (!(i & 0x3FF)) {
	std::cout << i << " iterations left after this one" << std::endl;
      }
#endif
      Node&parent = popBestOpen();
      // If it is time to prune exploration to the most promising frontier:
      if (!--j) {
	j = configuration.prunePeriod;
	std::set<const Node*>lineage;
	typename std::set<const Node*>::const_iterator
	  lineageEnd = lineage.end();
	for (const Node*k = &parent; k; k = k->getParent()) {
	  lineage.insert(k);
	}
	for (typename Relation<Node*, CoverageCost, true, false, Pless<Node<CoveringArray, CoverageCost>> >::key_iterator
	       k = closed.key_begin(),
	       kend = closed.key_end();
	     k != kend;) {
	  if (lineage.find(k->first) == lineageEnd) {
	    delete k->first;
	    closed.key_erase(k++);
	  } else {
	    ++k;
	  }
	}
	for (typename Relation<Node*, CoverageCost, true, false, Pless<Node<CoveringArray, CoverageCost>> >::key_iterator
	       k = open.key_begin(),
	       kend = open.key_end();
	     k != kend;++k) {
	  delete k->first;
	}
	open.clear();
      }
      // Explore.
      std::set<CoveringArray>children = getChildren(parent);
      if (configuration.retryChildren) {
	(*filter)(children, parent.getState(), *heuristic, *goal);
      } else {
	(*filter)(children, *heuristic, *goal);
      }
      // The flag to decide if the parent information can be deleted.  It is
      // true when we aren't looking for paths and the parent isn't part of the
      // best set.
      bool parentMoribund = false;
      if (!configuration.useClosed) {
	typename std::set<const Node*>::const_iterator
	  asBest = best.find(&parent),
	  bestEnd = best.end();
	if (asBest == bestEnd) {
	  parentMoribund = true;
	}
      }
      // See children.
      for (typename std::set<CoveringArray>::const_iterator
	     iterator = children.begin(),
	     end = children.end();
	   iterator != end;
	   ++iterator) {
	CoverageCost traveled = space->getTraveled(parent, *iterator);
	Node*node =
	  new Node
	  (configuration.useClosed ? &parent : NULL,
	   *iterator,
	   traveled,
	   heuristic->estimate(*iterator, *goal));
	if (replaceInOpen(parent, *node, traveled)) {
	  // The new node was beaten by something in the open set.
	  delete node;
	} else if (configuration.useClosed &&
		   replaceInClosed(parent, *node, traveled)) {
	  // The new node was beaten by something in the closed set.
	  delete node;
	} else {
	  // The new node is worth exploring.
	  addNew(node);
	  // Track goals.
	  if (goal->isGoal(*iterator)) {
	    result.insert(node);
	    // If we are just interested in finding a goal, we can return now.
	    if (!restartable) {
	      if (parentMoribund) {
		delete &parent;
	      }
	      // Complete the search.
	      SearchFinish finish(*this, result, iterations - i, iterations);
	      EventSource<SearchFinish>::dispatch(finish);
	      return result;
	    }
	  }
	}
      }
      if (parentMoribund) {
	delete &parent;
      }
      // Complete the iteration.
      SearchIteration iteration;
      EventSource<SearchIteration>::dispatch(iteration);
    }
    // Complete the search.
    SearchFinish finish(*this, result, iterations, iterations);
    EventSource<SearchFinish>::dispatch(finish);
    return result;
  }
};


 */