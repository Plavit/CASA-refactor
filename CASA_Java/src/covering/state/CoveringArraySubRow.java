package covering.state;

import common.utility.Array;

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

    protected void updateTracking(Array<Integer> values) {
        Integer size = values.length;
        assert (size == owner.getOptions());
        Integer strength = owner.coverage.getStrength();
        Integer limit = owner.coverage.getOptions().getSize();
        Integer[] oldRow = new Integer[size];
        for (Integer i = size; i > 0;i--) {
            oldRow[i] = owner(row, i);
        }
        //TODO dokoncit prepis nasledujiciho kodu

/*
  assert(values.getSize() == columns.getSize());
  const Options&options = owner.coverage.getOptions();
  unsigned strength = owner.coverage.getStrength();
  unsigned limit = options.getSize();
  unsigned changes = 0;
  Array<unsigned>oldRow(limit);
  Array<unsigned>newRow(limit);
  Array<unsigned>changedColumns(columns.getSize());
  for (unsigned i = limit; i--;) {
    oldRow[i] = owner(row, i);
  }
  for (unsigned i = limit, j = values.getSize(); i--;) {
    if (j && (columns[j - 1] == i)) {
      newRow[i] = values[--j];
    } else {
      newRow[i] = oldRow[i];
    }
  }
  for (unsigned i = 0; i < limit; ++i) {
    if (newRow[i] != oldRow[i]) {
      changedColumns[changes++] = i;
    }
  }
  changedColumns = Array<unsigned>(changedColumns, changes);
  Array<unsigned>firsts = options.getFirstSymbols();
  Array<unsigned>counts = options.getSymbolCounts();
  for (CombinadicIterator combo(limit, strength, changedColumns);
       combo;
       ++combo) {
    const Array<unsigned>updateColumns = *combo;
    Array<unsigned>oldSymbols(strength);
    Array<unsigned>newSymbols(strength);
    for (unsigned j = strength; j--;) {
      unsigned column = updateColumns[j];
      oldSymbols[j] = oldRow[column];
      newSymbols[j] = newRow[column];
    }
    Coverage<unsigned>::Entry lost =
      owner.coverage.hintGet(updateColumns, firsts, counts, oldSymbols);
    assert(lost); // Assert that what we lost is something we had to lose.
    if (--lost == 0) {
      --owner.coverageCount;
      if (owner.trackingNoncoverage) {
	Array<unsigned>separateCopyOfSymbols(oldSymbols.getSize());
	for (unsigned j = oldSymbols.getSize(); j--;) {
	  separateCopyOfSymbols[j] = oldSymbols[j];
	}
	bool successfulInsertion =
	  owner.noncoverage->insert(separateCopyOfSymbols).second;
	assert(successfulInsertion);
	(void)successfulInsertion; // This is unused without assertions.
      }
    } else {
      --owner.multipleCoverageCount;
    }
    Coverage<unsigned>::Entry gained =
      owner.coverage.hintGet(updateColumns, firsts, counts, newSymbols);
    if (++gained == 1) {
      ++owner.coverageCount;
      if (owner.trackingNoncoverage) {
	Array<unsigned>separateCopyOfSymbols(newSymbols.getSize());
	for (unsigned j = newSymbols.getSize(); j--;) {
	  separateCopyOfSymbols[j] = newSymbols[j];
	}
	bool successfulErasure =
	  (bool)owner.noncoverage->erase(separateCopyOfSymbols);
	assert(successfulErasure);
	(void)successfulErasure; // This is unused without assertions.
      }
    } else {
      ++owner.multipleCoverageCount;
    }
  }
  owner.autoFinalizeSubstitutions();
 */
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
    public Array<Integer> op_get() {
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
    public CoveringArraySubRow op_set(Array<Integer> values) {
        //TODO
    }
}