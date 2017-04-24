package covering.state;

import common.utility.Combinadic;
import covering.bookkeeping.Coverage;
import sat.InputKnown;

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

    private void updateTracking(Integer value) {
        if (owner.getEntry(row, option).getValue() == value) {
            return;
        }
        Coverage coverage = owner.getCoverage();
        int strength = coverage.getStrength();
        int limit = coverage.getOptions().getSize();
        Vector<Integer> firsts = coverage.getOptions().getFirstSymbols();
        Vector<Integer> counts = coverage.getOptions().getSymbolCounts();
        int hint = 0;
        int[] columns = Combinadic.begin(strength);
        int[] symbols = new int[strength];
        for (;
             columns[strength - 1] < limit ;
            Combinadic.next(columns), ++hint){
            for (int i = strength; i-- != 0; ) {
                if (columns[i] == option) {
                    for (int j = strength; j-- != 0; ) {
                        symbols[j] = owner.getEntry(row, columns[j]).getValue();
                    }
                    InputKnown oldKnown (symbols);
                    if (owner.getSolver().solve(oldKnown) &&
                            --coverage.hintGet(hint, columns, firsts, counts, symbols) ==
                                    0){
                        --owner.coverageCount;
                        if (owner.trackingNoncoverage) {
                            Array<int> separateCopyOfSymbols (symbols.getSize());
                            for (int j = symbols.getSize(); j--; ) {
                                separateCopyOfSymbols[j] = symbols[j];
                            }
                            bool successfulInsertion =
                                    owner.noncoverage->insert(separateCopyOfSymbols).second;
                            assert (successfulInsertion);
                            (void) successfulInsertion; // This is unused without assertions.
                        }
                    }else{
                        --owner.multipleCoverageCount;
                    }
                    symbols[i] = value;
                    InputKnown newKnown (symbols);
                    if (( * owner.solver) (newKnown) &&
                            ++coverage.hintGet(hint, columns, firsts, counts, symbols) ==
                                    1){
                        ++coverageCount;
                        if (owner.trackingNoncoverage) {
                            Array<int> separateCopyOfSymbols (symbols.getSize());
                            for (int j = symbols.getSize(); j--; ) {
                                separateCopyOfSymbols[j] = symbols[j];
                            }
                            bool successfulErasure =
                                    (bool) owner.noncoverage->erase(separateCopyOfSymbols);
                            assert (successfulErasure);
                            (void) successfulErasure; // This is unused without assertions.
                        }
                    }else{
                        ++owner.multipleCoverageCount;
                    }
                    break;
                }
            }
        }
        owner.autoFinalizeSubstitutions();
  /*
 */
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
    public Integer getValue() {
        //TODO
        return 0;
    }

    //C_CODE
//        CoveringArray::Entry&CoveringArray::Entry::operator =(int value) {
//            if (owner.trackingCoverage) {
//                updateTracking(value);
//            }
//            (*owner.substitutions)[pair<int, int>(row, option)] = value;
//            return *this;
//        }
    public CoveringArrayEntry setValue(Integer value) { //TODO
        if (owner.trackingCoverage) {
            updateTracking(value);
        }
        owner.substitutions.set(new RowOptionPair(row, option))

    }
}


