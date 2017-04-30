package covering.state;

import common.utility.Combinadic;
import covering.bookkeeping.Coverage;
import covering.bookkeeping.Entry;
import sat.InputKnown;

import java.util.Map;
import java.util.Vector;

class CoveringArrayEntry {

    private CoveringArray owner;
    private Integer row;
    private Integer option;

    public CoveringArrayEntry(CoveringArray owner, Integer row, Integer option) {
        this.owner = owner;
        this.row = row;
        this.option = option;
    }

    @SuppressWarnings("Duplicates")
    private void updateTracking(Integer value) {
        if (owner.getEntry(row, option).op_getValue() == value) {
            return;
        }
        int strength = owner.getCoverage().getStrength();
        int limit = owner.getCoverage().getOptions().getSize();
        Vector<Integer> firsts = owner.getCoverage().getOptions().getFirstSymbols();
        Vector<Integer> counts = owner.getCoverage().getOptions().getSymbolCounts();
        int hint = 0;
        for (int[] columns = Combinadic.begin(strength),
             symbols = new int[strength];
             columns[strength - 1] < limit;
             Combinadic.next(columns), ++hint) {
            for (int i = strength; i > 0; i--) {
                if (columns[i] == option) {
                    for (int j = strength; j > 0; j--) {
                        symbols[j] = owner.getEntry(row, columns[j]).op_getValue();
                    }

                    // TODO need SAT
                    InputKnown oldKnown(symbols);
                    if (owner.getSolver().solve(oldKnown) &&
                            owner.getCoverage().hintGet(hint, arrToVect(columns),
                                    firsts, counts, arrToVect(symbols)).op_decrement().equals(false)) {
                        --owner.coverageCount;
                        if (owner.trackingNoncoverage) {
                            int[] separateCopyOfSymbols = new int[symbols.length];
                            for (int j = symbols.length; j > 0; j--) {
                                separateCopyOfSymbols[j] = symbols[j];
                            }

                            //TODO
                            //bool successfulInsertion = owner.noncoverage->insert(separateCopyOfSymbols).second;
                            boolean successfulInsertion =
                            assert (successfulInsertion);
                        }
                    } else {
                        --owner.multipleCoverageCount;
                    }
                    symbols[i] = value;
                    InputKnown newKnown(symbols);
                    if (owner.getSolver().solve(newKnown) &&
                            owner.getCoverage().hintGet(hint, arrToVect(columns),
                                    firsts, counts, arrToVect(symbols)).op_increment().equals(true)){
                        ++owner.coverageCount;
                        if (owner.trackingNoncoverage) {
                            int[] separateCopyOfSymbols = new int[symbols.length];
                            for (int j = symbols.length; j > 0; j--) {
                                separateCopyOfSymbols[j] = symbols[j];
                            }
                            //TODO
                            //bool successfulErasure = (bool)owner.noncoverage->erase(separateCopyOfSymbols);
                            boolean successfulErasure =
                            assert (successfulErasure);
                        }
                    } else {
                        ++owner.multipleCoverageCount;
                    }
                    break;
                }
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
//        CoveringArray::Entry::operator int() const {
//            map<pair<int, int>, int>::const_iterator
//                    substitution =
//                    owner.substitutions->find(pair<int, int>(row,option)),
//            end = owner.substitutions->end();
//            return (substitution == end) ?
//                    owner.array[row][option] :
//                    substitution->second;
//        }
    public Integer op_getValue() {
        //TODO
        //http://www.cplusplus.com/reference/map/map/find/   ...find returns iterator
        Map<RowOptionPair, Integer> substitution = owner.substitutions.getImplementation().TODO
        Map<RowOptionPair, Integer> end = owner.substitutions.getImplementation().TODO
        return (substitution.equals(end)) ? owner.getArrayValue(row, option) : substitution.TODO;
    }

    //C_CODE
//        CoveringArray::Entry&CoveringArray::Entry::operator =(int value) {
//            if (owner.trackingCoverage) {
//                updateTracking(value);
//            }
//            (*owner.substitutions)[pair<int, int>(row, option)] = value;
//            return *this;
//        }
    public CoveringArrayEntry op_setValue(Integer value) {
        //TODO
        if (owner.trackingCoverage) {
            updateTracking(value);
        }
        owner.substitutions.set(new RowOptionPair(row, option))
        return this;
    }
}


