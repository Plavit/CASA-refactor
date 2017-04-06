package covering.state;

// Copyright 2008, 2009 Brady J. Garvin

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import covering.bookkeeping.Coverage;
import covering.bookkeeping.Options;
import javafx.util.Pair;


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

public class CoveringArray implements Comparable<CoveringArray> {
    /* TODO */
    public int compareTo(CoveringArray o) {
        return 0;
    }

    //MAXIMUM_COVERING_ARRAY_SUBSTITUTION 0x40
    public static int MAXIMUM_COVERING_ARRAY_SUBSTITUTION = 64;

    // The first index is the test configuration (row).
    // The second index is the option (column).
    protected Vector<Vector<Integer>> array;
    protected Lazy<Map<Pair<Integer, Integer>, Integer>> substitutions;
    protected SATSolver solver;
    protected boolean trackingCoverage;
    protected boolean trackingNoncoverage;
    protected Integer coverageCount;
    protected Integer multipleCoverageCount;
    protected Coverage<Integer> coverage;
    protected Lazy<Set<Vector<Integer>, ArrayComparator<Integer>>> noncoverage;

    public CoveringArray(Integer rows, Integer strength, Options options, SATSolver solver) {

        array = new Vector<>(rows);

        //C_CODE
        //substitutions(new map<pair<unsigned, unsigned>, unsigned>()),
        //TODO Lazy
        substitutions = new Lazy(new HashMap<Pair<Integer, Integer>, Integer>());

        this.solver = solver;
        this.trackingCoverage = false;
        this.trackingNoncoverage = false;
        this.coverageCount = 0;
        this.multipleCoverageCount = 0;
        this.coverage = new Coverage(strength, options);

        //C_CODE
        //noncoverage(new set<Array<unsigned>, ArrayComparator<unsigned> >()) {
        //  for (unsigned i = rows; i--;) {
        //      array[i] = Array<unsigned>(options.getSize());
        //  }
        //  coverage.fill(0);
        //}
        //TODO Lazy
        this.noncoverage = new Lazy(new Set<Vector<Integer>, ArrayComparator<Integer>>());
        for (int i = rows; i > 0; i--) {
            array.set(i, new Vector<Integer>(options.getSize()));
        }
        coverage.fill(0);
    }

    public CoveringArray(CoveringArray copy) {

        this.array = copy.array;
        this.substitutions = copy.substitutions;
        this.solver = copy.solver;
        this.trackingCoverage = copy.trackingCoverage;
        this.trackingNoncoverage = copy.trackingNoncoverage;
        this.coverageCount = copy.coverageCount;
        this.multipleCoverageCount = copy.multipleCoverageCount;
        this.coverage = copy.coverage;
        this.noncoverage = copy.noncoverage;
        assert (!array.isEmpty()); //TODO ???
    }

    public Integer getRows() {
        return array.size();
    }

    public Integer getOptions() {
        return (array.size() != 0) ? array.get(0).size() : 0;
    }

    public void setBackingArrayEntry(Integer row, Integer option, Integer value) {
        assert (!substitutions.getSize());

        Vector tmp = array.get(row);
        tmp.set(option, value);
        array.set(row, tmp); //TODO not sure
    }

    public void setBackingArrayRow(Integer row, Vector<Integer> value) {
        assert (!substitutions.getSize());
        array.set(row, value);
    }

    class Entry {

        protected CoveringArray owner;
        protected Integer row;
        protected Integer option;

        public Entry(CoveringArray owner, Integer row, Integer option) {
            this.owner = owner;
            this.row = row;
            this.option = option;
        }

        protected void updateTracking(Integer value) {
            //TODO c_kod v CoveringArrayEntry.C
        }

        //C_CODE
//        CoveringArray::Entry::operator unsigned() const {
//            map<pair<unsigned, unsigned>, unsigned>::const_iterator
//                    substitution =
//                    owner.substitutions->find(pair<unsigned, unsigned>(row,option)),
//            end = owner.substitutions->end();
//            return (substitution == end) ?
//                    owner.array[row][option] :
//                    substitution->second;
//        }
        public void op_call() { //TODO

        }

        //C_CODE
//        CoveringArray::Entry&CoveringArray::Entry::operator =(unsigned value) {
//            if (owner.trackingCoverage) {
//                updateTracking(value);
//            }
//            (*owner.substitutions)[pair<unsigned, unsigned>(row, option)] = value;
//            return *this;
//        }
        public Entry op_TODO2(Integer value) { //TODO

        }
    }

    // Warning: CoveringArray::Row assumes that constraints are always satisfied.
    class Row {

        protected CoveringArray owner;
        protected Integer row;

        public Row(CoveringArray owner, Integer row) {
            this.owner = owner;
            this.row = row;
        }

        protected void updateTracking(Vector<Integer> values) {
            //TODO c_kod v CoveringArrayRow.C
        }

        //C_CODE
//        CoveringArray::Row::operator Array<unsigned>() const {
//            typedef map<pair<unsigned,unsigned>,unsigned>::const_iterator Substitution;
//            Substitution end = owner.substitutions->end();
//            Array<unsigned>result(owner.getOptions());
//            for (pair<unsigned, unsigned>key(row, result.getSize()); key.second--;) {
//                Substitution substitution = owner.substitutions->find(key);
//                result[key.second] =
//                        (substitution == end) ?
//                                owner.array[row][key.second] :
//                                substitution->second;
//            }
//            return result;
//        }
        public void op_TODO() {
            //TODO
        }

