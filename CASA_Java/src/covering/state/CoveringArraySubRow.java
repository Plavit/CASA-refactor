package covering.state;

import common.utility.Array;
import common.utility.CombinadicIterator;
import covering.bookkeeping.Entry;
import covering.bookkeeping.Options;

import java.util.Map;
import java.util.Vector;

// Warning: CoveringArray::SubRow assumes that constraints are always
// satisfied.
class CoveringArraySubRow {

    private CoveringArray owner;
    private Integer row;
    private Array<Integer> columns;

    public CoveringArraySubRow(CoveringArray owner, Integer row, Array<Integer> columns) {
        this.owner = owner;
        this.row = row;
        this.columns = columns;
    }

    @SuppressWarnings("Duplicates")
    protected void updateTracking(Array<Integer> values) {
        assert(values.getSize() == columns.getSize());
        Options options = owner.getCoverage().getOptions();
        Integer strength = owner.getCoverage().getStrength();
        Integer limit = options.getSize();
        Integer changes = 0;
        int[] oldRow = new int[limit];
        int[] newRow = new int[limit];
        Array<Integer> changedColumns = new Array<>(columns.getSize());
        for (int i = limit; i > 0; i--) {
            oldRow[i] = owner.getEntry(row, i).op_getValue();
        }
        for (int i = limit, j = values.getSize(); i > 0; i--) {
            if ((j > 0) && (columns.getArray().get(j - 1) == i)) {
                newRow[i] = values.getArray().get(--j);
            } else {
                newRow[i] = oldRow[i];
            }
        }
        for (int i = 0; i < limit; ++i) {
            if (newRow[i] != oldRow[i]) {
                changedColumns.getArray().set(changes++, i);
            }
        }
        changedColumns = new Array<>(changedColumns.getArray(), changes);
        Vector<Integer> firsts = options.getFirstSymbols();
        Vector<Integer> counts = options.getSymbolCounts();
        CombinadicIterator combo = new CombinadicIterator(
                limit, strength, vectToArr(changedColumns.getArray()));
        //TODO ???
        for (TODO;
        combo.op_bool();
        combo.op_pre_inc()) {
            Vector<Integer> updateColumns = arrToVect(combo.op_dereference());
            int[] oldSymbols = new int[strength];
            int[] newSymbols = new int[strength];
            for (int j = strength; j > 0; j--) {
                int column = updateColumns.get(j);
                oldSymbols[j] = oldRow[column];
                newSymbols[j] = newRow[column];
            }
            Entry<Integer> lost =
                    owner.getCoverage().hintGet(updateColumns, firsts, counts, arrToVect(oldSymbols));
            //TODO ???
            //assert(lost);
            assert(lost.op_getContent() > 0); // Assert that what we lost is something we had to lose.
            //TODO
            lost.op_decrement();
            if (lost.op_getContent() == 0) {
                --owner.coverageCount;
                if (owner.trackingNoncoverage) {
                    int[] separateCopyOfSymbols = new int[oldSymbols.length];
                    for (int j = oldSymbols.length; j > 0; j--) {
                        separateCopyOfSymbols[j] = oldSymbols[j];
                    }
                    //TODO
                    //bool successfulInsertion = owner.noncoverage->insert(separateCopyOfSymbols).second;
                    boolean successfulInsertion =
                    assert(successfulInsertion);
                }
            } else {
                --owner.multipleCoverageCount;
            }
            Entry<Integer> gained =
                    owner.getCoverage().hintGet(updateColumns, firsts, counts, arrToVect(newSymbols));
            //TODO
            gained.op_increment();
            if (gained.op_getContent() == 1) {
                ++owner.coverageCount;
                if (owner.trackingNoncoverage) {
                    int[] separateCopyOfSymbols = new int[newSymbols.length];
                    for (int j = newSymbols.length; j > 0; j--) {
                        separateCopyOfSymbols[j] = newSymbols[j];
                    }
                    //TODO
                    //bool successfulErasure = (bool)owner.noncoverage->erase(separateCopyOfSymbols);
                    boolean successfulErasure =
                    assert(successfulErasure);
                }
            } else {
                ++owner.multipleCoverageCount;
            }
        }
        owner.autoFinalizeSubstitutions();
    }

    private int[] vectToArr(Vector<Integer> vect) {
        int[] result = new int[vect.size()];
        for (int i = vect.size(); i > 0; i--) {
            result[i] = vect.get(i);
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
    public Array<Integer> op_getValue() {
        //TODO
        Map<RowOptionPair, Integer> end = owner.TODO;
        int size = columns.getSize();
        Array<Integer> result = new Array<>(size);
        RowOptionPair key = new RowOptionPair(row, 0);
        for (int i = size; i > 0; i--) {
            key.setOption(columns.getArray().get(i));
            Map<RowOptionPair, Integer> substitution = owner.TODO;
            //TODO ???
            result.getArray().set(i, (substitution.equals(end)) ?
                    owner.array.getArray().get(row).getArray().get(key.getOption()) :
            substitution.TODO);
        }
        return result;
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
    public CoveringArraySubRow op_setValue(Array<Integer> values) {
        assert(values.getSize() == columns.getSize());
        if (owner.trackingCoverage) {
            updateTracking(values);
        }
        RowOptionPair key = new RowOptionPair(row, 0);
        for (int i = columns.getSize(); i > 0; i--) {
            key.setOption(columns.getArray().get(i));
            //TODO ???
            owner.substitutions.getImplementation().get(key) = values.getArray().get(i);
            }
        return this;
    }
}