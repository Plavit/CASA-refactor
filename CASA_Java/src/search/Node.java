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


import java.util.HashSet;
import java.util.Set;

/**
 Represents an explored state, its heuristic estimate, the best known path
 leading to that state, and other nodes whose best paths this state is on.
 */
public class Node<STATE extends Comparable<STATE>, COST> implements Comparable<Node<STATE, COST>> {

    /** The node immediately preceding this one on a best path. */
    protected Node<STATE,COST> parent;
    /** The state. */
    protected final STATE state;
    /** The distance from the start state along the best path. */
    protected COST traveled;
    /** The heuristic estimate of the distance to a goal state. */
    protected COST estimate;
    /** The set of nodes who have this one as their parent. */
    protected HashSet<Node<STATE,COST>> children;

    public Node(Node<STATE,COST> parent, final STATE state, COST traveled, COST estimate)
    {
        this.parent = parent;
        this.state = state;
        this.traveled = traveled;
        this.estimate = estimate;
        this.children = new HashSet<>();
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

    /* TODO: call this where appropriate */
    public void destructor() {
        if (parent != null) {
            parent.removeChild(this);
        }
        for (Node<STATE,COST> child : children) {
            removeChild(child);
        }
    }

    public STATE getState() {
        return state;
    }

    public COST getTraveled() {
        return traveled;
    }
    public void setTraveled(COST traveled) {
        this.traveled = traveled;
    }

    public COST getEstimate() {
        return estimate;
    }
    public void setEstimate(COST estimate) {
        this.estimate = estimate;
    }

    public Node<STATE,COST> getParent() {
        return parent;
    }

    public Set<Node<STATE,COST>> getChildren() {
        return children;
    }

    public void addChild(Node<STATE,COST> child) {
        assert(child != null);
        if (child.parent != null) {
            if (child.parent == this) {
                return;
            }
            child.parent.removeChild(child);
        }
        child.parent = this;
        children.add(child);
    }

    public void removeChild(Node<STATE,COST> child) {
        assert(child != null);
        if (child.parent != this) {
            return;
        }
        child.parent = null;
        children.remove(child);
    }

    @Override
    public int compareTo(Node<STATE, COST> other) {
        return state.compareTo(other.state);
    }

    /* TODO: not sure if need to override equals & hashCode */
    /*
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Node.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Node<STATE,COST> other = (Node<STATE, COST>) obj;
        return (this.state == null) ? other.state == null : this.state.equals(other.state);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.state != null ? this.state.hashCode() : 0);
        return hash;
    }
    */

    @Override
    public String toString() {
        return state + "(" + traveled + "+" + estimate + "*)" +
                ((getParent() != null) ? "<-" + getParent() : "");
    }
};