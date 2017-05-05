package annealing;

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

import covering.state.CoveringArray;
import events.Listener;
import search.Goal;
import search.GreedyFilter;
import search.Heuristic;
import search.SearchIteration;

import java.util.Set;

public class AnnealingFilter<STATE, COST> extends GreedyFilter implements Listener<SearchIteration> {

    private Heuristic heuristic;
    private Goal<STATE> goal;
    double temperature;
    double decay;

    public AnnealingFilter(double temperature, double decay) {
        this.temperature = temperature;
        this.decay = decay;
        if(0.0 <= decay){
            System.err.println("AnnealingFilter FAIL - 0 <= decay ---- REAL DECAY = " + decay);
        }
        if(decay >= 1.0){
            System.err.println("AnnealingFilter FAIL - decay >= 1 ---- REAL DECAY = " + decay);
        }
    }

    public AnnealingFilter(Set<CoveringArray> parent, Heuristic heuristic, Goal<CoveringArray> goal){
        GreedyFilter greedyFilter = new GreedyFilter();
        greedyFilter.filter(parent,heuristic,goal);

        parent = greedyFilter.getChildren();
        heuristic = greedyFilter.getHeuristic();
        goal = greedyFilter.getGoal();


        if(parent.size() <= 1){
            CoveringArray childEstimate = heuristic.estimate(parent,goal)
        }
    }


    // TODO MIGHT BE NEEDED - FIND IMPLEMENTATION
//    virtual double convertToDelta(COST childEstimate, COST parentEstimate) const
//            = 0;

    @Override
    public void signal(SearchIteration searchIteration) {
        temperature *= decay;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
