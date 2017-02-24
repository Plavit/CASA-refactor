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


#ifndef NODE_H
#define NODE_H

#include <cassert>
#include <iostream>
#include <set>

// Represents an explored state, its heuristic estimate, the best known path
// leading to that state, and other nodes whose best paths this state is on.

template<class STATE, class COST>class Node {
protected:
  typedef Node<STATE,COST>*		Edge;

  // The node immediately preceding this one on a best path.
  Edge					parent;
  // The state.
  const STATE				state;
  // The distance from the start state along the best path.
  COST					traveled;
  // The heuristic estimate of the distance to a goal state.
  COST					estimate;
  // The set of nodes who have this one as their parent.
  std::set<Edge>			children;

public:
  Node(Edge parent, const STATE&state, COST traveled, COST estimate) :
    parent(parent),
    state(state),
    traveled(traveled),
    estimate(estimate) {
    if (parent) {
      parent->children.insert(this);
    }
  }

  virtual ~Node() {
    if (parent) {
      parent->removeChild(this);
    }
    for (typename std::set<Edge>::iterator
	   iterator = children.begin(),
	   end = children.end();
	 iterator != end;) {
      Edge child = *iterator;
      ++iterator;
      removeChild(child);
    }
  }

  const STATE&getState() const {
    return state;
  }

  COST getTraveled() const {
    return traveled;
  }
  void setTraveled(COST traveled) {
    this->traveled = traveled;
  }

  COST getEstimate() const {
    return estimate;
  }
  void setEstimate(COST estimate) {
    this->estimate = estimate;
  }

  Edge getParent() const {
    return parent;
  }

  const std::set<Edge>&getChildren() const {
    return children;
  }

  void addChild(Edge child) {
    assert(child);
    if (child->parent) {
      if (child->parent == this) {
	return;
      }
      child->parent->removeChild(child);
    }
    child->parent = this;
    children.insert(child);
  }

  void removeChild(Edge child) {
    assert(child);
    if (child->parent != this) {
      return;
    }
    child->parent = NULL;
    children.erase(child);
  }

  // Comparisons are made solely by state.
#define COMP(op) bool operator op(const Node&other) const { \
    return state op other.state; \
  }
  COMP(<);
  COMP(>);
  COMP(==);
  COMP(!=);
#undef COMP
};

template<class STATE, class COST>std::ostream&operator <<
  (std::ostream&out, const Node<STATE, COST>node) {
  out << node.getState() << '(' << node.getTraveled() << '+' <<
    node.getEstimate() << "*)";
  if (node.getParent()) {
    return out << "<-" << *node.getParent();
  }
  return out;
}

#endif