        //C_CODE
//        CoveringArray::Row&CoveringArray::Row::operator =(const Array<unsigned>values) {
//            if (owner.trackingCoverage) {
//                updateTracking(values);
//            }
//            for (pair<unsigned, unsigned>key(row, owner.getOptions()); key.second--;) {
//                (*owner.substitutions)[key] = values[key.second];
//            }
//            return *this;
//        }
        public Row op_TODO3(Vector<Integer> values) {
            //TODO
        }
    }

    // Warning: CoveringArray::SubRow assumes that constraints are always
    // satisfied.
    class SubRow {

        protected CoveringArray owner;
        protected Integer row;
        protected Vector<Integer> columns;

        public SubRow(CoveringArray owner, Integer row, Vector<Integer> columns) {
            this.owner = owner;
            this.row = row;
            this.columns = columns;
        }

        protected void updateTracking(Vector<Integer> values) {
            //TODO c_kod v CoveringArraySubRow.C
        }

        //C_CODE
//        CoveringArray::SubRow::operator Array<unsigned>() const {
//            typedef map<pair<unsigned, unsigned>, unsigned>::const_iterator Substitution;
//            Substitution end = owner.substitutions->end();
//            unsigned size = columns.getSize();
//            Array<unsigned>result(size);
//            pair<unsigned, unsigned>key(row,0);
//            for (unsigned i = size; i--;) {
//                key.second = columns[i];
//                Substitution substitution = owner.substitutions->find(key);
//                result[i] =
//                        (substitution == end) ?
//                                owner.array[row][key.second] :
//                                substitution->second;
//            }
//            return result;
//        }
        public void op_TODO() {
            //TODO
        }

        //C_CODE
//        CoveringArray::SubRow&CoveringArray::SubRow::operator =
//                (const Array<unsigned>values) {
//            assert(values.getSize() == columns.getSize());
//            if (owner.trackingCoverage) {
//                updateTracking(values);
//            }
//            pair<unsigned, unsigned>key(row, 0);
//            for (unsigned i = columns.getSize(); i--;) {
//                key.second = columns[i];
//                (*owner.substitutions)[key] = values[i];
//            }
//            return *this;
//        }
        public void op_TODO2() {
            //TODO
        }
    }

    //C_CODE
    //Entry operator ()(unsigned row, unsigned option) {
    //  return Entry(*this, row, option);
    //}
    //TODO

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

    public Vector<Integer> countDistinctCoverage() {
        assert (trackingCoverage);
        Vector<Integer> result = new Vector<>(array.size());
        //C_CODE
        //result.fill(0);
        for (int i = 0; i < result.size(); i++) {
            result.set(i, 0);
        }
        Integer strength = coverage.getStrength();
        Integer limit = coverage.getOptions().getSize();

        Vector<Integer> first = coverage.getOptions().getFirstSysmbols();
        Vector<Integer> counts = coverage.getOptions().getSymbolCounts();
        Integer hint = 0;

        //TODO
    }

    //C_CODE
//    bool CoveringArray::operator <(const CoveringArray&other) const {
//        return this < &other;
//    }
    public boolean op_isLess(CoveringArray other) {
        return array.size() < other.array.size(); //TODO ???
    }

    //C_CODE
//    bool CoveringArray::operator >(const CoveringArray&other) const {
//        return this > &other;
//    }
    public boolean op_isGreater(CoveringArray other) {
        return array.size() > other.array.size(); //TODO ???
    }

    //C_CODE
//    bool CoveringArray::operator ==(const CoveringArray&other) const {
//        return this == &other;
//    }
    public boolean op_equals(CoveringArray other) {
        //TODO
    }

    //C_CODE
//    bool CoveringArray::operator !=(const CoveringArray&other) const {
//        return this != &other;
//    }
    public boolean op_notEquals(CoveringArray other) {
        //TODO
    }

    public void finalizeSubstitutions() {
        //TODO
    }

    public void autoFinalizeSubstitutions() {
        if (substitutions.getSize() > MAXIMUM_COVERING_ARRAY_SUBSTITUTION) {
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
            Vector<Integer> firsts = coverage.getOptions().getFirstSymbols();
            Vector<Integer> counts = coverage.getOptions().getSymbolCounts();
            coverage.fill(0);
            //TODO class Coverage needed first


        }
    }

    public boolean isTrackingNoncoverage() {
        return trackingNoncoverage;
    }

    public void setTrackingNoncoverage(boolean trackingNoncoverage) {
        if (this.trackingNoncoverage) {
            this.trackingNoncoverage = trackingNoncoverage;
            if (!trackingNoncoverage) {
                noncoverage.clear();
            }
            return;
        }
        this.trackingNoncoverage = trackingNoncoverage;
        if (trackingNoncoverage) {
            assert (trackingCoverage);
            assert (noncoverage.empty());
            int impossible = 0;
            for () {
                //TODO Coverage iterator needed
            }
            assert ((coverageCount + noncoverage.size() + impossible)
                    == coverage.getSize());
        }
    }

    public Set<Vector<Integer>, ArrayComparator<Integer>> getNoncoverage() {
        return noncoverage.getSet();
    }
}
