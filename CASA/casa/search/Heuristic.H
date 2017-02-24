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


#ifndef HEURISTIC_H
#define HEURISTIC_H

#include "search/Goal.H"

// Rates the quality of a state (never a path).

template<class STATE, class COST>class Heuristic {
public:
  virtual ~Heuristic() {}
  virtual COST estimate(const STATE&state, const Goal<STATE>&goal) const = 0;
};

#endif
