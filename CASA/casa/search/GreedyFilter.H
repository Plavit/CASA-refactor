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


#ifndef GREEDYFILTER_H
#define GREEDYFILTER_H

#include "search/Filter.H"

// A specialization of Filter where only the heuristically best child is
// explored.  Furthermore, if given the option to treat the parent as a child,
// the filter will only have the search explore children if at least one of them
// is heuristically better than the parent.

template<class STATE, class COST>class GreedyFilter :
  public Filter<STATE, COST> {
  typedef Heuristic<STATE, COST>	HeuristicT;
  typedef Goal<STATE>			GoalT;

public:
  void operator()
    (std::set<STATE>&children, const HeuristicT&heuristic, const GoalT&goal)
    const {
    typename std::set<STATE>::iterator best = children.end();
    COST score = 0;
    for(typename std::set<STATE>::iterator
	  iterator = children.begin(),
	  end = children.end();
	iterator != end;
	++iterator) {
      COST estimate = heuristic.estimate(*iterator, goal);
      if (best == children.end() || estimate < score) {
	best = iterator;
	score = estimate;
      }
    }
    for(typename std::set<STATE>::iterator
	  iterator = children.begin(),
	  end = children.end();
	iterator != end;){
      if (iterator == best) {
	++iterator;
      } else {
	children.erase(iterator++);
      }
    }
  }
};

#endif
