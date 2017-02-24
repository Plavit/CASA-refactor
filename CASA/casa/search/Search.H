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


#ifndef SEARCH_H
#define SEARCH_H

#include <cassert>
#include <vector>
#include <set>

#include "utility/pless.H"
#include "utility/relation.H"

#include "events/EventSource.H"

#include "search/SearchConfiguration.H"
#include "search/Node.H"
#include "search/StateSpace.H"
#include "search/Heuristic.H"
#include "search/Guide.H"
#include "search/Goal.H"
#include "search/Filter.H"
#include "search/SearchIteration.H"
#include "search/SearchFinish.H"

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

template<class STATE, class COST>class Search :
  public EventSource<SearchIteration>,
  public EventSource<SearchFinish<STATE, COST> > {
public:
  typedef Node<STATE, COST>		NodeT;

protected:
  typedef relation<NodeT*, COST, true, false, pless<NodeT> >
					VisitSetT;
  typedef StateSpace<STATE, COST>
					StateSpaceT;
  typedef Heuristic<STATE, COST>
					HeuristicT;
  typedef Guide<STATE, COST>
					GuideT;
  typedef Goal<STATE>			GoalT;
  typedef Filter<STATE,COST>		FilterT;
  typedef SearchFinish<STATE, COST>
					SearchFinishT;

  SearchConfiguration			configuration;
  StateSpaceT*				space;
  HeuristicT*				heuristic;
  GuideT*				guide;
  GoalT*				goal;
  FilterT*				filter;
  bool					oneBest;
  VisitSetT				open;
  VisitSetT				closed;
  std::set<const NodeT*>		best;
  COST					bestRank;

public:
  // See the classes SearchConfiguration, StateSpace, Heuristic, Guide, Goal,
  // and Filter for documentation on their role in search.  The parameter
  // oneBest does not affect the method of search, but merely how many solutions
  // are remembered in the case of a tie in the guide's ranking.
  Search
    (SearchConfiguration configuration,
     StateSpaceT*space,
     HeuristicT*heuristic,
     GuideT*guide,
     GoalT*goal,
     FilterT*filter,
     bool oneBest) :
      configuration(configuration),
      space(space),
      heuristic(heuristic),
      guide(guide),
      goal(goal),
      filter(filter),
      oneBest(oneBest) {
    assert(space);
    assert(heuristic);
    assert(guide);
    assert(goal);
    assert(filter);
  }

  virtual ~Search() {
    clear();
  }

protected:
  // A leak-free way to clear the set of best nodes.
  void clearBest() {
    if (!configuration.useClosed) {
      for (typename std::set<const NodeT*>::const_iterator
	     iterator = best.begin(),
	     end = best.end();
	   iterator != end;
	   ++iterator) {
	NodeT*node = const_cast<NodeT*>(*iterator);
	if (open.key_find(node) == open.key_end()) {
	  delete node;
	}
      }
    }
    best.clear();
  }

  // See if the given node with the given guide ranking should be entered into
  // the set of best nodes.
  void updateBest(const NodeT&node, COST rank) {
    if (rank < bestRank) {
      clearBest();
    }
    if (best.empty()) {
      best.insert(&node);
      bestRank = rank;
    } else if (rank == bestRank) {
      if (oneBest) clearBest();
      best.insert(&node);
    }
  }

  // Pop the best ranked node from the set of nodes that have been seen but not
  // explored.
  NodeT&popBestOpen() {
    assert(!open.empty());
    typename VisitSetT::data_iterator pop = open.data_begin();
    assert(pop->second);
    if (configuration.useClosed) {
      closed.key_insert(pop->second, pop->first);
    }
    NodeT&result = *(pop->second);
    open.data_erase(pop);
    return result;
  }

  // Get the children (immediately reachable neighbors) according to the
  // SearchConfiguration.
  std::set<STATE>getChildren(const NodeT&parent) {
    if (configuration.proportionChildren) {
      return space->getChildren
	(parent.getState(), configuration.childrenAsk.proportion);
    }
    return space->getChildren
      (parent.getState(), configuration.childrenAsk.count);
  }

  // Return true if a node should be discarded because we have seen a better one
  // representing the same state, but not explored it yet.  Also, forget about
  // any nodes that we have seen but not explored if they represent the same
  // state but are worse.
  bool replaceInOpen(NodeT&parent, NodeT&node, COST traveled) {
    typename VisitSetT::key_iterator similar = open.key_find(&node);
    if (similar == open.key_end()) {
      // The node does not have an already seen state.
      return false;
    }
    NodeT*visited = similar->first;
    assert(visited);
    if (visited->getTraveled() <= traveled) {
      // The node has an already seen state and cannot improve a path; discard
      // it.
      return true;
    }
    // The node has an already seen state, but may improve some paths; use it
    // instead.
    if (configuration.useClosed) {
      parent.addChild(visited);
    }
    visited->setTraveled(traveled);
    open.key_erase(similar);
    COST rank = guide->rank(*visited);
    open.key_insert(visited, rank);
    updateBest(*visited, rank);
    return true;
  }

  // Correct any out-of-date distance calculations when we change a path prefix.
  // The arguments are the newly connected parent and child nodes.
  void updateTraveled(NodeT&parent, NodeT&visited) {
    // Setup to DFS from the visited node.
    std::set<NodeT*>parentSet;
    std::set<NodeT*>visitedSet;
    parentSet.insert(&parent);
    visitedSet.insert(&visited);
    std::vector<const std::set<NodeT*>*>extrusion;
    std::vector<typename std::set<NodeT*>::const_iterator>path;
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
	  std::vector<typename std::set<NodeT*>::const_iterator>::
	  const_reverse_iterator back = path.rbegin();
	NodeT&update = ***back;
	assert(&update);
	++back;
	NodeT&source = ***back;
	assert(&source);
	update.setTraveled(space->getTraveled(source, update.getState()));
	COST rank = guide->rank(update);
	typename VisitSetT::key_iterator moribund = open.key_find(&update);
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
  bool replaceInClosed(NodeT&parent, NodeT&node, COST traveled) {
    typename VisitSetT::key_iterator similar = closed.key_find(&node);
    if (similar == closed.key_end()) {
      // The node does not have an already explored state.
      return false;
    }
    NodeT*visited = similar->first;
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

  //Add a newly seen node to the set of seen but not yet explored nodes.
  void addNew(NodeT*node) {
    COST rank = guide->rank(*node);
    open.key_insert(node,rank);
    updateBest(*node,rank);
  }

public:
  // Add a start state before searching.
  void addStartState(const STATE&start) {
    NodeT*node =
      new NodeT
      (NULL,
       start,
       space->getTraveled(start),
       heuristic->estimate(start, *goal));
    addNew(node);
  }

  // Try to find a goal in the given budget.  If restartable is true the search
  // can be resumed by a later call to this method.
  std::set<NodeT*>search(unsigned iterations, bool restartable) {
    std::set<NodeT*>result;
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
      NodeT&parent = popBestOpen();
      // If it is time to prune exploration to the most promising frontier:
      if (!--j) {
	j = configuration.prunePeriod;
	std::set<const NodeT*>lineage;
	typename std::set<const NodeT*>::const_iterator
	  lineageEnd = lineage.end();
	for (const NodeT*k = &parent; k; k = k->getParent()) {
	  lineage.insert(k);
	}
	for (typename VisitSetT::key_iterator
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
	for (typename VisitSetT::key_iterator
	       k = open.key_begin(),
	       kend = open.key_end();
	     k != kend;++k) {
	  delete k->first;
	}
	open.clear();
      }
      // Explore.
      std::set<STATE>children = getChildren(parent);
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
	typename std::set<const NodeT*>::const_iterator
	  asBest = best.find(&parent),
	  bestEnd = best.end();
	if (asBest == bestEnd) {
	  parentMoribund = true;
	}
      }
      // See children.
      for (typename std::set<STATE>::const_iterator
	     iterator = children.begin(),
	     end = children.end();
	   iterator != end;
	   ++iterator) {
	COST traveled = space->getTraveled(parent, *iterator);
	NodeT*node =
	  new NodeT
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
	      SearchFinishT finish(*this, result, iterations - i, iterations);
	      EventSource<SearchFinishT>::dispatch(finish);
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
    SearchFinishT finish(*this, result, iterations, iterations);
    EventSource<SearchFinishT>::dispatch(finish);
    return result;
  }

#define GET_SET(type, member, capMember) \
  const type get ## capMember() const { \
    return member; \
  } \
  void set ## capMember(const type&member) { \
    this->member = member; \
  }
  GET_SET(SearchConfiguration, configuration, SearchConfiguration);
  GET_SET(StateSpaceT*, space, Space);
  GET_SET(HeuristicT*, heuristic, Heuristic);
  GET_SET(GuideT*, guide, Guide);
  GET_SET(GoalT*, goal, Goal);
#undef GET_SET

  const std::set<const NodeT*>getBest() const {
    return best;
  }

  void clear() {
    clearBest();
    for (typename VisitSetT::key_iterator
	   k = open.key_begin(),
	   kend = open.key_end();
	 k != kend;
	 ++k) {
      delete k->first;
    }
    open.clear();
    for (typename VisitSetT::key_iterator
	   k = closed.key_begin(),
	   kend = closed.key_end();
	 k != kend;
	 ++k) {
      delete k->first;
    }
    closed.clear();
  }
};

#endif
