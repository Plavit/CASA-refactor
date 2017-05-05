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
import common.utility.Igreater;
import covering.bookkeeping.Options;
import covering.cost.CoverageCost;
import covering.state.CoveringArray;
import events.Listener;
import sat.InputKnown;
import sat.InputTerm;
import search.Node;
import search.SearchFinish;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class SingleChangeSpace extends CoveringArraySpace implements Listener<SearchFinish> {

    private static final int MAXIMUM_SINGLE_CHANGE_FAILED_ATTEMPTS = 32;

    protected Vector<Array<Integer>> resurrectionBuffer = new Vector<>();

    public SingleChangeSpace(int strength, Options options) {
        super(strength, options);
    }

    @SuppressWarnings("Duplicates")
    protected Array<Integer> createRandomMatchingRow(InputKnown known) {
        Array<Integer> result = new Array<>(options.getSize());
        Random rand = new Random();
        for (int i = options.getSize(); i > 0; i--) {
            Vector<Integer> candidates = new Vector<>();
            for (int
                 j = options.getSymbolCount(i),
                 base = options.getFirstSymbol(i);
                 j > 0; j--) {
                candidates.add(base + j);
            }
            int index = rand.nextInt() % candidates.size();
            int symbol = candidates.get(index);
            known.append(new InputTerm(false, symbol));
            while (!solver.solve(known)) {
                known.undoAppend();
                candidates.set(index, candidates.get(candidates.size() - 1));
                candidates.remove(candidates.size() - 1);
                index = rand.nextInt() % candidates.size();
                symbol = candidates.get(index);
                known.append(new InputTerm(false, symbol));
            }
            result.set(i, symbol);
        }
        return result;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public CoveringArray createStartState(int rows) {
        CoveringArray result = new CoveringArray(rows, strength, options, solver);
        int i = rows;
        while (i-- >resurrectionBuffer.size()) {
            InputKnown known = new InputKnown();
            result.setBackingArrayRow(i, createRandomMatchingRow(known));
        }
        if (resurrectionBuffer.size() > 0) {
            for (++i; i > 0; i--) {
                Array<Integer> row = resurrectionBuffer.get(i);
                // We must deep copy to preserve the resurrection buffer.
                for (int j = options.getSize(); j > 0; j--) {
                    result.setBackingArrayEntry(i, j, row.get(j));
                }
            }
        }
        result.setTrackingCoverage(true);
        result.setTrackingNoncoverage(true);
        return result;
    }

    @Override
    public CoverageCost getTraveled(CoveringArray start) {
        return null;
    }

    @Override
    public CoverageCost getTraveled(Node parent, CoveringArray state) {
        return null;
    }

    @Override
    public Set<CoveringArray> getChildren(CoveringArray state, float proportion) {
        assert(false); // Unimplemented
        return getChildren(state, 1);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Set<CoveringArray> getChildren(CoveringArray state, int count) {
        Set<CoveringArray> result = new TreeSet<>();
        int rows = state.getRows();
        assert(options.getSize() == state.getOptions());
        assert(state.isTrackingNoncoverage());
        //TODO ???
        //const set<Array<unsigned>, ArrayComparator<unsigned> >&noncoverage =
        Set<Array<Integer>> noncoverage = state.getNoncoverage();
        if (noncoverage.isEmpty()) {
            return result;
        }
        int attempts = 0;
        Random rand = new Random();
        for (int i = count; i > 0; ++attempts) {
            int row = rand.nextInt() % rows;
            int option = rand.nextInt() % options.getSize();
            //TODO
            int value = options.getOtherRandomSymbol(option, state(row, option));
            InputKnown known = new InputKnown();
            known.append(InputTerm(false, value));
            if (attempts < MAXIMUM_SINGLE_CHANGE_FAILED_ATTEMPTS) {
                for (int j = 0; j < option; ++j) {
                    //TODO
                    known.append(InputTerm(false, state(row, j)));
                }
                for (int j = option + 1; j < options.getSize(); ++j) {
                    //TODO
                    known.append(InputTerm(false, state(row, j)));
                }
                if (solver.solve(known)) {
                    CoveringArray child = state;
                    //TODO
                    child(row, option) = value;
                    result.add(child);
                    --i;
                    attempts = 0;
                }
            } else {
                System.out.println("Considering a full row change");
                CoveringArray child = state;
                //TODO
                child(row) = createRandomMatchingRow(known);
                result.add(child);
                --i;
                attempts = 0;
            }
        }
        return result;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void signal(SearchFinish finish) {
        //const set<const Node<CoveringArray, CoverageCost>*>&best = finish.source.getBest();
        Set<Node> best = finish.source.getBest();
        if (best.isEmpty()) {
            return;
        }
        CoveringArray solution = TODO;//(*best.begin())->getState();
        int rowCount = solution.getRows();
        int optionCount = solution.getOptions();
        //TODO necessary?
        //resurrectionBuffer.reserve(rowCount);
        while (resurrectionBuffer.size() < rowCount) {
            resurrectionBuffer.add(new Array<>(0));
        }
        System.out.println("Sorting rows for distinct coverage");
        Array<Integer> distinctCoverage = solution.countDistinctCoverage();
        Array<Integer> permutation = new Array<>(rowCount);
        for (int i = rowCount; i > 0; i--) {
            permutation.set(i, i);
        }
        Igreater<Integer> betterDistinctCoverage = new Igreater<>(distinctCoverage);
        //TODO
        //sort(permutation + 0, permutation + rowCount, betterDistinctCoverage);
        System.out.println("Saving rows in resurrection buffer");
        for (int i = rowCount; i > 0; i--) {
            int p = permutation.get(i);
            Array<Integer> row = new Array<>(optionCount);
            for (int j = optionCount; j > 0; j--) {
                //TODO
                //row[j] = solution(p,j);
            }
            resurrectionBuffer.set(i, row);
        }
    }

    void clearResurrectionBuffer() {
        resurrectionBuffer = new Vector<>();
    }
}
