package covering.state;

import common.utility.Array;
import common.utility.Combinadic;
import covering.state.CoveringArray;
import sat.InputKnown;

import java.util.Map;
import java.util.Vector;

// Warning: CoveringArray::Row assumes that constraints are always satisfied.
class CoveringArrayRow {

    private CoveringArray owner;
    private Integer row;

    public CoveringArrayRow(CoveringArray owner, Integer row) {
        this.owner = owner;
        this.row = row;
    }

    @SuppressWarnings("Duplicates")
    protected void updateTracking(Array<Integer> values) {
        Integer size = values.getSize();
        assert (size == owner.getOptions());
        Integer strength = owner.getCoverage().getStrength();
        Integer limit = owner.getCoverage().getOptions().getSize();
        int[] oldRow = new int[size];
        for (int i = size; i > 0; i--) {
            oldRow[i] = owner.getEntry(row, i).op_getValue();
        }
        Vector<Integer> firsts = owner.getCoverage().getOptions().getFirstSymbols();
        Vector<Integer> counts = owner.getCoverage().getOptions().getSymbolCounts();
        int hint = 0;
        for (int[]
             columns = Combinadic.begin(strength),
             oldSymbols = new int[strength],
             newSymbols = new int[strength];
             columns[strength - 1] < limit;
             Combinadic.next(columns), ++hint) {
            boolean unchanged = true;
            for (int j = strength; j > 0; j--) {
                int column = columns[j];
                oldSymbols[j] = oldRow[column];
                newSymbols[j] = values.getArray().get(column);
                if (oldSymbols[j] != newSymbols[j]) {
                    unchanged = false;
                }
            }
            if (unchanged) {
                continue;
            }
            //TODO need SAT
            InputKnown oldKnown (oldSymbols);
            InputKnown newKnown (newSymbols);
            if (owner.getCoverage().hintGet(hint, arrToVect(columns),
                    firsts, counts, arrToVect(oldSymbols)).op_decrement().equals(false)) {
                --owner.coverageCount;
                if (owner.trackingNoncoverage) {
                    int[] separateCopyOfSymbols = new int[oldSymbols.length];
                    for (int j = oldSymbols.length; j > 0; j--) {
                        separateCopyOfSymbols[j] = oldSymbols[j];
                    }
                    boolean successfulInsertion =
                    //TODO
                    //owner.noncoverage->insert(separateCopyOfSymbols).second;
                    assert (successfulInsertion);
                }
            } else {
                --owner.multipleCoverageCount;
            }
            if (owner.getCoverage().hintGet(hint, arrToVect(columns),
                    firsts, counts, arrToVect(newSymbols)).op_increment().equals(true)) {
                ++owner.coverageCount;
                if (owner.trackingNoncoverage) {
                    int[] separateCopyOfSymbols = new int[newSymbols.length];
                    for (int j = newSymbols.length; j > 0; j--) {
                        separateCopyOfSymbols[j] = newSymbols[j];
                    }
                    boolean successfulErasure =
                    //TODO
                    //(bool)owner.noncoverage->erase(separateCopyOfSymbols);
                    assert (successfulErasure);
                }
            } else {
                ++owner.multipleCoverageCount;
            }
        }
        owner.autoFinalizeSubstitutions();
    }

    private Vector<Integer> arrToVect(int[] arr) {
        Vector<Integer> vect = new Vector<>(arr.length);
        for (int i = 0; i < arr.length; i++) {
            vect.set(i, arr[i]);
        }
        return vect;
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
    public Array<Integer> op_getValue() {
        //TODO
        Map<RowOptionPair, Integer> end = owner.TODO;
        int[] result = new int[owner.getOptions()];
        RowOptionPair key = new RowOptionPair(row, result.length);
        for (int keyOption = key.getOption(); keyOption > 0; keyOption--) {
            Map<RowOptionPair, Integer> substitution = owner.substitutions.TODO;
            result[keyOption] = (substitution.equals(end) ?
                    owner.getArrayValue(row, keyOption) : substitution.TODO)
        }
        return new Array<>(arrToVect(result),result.length);
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
    public CoveringArrayRow op_setValue(Array<Integer> values) {
        if (owner.trackingCoverage) {
            updateTracking(values);
        }
        RowOptionPair key = new RowOptionPair(row, owner.getOptions());
        for (int keyOption = key.getOption(); keyOption > 0; keyOption--) {
            //TODO not sure about this
            owner.substitutions.getImplementation().get(key).equals(values.getArray().get(keyOption));
        }
        return this;
    }
}
