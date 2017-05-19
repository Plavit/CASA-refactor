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


#ifndef GOAL_H
#define GOAL_H

// Decides when a search should terminate because it has found a solution.  In
// some applications the goal's RTTI is also used to inform other search objects
// (such as the heuristic).

template<class STATE>class Goal {
public:
  virtual ~Goal() {}
  virtual bool isGoal(const STATE&state) = 0;
};

#endif
