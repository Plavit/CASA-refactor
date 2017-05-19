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


#ifndef GUIDE_H
#define GUIDE_H

// Decides the order in which states are explored.  Usual policies include
// visiting the heuristically best states first, visiting states whose distance
// from the start state added to the heuristic is minimal, visiting states
// randomly, etc.  There is provision to treat start states specially.

template<class STATE, class COST>class Guide {
public:
  virtual ~Guide() {}
  virtual COST rankStart(const Node<STATE, COST>&start) const = 0;
  virtual COST rank(const Node<STATE, COST>&node) const = 0;
};

#endif
