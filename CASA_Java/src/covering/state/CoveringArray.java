package covering.state;

import java.util.*;

import common.utility.Array;
import common.utility.Combinadic;
import common.utility.Lazy;
import covering.bookkeeping.Coverage;
import covering.bookkeeping.Options;
import sat.SATSolver;

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

class RowOptionPair {
    private int row;
    private int option;

    RowOptionPair(int key, int value) {
        this.row = key;
        this.option = value;
    }

    int getRow() {
        return row;
    }

    int getOption() {
        return option;
    }

    void setOption(int option) {
        this.option = option;
    }

    public int hashCode() {
        return (row + option) * option + row;
    }

    public boolean equals(Object other) {
        if (other instanceof RowOptionPair) {
            RowOptionPair otherPair = (RowOptionPair) other;
            return (this.row == otherPair.row) && (this.option == otherPair.option);
        }
        return false;
    }
}

public class CoveringArray implements Comparable<CoveringArray> {

    /* TODO */
    public int compareTo(CoveringArray o) {
        return 0;
    }

    //MAXIMUM_COVERING_ARRAY_SUBSTITUTION 0x40
    public static int MAXIMUM_COVERING_ARRAY_SUBSTITUTION = 64;

    // The first index is the test configuration (row).
    // The second index is the option (column).
    protected Array<Array<Integer>> array;
    protected Lazy<Map<RowOptionPair, Integer>> substitutions;
    protected SATSolver solver;
    protected boolean trackingCoverage;
    protected boolean trackingNoncoverage;
    protected Integer coverageCount;
    protected Integer multipleCoverageCount;
    private Coverage<Integer> coverage;

    //TODO http://en.cppreference.com/w/cpp/container/set   , ArrayComparator is used for sorting
    //Lazy<std::set<Array<unsigned>, ArrayComparator<unsigned> > > noncoverage;
    protected Lazy<Set<Array<Integer>>> noncoverage;

    public CoveringArray(Integer rows, Integer strength, Options options, SATSolver solver) {

        this.array = new Array<>(rows);
        this.substitutions = new Lazy<>(new TreeMap<>());
        this.solver = solver;
        this.trackingCoverage = false;
        this.trackingNoncoverage = false;
        this.coverageCount = 0;
        this.multipleCoverageCount = 0;
        this.coverage = new Coverage<>(strength, options);

        this.noncoverage = new Lazy<>(new TreeSet<>());
        for (int i = rows; i > 0; i--) {
            array.set(i, new Array<>(options.getSize()));
        }
        coverage.fill(0);
    }

    public CoveringArray(CoveringArray copy) {

        this.array = new Array<>(copy.array);
        this.substitutions = new Lazy<>(copy.substitutions);
        this.solver = copy.solver;
        this.trackingCoverage = copy.trackingCoverage;
        this.trackingNoncoverage = copy.trackingNoncoverage;
        this.coverageCount = copy.coverageCount;
        this.multipleCoverageCount = copy.multipleCoverageCount;
        this.coverage = new Coverage<>(copy.coverage);
        this.noncoverage = new Lazy<>(copy.noncoverage);
        assert (!array.isEmpty());
    }

    public Integer getRows() {
        return array.getSize();
    }

    public Integer getOptions() {
        return (array.getSize() != 0) ? array.get(0).getSize() : 0;
    }

    public void setBackingArrayEntry(Integer row, Integer option, Integer value) {
        assert (!(substitutions.getImplementation().size() > 0));
        array.get(row).set(option, value);
    }

    public void setBackingArrayRow(Integer row, Array<Integer> value) {
        assert (!(substitutions.getImplementation().size() > 0));
        array.set(row, value);
    }

    public CoveringArrayEntry getEntry(Integer row, Integer option) {
        return new CoveringArrayEntry(this, row, option);
    }

    public CoveringArrayRow getRow(Integer row) {
        return new CoveringArrayRow(this, row);
    }

    public CoveringArraySubRow getSubRow(Integer row, Array<Integer> colums) {
        return new CoveringArraySubRow(this, row, colums);
    }

    public Integer getCoverageCount() {
        return coverageCount;
    }

    public Integer getMultipleCoverageCount() {
        return multipleCoverageCount;
    }

    public Array<Integer> countDistinctCoverage() {
        assert (trackingCoverage);
        Array<Integer> result = new Array<>(array.getSize());
        result.fill(0);

        Integer strength = coverage.getStrength();
        Integer limit = coverage.getOptions().getSize();

        Vector<Integer> firsts = coverage.getOptions().getFirstSymbols();
        Vector<Integer> counts = coverage.getOptions().getSymbolCounts();
        Integer hint = 0;

        //TODO not sure if arrays are updated in for loop,
        //because parameters are copied when passing to method
        int[] columns = Combinadic.begin(strength);
        int[] symbols = new int[strength];
        for (; columns[strength - 1] < limit; Combinadic.next(columns), ++hint) {
            for (int i = array.getSize(); i > 0; i--) {
                for (int j = strength; j > 0; j--) {
                    symbols[j] = //TODO don't know what is being dereferenced here
                }
                //TODO hintGet returns Entry
                //Array<int[]> x = new Array<>();
                if (coverage.hintGet(hint, columns, vectToArr(firsts), vectToArr(counts), symbols).op_getContent().equals(1)) {
                    Integer tmp = result.get(i);
                    tmp++;
                    result.set(i, tmp);
                }
            }
        }
        return result;
    }

    private Vector<Integer> arrToVect(int[] arr) {
        Vector<Integer> vect = new Vector<>(arr.length);
        for (int i = 0; i < arr.length; i++) {
            vect.set(i, arr[i]);
        }
        return vect;
    }

