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


import java.util.NavigableSet;
import java.util.Set;

/**
 * A message broadcast when a search finishes (even if it was unsuccessful). 
 */
public class SearchFinish {
    public final Search source;
    public NavigableSet<Node> results;
    public long iterations;
    public long maxIterations;

    public SearchFinish(
            final Search source,
            final NavigableSet<Node> results,
            long iterations,
            long maxIterations) {
        this.source = source;
        this.results = results;
        this.iterations = iterations;
        this.maxIterations = maxIterations;
    }
}