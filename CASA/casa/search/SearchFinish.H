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


#ifndef SEARCH_FINISH_H
#define SEARCH_FINISH_H

#include <set>

#include "search/Node.H"

// A forward declaration for when this header is included by Search.H:
template<class, class>class Search;

// A message broadcast when a search finishes (even if it was unsuccessful).

template<class STATE, class COST>class SearchFinish {
public:
  typedef Search<STATE, COST>		SearchT;
  typedef Node<STATE, COST>		NodeT;

  const SearchT&			source;
  std::set<NodeT*>			results;
  unsigned				iterations;
  unsigned				maxIterations;

  SearchFinish
    (const SearchT&source,
     const std::set<NodeT*>&results,
     unsigned iterations,
     unsigned maxIterations) :
      source(source),
      results(results),
      iterations(iterations),
      maxIterations(maxIterations) {}
};

// For inclusion from anywhere but Search.H:
#include "search/Search.H"

#endif
