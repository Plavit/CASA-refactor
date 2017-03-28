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
//  <destruct>

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import common.utility.Relation;
import covering.cost.CoverageCost;
import covering.state.CoveringArray;
import events.EventSource;

import java.util.*;

public class Search
{
    private SearchConfiguration	    configuration;
    private StateSpace				space;
    private Heuristic				heuristic;
    private Guide				    guide;
    private Goal<CoveringArray>		goal;
    private Filter				    filter;
    private boolean					oneBest;
    private Relation                open;
    private Relation                closed;
    private Set<Node>               best;
    private CoverageCost            bestRank;
    private EventSource<SearchFinish>   searchFinishEventSource;
    private EventSource<SearchIteration> searchIterationEventSource;

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
        this.open = new Relation();
        this.closed = new Relation();
        this.best = new HashSet<>();
        this.bestRank = new CoverageCost();
        this.searchFinishEventSource = new EventSource<>();
        this.searchIterationEventSource = new EventSource<>();
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

    public EventSource<SearchFinish> asSearchFinishEventSource() {
        return searchFinishEventSource;
    }

    public EventSource<SearchIteration> asSearchIterationEventSource() {
        return searchIterationEventSource;
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
                if (!open.key_find(n).hasNext()) {
                    n.destruct();
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
        Node node = new Node(null, start, space.getTraveled(start), heuristic.estimate(start, goal));
        addNew(node);
    }

    // Pop the best ranked node from the set of nodes that have been seen but not
    // explored.
    private Node popBestOpen() {
        Iterator<Map.Entry<CoverageCost, Node>> dataEntries = open.getDataEntries().iterator();
        assert (dataEntries.hasNext());
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
        Iterator<Map.Entry<Node, CoverageCost>> similar = open.key_find(node);
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

    // Correct any out-of-date distance calculations when we change a path prefix.
    // The arguments are the newly connected parent and child nodes.
    private void updateTraveled(Node parent, Node visited) {
        // Setup to DFS from the visited node.
        NavigableSet<Node> parentSet = new TreeSet<>();
        NavigableSet<Node> visitedSet = new TreeSet<>();
        parentSet.add(parent);
        visitedSet.add(visited);
        ArrayDeque<NavigableSet<Node>> extrusion= new ArrayDeque<>();
        ArrayDeque<PeekingIterator<Node>> path = new ArrayDeque<>();
        extrusion.addFirst(parentSet);
        path.addLast(Iterators.peekingIterator(extrusion.peekLast().iterator()));
        extrusion.addLast(visitedSet);
        path.addLast(Iterators.peekingIterator(extrusion.peekLast().iterator()));
        // Run the DFS, updating traveled distances and resorting.
        for (;;) {
            if (path.peekLast().peek() == extrusion.peekLast().descendingIterator().next()) {
                path.removeLast();
                extrusion.removeLast();
                if (path.isEmpty()) {
                    break;
                }
                path.peekLast().next();
            } else {
                Iterator<PeekingIterator<Node>> back = path.descendingIterator();
                Node update = back.next().peek();
                assert(update != null);
                Node source = back.next().peek();
                assert(source != null);
                update.setTraveled(space.getTraveled(source, update.getState()));
                CoverageCost rank = guide.rank(update);
                PeekingIterator<Map.Entry<Node, CoverageCost>> moribund = open.key_find(update);
                if (moribund.hasNext()) {
                    open.key_erase(moribund.peek());
                    open.key_insert(update, rank);
                    updateBest(update, rank);
                    path.peekLast().next();
                } else {
                    moribund = closed.key_find(update);
                    assert(moribund.hasNext());
                    closed.key_erase(moribund.peek());
                    closed.key_insert(update, rank);
                    updateBest(update, rank);
                    // Push children.
                    extrusion.addLast(update.getChildren());
                    path.addLast(Iterators.peekingIterator(extrusion.peekLast().iterator()));
                }
            }
        }
    }

    // Return true if a node should be discarded because we have explored a better
    // node representing the same state.  Also, forget about any nodes that we
    // have explored if they represent the same state but are worse.
    private boolean replaceInClosed(Node parent, Node node, CoverageCost traveled) {
        PeekingIterator<Map.Entry<Node, CoverageCost>> similar = closed.key_find(node);
        /* TODO: is this OK? how key_end() should be implemented to make this work? */
        if (!similar.hasNext()) {
            // The node does not have an already explored state.
            return false;
        }
        Node visited = similar.peek().getKey();
        assert(visited != null);
        if (visited.getTraveled().compareTo(traveled) <= 0) {
            // The node has an already explored state and cannot improve a path;
            // discard it.
            return true;
        }
        // The node has an already explored state, but will improve some paths; use
        // it instead.
        parent.addChild(visited);
        updateTraveled(parent, visited);
        return true;
    }

    // Try to find a goal in the given budget.  If restartable is true the search
    // can be resumed by a later call to this method.
    NavigableSet<Node> search(long iterations, boolean restartable) {
        NavigableSet<Node> result = new TreeSet<>();
        if (open.isEmpty()) {
            return result;
        }
        for (long i = iterations, j = configuration.prunePeriod;
             (i-- != 0) && result.isEmpty(); ) {
            if (false) { // SEARCH_PROGRESS
                if ((i & 0x3FF) != 0) {
                    System.out.printf("%d iterations left after this one\n", i);
                }
            }
            Node parent = popBestOpen();
            // If it is time to prune exploration to the most promising frontier:
            if (--j != 0) {
                j = configuration.prunePeriod;
                HashSet<Node> lineage = new HashSet<>();
                for (Node k = parent; k != null; k = k.getParent()) {
                    lineage.add(k);
                }
                for (Map.Entry<Node, CoverageCost> k : closed.getKeys()) {
                    if (!lineage.contains(k.getKey())) {
                        k.getKey().destruct();
                        closed.key_erase(k);
                    }
                }
                for (Map.Entry<Node, CoverageCost> k : open.getKeys()) {
                    k.getKey().destruct();
                }
                open.clear();
            }
            // Explore.
            Set<CoveringArray> children = getChildren(parent);
            if (configuration.retryChildren) {
                filter.filter(children, parent.getState(), heuristic, goal);
            } else {
                filter.filter(children, heuristic, goal);
            }
            // The flag to decide if the parent information can be deleted.  It is
            // true when we aren't looking for paths and the parent isn't part of the
            // best set.
            boolean parentMoribund = false;
            if (!configuration.useClosed) {
                if (!best.contains(parent)) {
                    parentMoribund = true;
                }
            }
            // See children.
            for (CoveringArray child : children) {
                CoverageCost traveled = space.getTraveled(parent, child);
                Node node = new Node(
                        configuration.useClosed ? parent : null,
                        child, traveled, heuristic.estimate(child, goal));
                if (replaceInOpen(parent, node, traveled)) {
                    // The new node was beaten by something in the open set.
                    node.destruct();
                } else if (configuration.useClosed && replaceInClosed(parent, node, traveled)) {
                    // The new node was beaten by something in the closed set.
                    node.destruct();
                } else {
                    // The new node is worth exploring.
                    addNew(node);
                    // Track goals.
                    if (goal.isGoal(child)){
                        result.add(node);
                        // If we are just interested in finding a goal, we can return now.
                        if (!restartable) {
                            if (parentMoribund) {
                                parent.destruct();
                            }
                            // Complete the search.
                            SearchFinish finish = new SearchFinish(this, result, iterations - i, iterations);
                            asSearchFinishEventSource().dispatch(finish);
                            return result;
                        }
                    }
                }
            }
            if (parentMoribund) {
                parent.destruct();
            }
            // Complete the iteration.
            asSearchIterationEventSource().dispatch(new SearchIteration());
        }
        // Complete the search.
        SearchFinish finish = new SearchFinish(this, result, iterations, iterations);
        asSearchFinishEventSource().dispatch(finish);
        return result;
    }
}