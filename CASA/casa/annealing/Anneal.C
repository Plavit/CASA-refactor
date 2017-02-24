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


#include "io/Usage.H"

#include "annealing/Anneal.H"
#include "annealing/Bounds.H"
#include "annealing/AnnealingSuccess.H"
#include "annealing/AnnealingPartitioner.H"

#include "search/Search.H"
#include "search/StateGuide.H"

#include "covering/space/SingleChangeSpace.H"
#include "covering/space/GraftSpace.H"
#include "covering/heuristic/CoveringArrayHeuristic.H"
#include "covering/goal/CoverageGoal.H"
#include "covering/filter/CoveringArrayAnnealingFilter.H"
#include "covering/report/IterationReport.H"

#include "algorithms/BinarySearch.H"

CoveringArray anneal
  (const SpecificationFile&specification,
   const ConstraintFile&constraints,
   unsigned iterations,
   double startingTemperature,
   double decrement) {
  std::cout << "Setting up annealing framework" << std::endl;
  // Build and connect all of the pieces of the search.
  // For simulated annealing:
  SearchConfiguration configuration(false,true,1U,0U);
#ifdef WITHOUT_INNER
  SingleChangeSpace space
#else
  GraftSpace space
#endif
    (specification.getStrength(), specification.getOptions());
  // Add the constraints.
  {
    SATSolver&solver = space.getSolver();
    const Array<InputClause>&clauses = constraints.getClauses();
    for (unsigned i = clauses.getSize(); i--;) {
      solver.addClause(const_cast<InputClause&>(clauses[i]));
    }
  }
  CoveringArrayHeuristic heuristic;
  StateGuide<CoveringArray, CoverageCost>guide;
  CoverageGoal goal(space.computeTargetCoverage());
  CoveringArrayAnnealingFilter filter(startingTemperature,decrement);
  IterationReport iterationReport;
  Search<CoveringArray, CoverageCost>search
    (configuration, &space, &heuristic, &guide, &goal, &filter, true);
  AnnealingPartitioner partitioner;
  // Add the listeners.
  {
    // For cooling:
    ((EventSource<SearchIteration>&)search).addListener(filter);
    typedef EventSource<SearchFinish<CoveringArray, CoverageCost> >
      FinishEventSourceT;
    // Structure start states:
    ((FinishEventSourceT&)search).addListener(space);
    // Use search feedback to guide the outer search:
    ((FinishEventSourceT&)search).addListener(partitioner);
    // Give reports on iterations taken:
    ((FinishEventSourceT&)search).addListener(iterationReport);
  }
  CoveringArray initialSolution
    (0,
     specification.getStrength(),
     specification.getOptions(),
     space.getSolver());

  // Here we go...
  std::cout << "Annealing"
#ifdef WITHOUT_INNER
       << " (without t-set replacement)"
#endif
#ifdef WITHOUT_OUTER
       << " (without extra outer search)"
#endif
       << std::endl;
  unsigned lower =
    computeLowerBound(specification.getStrength(), specification.getOptions());
  unsigned upper =
    computeUpperBound(specification.getStrength(), specification.getOptions());
  AnnealingSuccess annealingSuccess(search, iterations, initialSolution);

  std::cout << "Suspect that the optimum number of rows is in [" << lower <<
    ".." << upper << ']' << std::endl;
  std::cout << "Starting binary search" << std::endl;
  BinarySearch binarySearch(annealingSuccess, partitioner);
  unsigned result = binarySearch(lower, upper + 1 - lower);
  while (result > upper) {
    upper <<= 1;
    std::cout << "Trying more conservative upper bound " << upper << std::endl;
    result = binarySearch(lower, upper + 1 - lower);
  }
#ifndef WITHOUT_OUTER
  do {
    if (result == lower) {
      if (lower > 5) {
	lower -= 5;
      } else {
	--lower;
	if (!lower) {
	  break;
	}
      }
      std::cout << "Trying less conservative lower bound " << lower <<
	std::endl;
    }
    unsigned lastResult = result;
    unsigned upped = 0;
    do {
      if (lastResult == result) {
	iterations *= 2;
	annealingSuccess.setIterations(iterations);
	std::cout << "Upping iterations to " << iterations << std::endl;
	++upped;
      } else {
	upped = 0;
      }
      lastResult = result;
      std::cout << "Restarting binary search with best result at " <<
	lastResult << " rows" << std::endl;
      filter.setTemperature(startingTemperature);
      result = binarySearch(lower, lastResult - lower);
    } while ((result < lastResult || upped < retries) && result > lower);
  } while (result == lower);
#endif
  std::cout << "Giving up with best result at " << result << " rows" <<
    std::endl;
  std::cout << "Total cost of computation: " << iterationReport.getTotal() <<
    " iteration(s)" << std::endl;
  return annealingSuccess.getSolution();
}
