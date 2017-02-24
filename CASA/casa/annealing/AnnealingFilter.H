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


#ifndef ANNEALING_FILTER_H
#define ANNEALING_FILTER_H

#include <cassert>
#include <iostream>

#include "search/GreedyFilter.H"
#include "events/Listener.H"

// Decides, according to the rules of simulated annealing, when to take good
// moves and when to take bad ones.

// Should be added as a listener to search iterations in order that cooling be
// synchronized with the search.

template<class STATE, class COST>class AnnealingFilter :
  public GreedyFilter<STATE,COST>,
  public Listener<SearchIteration> {

  typedef Heuristic<STATE,COST>		HeuristicT;
  typedef Goal<STATE>			GoalT;

  double				temperature;
  const double				decay;

public:
  AnnealingFilter(double temperature, double decay) :
    temperature(temperature),
    decay(decay) {
    assert(0 <= decay);
    assert(decay <= 1);
  }
  virtual ~AnnealingFilter() {}

  void setTemperature(double temperature) {
    this->temperature = temperature;
  }

  virtual double convertToDelta(COST childEstimate, COST parentEstimate) const
    = 0;

  void signal(const SearchIteration&message) {
    temperature *= decay;
  }

  void operator()
    (std::set<STATE>&children,
     const STATE&parent,
     const HeuristicT&heuristic,
     const GoalT&goal) const {
    GreedyFilter<STATE,COST>::operator()(children, heuristic, goal);
    assert(children.size() <= 1);
    if (children.size()) {
      typename std::set<STATE>::iterator child = children.begin();
      COST childEstimate = heuristic.estimate(*child, goal);
      COST parentEstimate = heuristic.estimate(parent, goal);
      if (!(parentEstimate < childEstimate)) {
	// Always take good moves.
	return;
      }
      double delta = convertToDelta(childEstimate, parentEstimate);
      double probability = exp(delta / temperature);
      int boundary = (int)(probability * RAND_MAX);
      if (rand() < boundary) {
	// Sometimes take bad moves.
	return;
      }
      children.clear();
    }
    // When no moves are possible or a bad move is rejected, keep the parent.
    children.insert(parent);
  }
};

#endif
