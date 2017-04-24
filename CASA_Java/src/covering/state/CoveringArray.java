package covering.state;

// Copyright 2008, 2009 Brady J. Garvin

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import common.utility.Array;
import common.utility.Lazy;
import covering.bookkeeping.Coverage;
import covering.bookkeeping.Options;
import javafx.util.Pair;
import sat.SATSolver;

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
    private final int row;
    private final int option;

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

    //TODO ??? set that consists of two types ???
    protected Lazy<Set<Array<Integer>, ArrayComparator<Integer>>> noncoverage;

    public CoveringArray(Integer rows, Integer strength, Options options, SATSolver solver) {

        this.array = new Array<>(rows);
        this.substitutions = new Lazy(new HashMap<RowOptionPair, Integer>());
        this.solver = solver;
        this.trackingCoverage = false;
        this.trackingNoncoverage = false;
        this.coverageCount = 0;
        this.multipleCoverageCount = 0;
        this.coverage = new Coverage(strength, options);

        //TODO ??? set that consists of two types ???
        this.noncoverage = new Lazy(new Set<Array<Integer>, ArrayComparator<Integer>>());
        for (int i = rows; i > 0; i--) {
            array.getArray().set(i, new Array<>(options.getSize()));
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
        assert (!array.getArray().isEmpty());
    }

    public Integer getRows() {
        return array.getSize();
    }

    public Integer getOptions() {
        return (array.getSize() != 0) ? array.getArray().get(0).getSize() : 0;
    }

    public void setBackingArrayEntry(Integer row, Integer option, Integer value) {
        assert (!(substitutions.getImplementation().size() > 0));
        array.getArray().get(row).getArray().set(option, value);
    }

    public void setBackingArrayRow(Integer row, Array<Integer> value) {
        assert (!(substitutions.getImplementation().size() > 0));
        array.getArray().set(row, value);
    }

    //C_CODE
    //Entry operator ()(unsigned row, unsigned option) {
    //  return Entry(*this, row, option);
    //}
    CoveringArrayEntry getEntry(int row, int option) {
        return null; // TODO
    }

    //C_CODE
    //const Entry operator ()(unsigned row, unsigned option) const {
    //  return Entry(*const_cast<CoveringArray*>(this), row, option);
    //}
    //TODO

    //C_CODE
    //Row operator ()(unsigned row) {
    //  return Row(*this, row);
    //}
    //TODO

    //C_CODE
    //const Row operator ()(unsigned row) const {
    //  return Row(*const_cast<CoveringArray*>(this), row);
    //}
    //TODO

    //C_CODE
    //SubRow operator ()(unsigned row, Array<unsigned>columns) {
    //  return SubRow(*this, row, columns);
    //}
    //TODO

    //C_CODE
    //const SubRow operator ()(unsigned row, Array<unsigned>columns) const {
    //  return SubRow(*const_cast<CoveringArray*>(this), row, columns);
    //}
    //TODO

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

        //TODO we need to unite usage of Vector and Array

        Vector<Integer> firsts = coverage.getOptions().getFirstSymbols();
        Vector<Integer> counts = coverage.getOptions().getSymbolCounts();
        Integer hint = 0;

        for () {
            //TODO iterators
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
            replacement.getArray().set(i, new Array<>(inner));
            for (int j = inner; j > 0; j--) {
                replacement.getArray().get(i).getArray().set(j, array.getArray().get(i).getArray().get(j));
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

    public void setTrackingCoverage(boolean trackingCoverage) {
        if (this.trackingCoverage) {
            this.trackingCoverage = trackingCoverage;
            return;
        }
        this.trackingCoverage = trackingCoverage;
        if (trackingCoverage) {
            Integer strength = coverage.getStrength();
            Integer limit = coverage.getOptions().getSize();

            //TODO we need to unite usage of Vector and Array

            Vector<Integer> firsts = coverage.getOptions().getFirstSymbols();
            Vector<Integer> counts = coverage.getOptions().getSymbolCounts();
            coverage.fill(0);
            coverageCount = 0;
            multipleCoverageCount = 0;

            if (substitutions.getImplementation().size() > 0) {
                Integer hint = 0;
                for () {
                    //TODO iterator
                }
            } else {
                // A special common case where we can bypass the () operator:
                Integer hint = 0;
                for () {
                    //TODO iterator
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

    //TODO ??? set that consists of two types ???
    public Set<Array<Integer>, ArrayComparator<Integer>> getNoncoverage() {
        return noncoverage.getImplementation();
    }

    public Coverage getCoverage() {
        return coverage;
    }

    public SATSolver getSolver() {
        return solver;
    }
}