    private int[] vectToArr(Vector<Integer> vect) {
        int[] result = new int[vect.size()];
        for (int i = vect.size(); i > 0; i--) {
            result[i] = vect.get(i);
        }
        return result;
    }

    //TODO not sure what this operators are comparing

    //C_CODE
//    bool CoveringArray::operator <(const CoveringArray&other) const {
//        return this < &other;
//    }
    public boolean op_isLess(CoveringArray other) {
        return array.getSize() < other.array.getSize();
    }

    //C_CODE
//    bool CoveringArray::operator >(const CoveringArray&other) const {
//        return this > &other;
//    }
    public boolean op_isGreater(CoveringArray other) {
        return array.getSize() > other.array.getSize();
    }

    //C_CODE
//    bool CoveringArray::operator ==(const CoveringArray&other) const {
//        return this == &other;
//    }
    public boolean op_equals(CoveringArray other) {
        return array.getSize() == other.array.getSize();
    }

    //C_CODE
//    bool CoveringArray::operator !=(const CoveringArray&other) const {
//        return this != &other;
//    }
    public boolean op_notEquals(CoveringArray other) {
        return array.getSize() != other.array.getSize();
    }

    public void finalizeSubstitutions() {
        Integer outer = getRows();
        Integer inner = getOptions();
        Array<Array<Integer>> replacement = new Array<>(outer);
        for (int i = outer; i > 0; i--) {
            replacement.set(i, new Array<>(inner));
            for (int j = inner; j > 0; j--) {
                replacement.get(i).set(j, array.get(i).get(j));
            }
        }
        for () {
            //TODO iterators
        }
        substitutions.getImplementation().clear();
        array.op_arrayCopy(replacement);
    }

    public void autoFinalizeSubstitutions() {
        if (substitutions.getImplementation().size() > MAXIMUM_COVERING_ARRAY_SUBSTITUTION) {
            finalizeSubstitutions();
        }
    }

    public boolean isTrackingCoverage() {
        return trackingCoverage;
    }

    @SuppressWarnings("Duplicates")
    public void setTrackingCoverage(boolean trackingCoverage) {
        if (this.trackingCoverage) {
            this.trackingCoverage = trackingCoverage;
            return;
        }
        this.trackingCoverage = trackingCoverage;
        if (trackingCoverage) {
            Integer strength = coverage.getStrength();
            Integer limit = coverage.getOptions().getSize();

            Vector<Integer> firsts = coverage.getOptions().getFirstSymbols();
            Vector<Integer> counts = coverage.getOptions().getSymbolCounts();
            coverage.fill(0);
            coverageCount = 0;
            multipleCoverageCount = 0;

            if (substitutions.getImplementation().size() > 0) {
                Integer hint = 0;
                int [] columns = Combinadic.begin(strength);
                int [] symbols = new int[strength];
                //TODO not sure if arrays are updated in for loop
                for (; columns[strength - 1] < limit; Combinadic.next(columns), ++hint) {
                    for (int i = array.getSize(); i > 0; i--) {
                        for (int j = strength; j > 0; j--) {
                            symbols[j] = //TODO don't know what is being dereferenced here
                        }
                        //TODO object casted to int
                        int tmp = (int) coverage.hintGet(hint, columns, vectToArr(firsts), vectToArr(counts), symbols).op_getContent();
                        int newCoverage = ++tmp;
                        if (newCoverage == 1) {
                            ++coverageCount;
                        }
                        if (newCoverage > 1) {
                            ++multipleCoverageCount;
                        }
                    }
                }
            } else {
                // A special common case where we can bypass the () operator:
                Integer hint = 0;
                int[] columns = Combinadic.begin(strength);
                int[] symbols = new int[strength];
                for (; columns[strength - 1] < limit; Combinadic.next(columns), ++hint) {
                    for (int i = array.getSize(); i > 0; i--) {
                        for (int j = strength; j > 0; j--) {
                            symbols[j] = //TODO don't know what is being dereferenced here
                        }
                        //TODO object casted to int
                        int tmp = (int) coverage.hintGet(hint, columns, vectToArr(firsts), vectToArr(counts), symbols).op_getContent();
                        int newCoverage = ++tmp;
                        if (newCoverage == 1) {
                            ++coverageCount;
                        }
                        if (newCoverage > 1) {
                            ++multipleCoverageCount;
                        }
                    }
                }
            }
        }
    }

    public boolean isTrackingNoncoverage() {
        return trackingNoncoverage;
    }

    public void setTrackingNoncoverage(boolean trackingNoncoverage) {
        if (this.trackingNoncoverage) {
            this.trackingNoncoverage = trackingNoncoverage;
            if (!trackingNoncoverage) {
                noncoverage.getImplementation().clear();
            }
            return;
        }
        this.trackingNoncoverage = trackingNoncoverage;
        if (trackingNoncoverage) {
            assert (trackingCoverage);
            assert (noncoverage.getImplementation().isEmpty());
            int impossible = 0;
            for () {
                //TODO iterator
            }
            assert ((coverageCount + noncoverage.getImplementation().size() + impossible)
                    == coverage.getSize());
        }
    }

    public Set<Array<Integer>> getNoncoverage() {
        return noncoverage.getImplementation();
    }

    public Coverage getCoverage() {
        return coverage;
    }

    public SATSolver getSolver() {
        return solver;
    }

    public Integer getArrayValue(Integer row, Integer option) {
        return array.get(row).get(option);
    }
}
