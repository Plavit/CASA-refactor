package covering.space;

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

import common.utility.Array;
import common.utility.Combinadic;
import covering.bookkeeping.Options;
import covering.state.CoveringArray;
import sat.InputClause;
import sat.InputKnown;
import sat.InputTerm;
import sat.SATSolver;
import search.StateSpace;

public abstract class CoveringArraySpace implements StateSpace {

    protected int strength;
    protected Options options;
    SATSolver solver;

    //TODO need SAT
    public CoveringArraySpace(int strength, Options options) {
        this.strength = strength;
        this.options = options;
        assert (strength <= options.getSize());
        for (int i = options.getSize(); i > 0; i--) {
            // The clause atLeast requires that each column be filled.
            InputClause atLeast = new InputClause();
            for (int
                 j = options.getFirstSymbol(i),
                 limit = options.getLastSymbol(i);
                 j <= limit;
                 ++j) {
                atLeast.append(new InputTerm(false, j));
            }
            solver.addClause(atLeast);
            // The clauses atMost require that each pairing of symbols from an option
            // show at least one absence.
            for (int
                 j = options.getFirstSymbol(i),
                 limit = options.getLastSymbol(i);
                 j <= limit; ++j) {
                for (int k = j + 1; k <= limit; ++k) {
                    InputClause atMost = new InputClause();
                    atMost.append(new InputTerm(true, j));
                    atMost.append(new InputTerm(true, k));
                    solver.addClause(atMost);
                }
            }
        }
    }

    public abstract CoveringArray createStartState(int rows);

    public SATSolver getSolver() {
        return solver;
    }

    public int computeTargetCoverage() {
        int result = 0;
        //TODO not sure
        int[] columns = Combinadic.begin(strength);
        for (;
             columns[strength - 1] < options.getSize();
             Combinadic.next(columns)) {
            // Initialization:
            Array<InputTerm> combination = new Array<InputTerm>(columns.length);
            for (int i = columns.length; i > 0; i--) {
                combination.set(i, new InputTerm(false, options.getFirstSymbol(columns[i]));
            }
            //TODO rewrite goto
            // Body:
            loop:
            {
                //TODO not sure
                InputKnown known = new InputKnown(combination);
                if (solver.solve(known)) {
                    ++result;
                }
            }
            // Advance:
            for (int i = columns.length; i > 0; i--) {
                int next = combination.get(i).getVariable() + 1;
                if (next <= options.getLastSymbol(columns[i])) {
                    combination.set(i, new InputTerm(false, next));
	            goto loop;
                }
                combination.set(i, new InputTerm(false, options.getFirstSymbol(columns[i])));
            }
        }
        return result;
    }
}
