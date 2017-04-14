import covering.state.CoveringArray;

class CoveringArrayEntry {

    protected CoveringArray owner;
    protected Integer row;
    protected Integer option;

    public CoveringArrayEntry(CoveringArray owner, Integer row, Integer option) {
        this.owner = owner;
        this.row = row;
        this.option = option;
    }

    protected void updateTracking(Integer value) {
        //TODO dokoncit prepis nasledujiciho kodu
/*
  if (owner(row,option) == value) {
    return;
  }
  unsigned strength = owner.coverage.getStrength();
  unsigned limit = owner.coverage.getOptions().getSize();
  Array<unsigned>firsts = owner.coverage.getOptions().getFirstSymbols();
  Array<unsigned>counts = owner.coverage.getOptions().getSymbolCounts();
  unsigned hint = 0;
  for (Array<unsigned>
	 columns = combinadic.begin(strength),
	 symbols(strength);
       columns[strength - 1] < limit;
       combinadic.next(columns), ++hint) {
    for (unsigned i = strength; i--;) {
      if (columns[i] == option) {
	for (unsigned j = strength; j--;) {
	  symbols[j] = owner(row, columns[j]);
	}
	InputKnown oldKnown(symbols);
	if ((*owner.solver)(oldKnown) &&
	    --owner.coverage.hintGet(hint, columns, firsts, counts, symbols) ==
	    0) {
	  --owner.coverageCount;
	  if (owner.trackingNoncoverage) {
	    Array<unsigned>separateCopyOfSymbols(symbols.getSize());
	    for (unsigned j = symbols.getSize(); j--;) {
	      separateCopyOfSymbols[j] = symbols[j];
	    }
	    bool successfulInsertion =
	      owner.noncoverage->insert(separateCopyOfSymbols).second;
	    assert(successfulInsertion);
	    (void)successfulInsertion; // This is unused without assertions.
	  }
	} else {
	  --owner.multipleCoverageCount;
	}
	symbols[i] = value;
	InputKnown newKnown(symbols);
	if ((*owner.solver)(newKnown) &&
	    ++owner.coverage.hintGet(hint, columns, firsts, counts, symbols) ==
	    1) {
	  ++owner.coverageCount;
	  if (owner.trackingNoncoverage) {
	    Array<unsigned>separateCopyOfSymbols(symbols.getSize());
	    for (unsigned j = symbols.getSize(); j--;) {
	      separateCopyOfSymbols[j] = symbols[j];
	    }
	    bool successfulErasure =
	      (bool)owner.noncoverage->erase(separateCopyOfSymbols);
	    assert(successfulErasure);
	    (void)successfulErasure; // This is unused without assertions.
	  }
	} else {
	  ++owner.multipleCoverageCount;
	}
	break;
      }
    }
  }
  owner.autoFinalizeSubstitutions();
 */
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


