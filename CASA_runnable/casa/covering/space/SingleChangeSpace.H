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


#ifndef SINGLE_CHANGE_SPACE_H
#define SINGLE_CHANGE_SPACE_H

#include <cstdlib>
#include <cassert>
#include <vector>

#include "../../../common/utility/Array.H"

#include "CoveringArraySpace.H"
#include "../cost/CoverageCost.H"

#include "../../events/Listener.H"
#include "../../search/SearchFinish.H"

class SingleChangeSpace :
  public CoveringArraySpace,
  public Listener<SearchFinish<CoveringArray, CoverageCost> > {
protected:
  std::vector<Array<unsigned> >		resurrectionBuffer;

public:
  SingleChangeSpace(unsigned strength, Options options) :
    CoveringArraySpace(strength, options) {}

protected:
  Array<unsigned>createRandomMatchingRow(InputKnown&known) const;

public:
  CoveringArray createStartState(unsigned rows) const;
  CoverageCost getTraveled(const CoveringArray&start) const;
  CoverageCost getTraveled
    (const Node<CoveringArray, CoverageCost>&parent,
     const CoveringArray&state) const;
  std::set<CoveringArray>getChildren
    (const CoveringArray&state, float proportion) const;
  std::set<CoveringArray>getChildren
    (const CoveringArray&state, unsigned count) const;
  void signal(const SearchFinish<CoveringArray, CoverageCost>&finish);
  void clearResurrectionBuffer();
};

#endif
