package covering.bookkeeping;

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

import common.utility.PascalTriangle;
import common.utility.SubstitutionArray;

import java.util.Vector;

public class Coverage<T> {

    protected Integer strength;
    protected Options options;
    protected Vector<Integer> offsets;
    protected SubstitutionArray<T> contents;

    public Coverage(Integer strength, Options options) {
        this.strength = strength;
        this.options = options;
        this.offsets = new Vector<>(PascalTriangle.nCr(options.getSize(), strength));
        Integer size = 0;
        Integer offsetIndex = 0;
        for () {//TODO iterators
            Integer blockSize = 1;
            for (int i = strength; i > 0; i--) {
                blockSize *= options.getSymbolCount(columns.get(i));
            }
            offsets.set(offsetIndex, size);
            offsetIndex++;
        }
        contents = new SubstitutionArray<>(size); //TODO ???
    }

    public Coverage(Coverage copy) {
        this.strength = copy.getStrength();
        this.options = copy.getOptions();
        this.offsets = copy.getOffsets();
        this.contents = copy.getContents();
    }

    protected Integer encode(Integer indexHint,
                             Vector<Integer> columnsHint,
                             Vector<Integer> firstsHint,
                             Vector<Integer> countsHint,
                             Vector<Integer> sortedCombination) {
        assert(sortedCombination.size() == strength);
        assert(indexHint < offsets.size());
        Integer offset = offsets.get(indexHint);
        Integer index = 0;
        for (int i = strength; i > 0; i--) {
            int column = columnsHint.get(i);
            int base = firstsHint.get(column);
            int count = countsHint.get(column);
            index *= count;
            index += sortedCombination.get(i) - base;
        }
        return offset + index;
    }

    protected Integer encode(Vector<Integer> columnsHint,
                             Vector<Integer> firstsHint,
                             Vector<Integer> countsHint,
                             Vector<Integer> sortedCombination) {
        //TODO
    }

    protected Integer encode(Vector<Integer> sortedCombination) {
        //TODO
    }

    protected Vector<Integer> decode(Integer encoding) {
        //TODO
    }

    public Integer getStrength() {
        return strength;
    }

    public Options getOptions() {
        return options;
    }

    public Vector<Integer> getOffsets() {
        return offsets;
    }

    public SubstitutionArray<T> getContents() {
        return contents;
    }

    class Entry()

    {
        //TODO
    }

    //C_CODE
//    const Entry operator[](Array<unsigned>sortedCombination) const {
//        return Entry(*this, encode(sortedCombination));
//    }
    public Entry op_getEntry(Vector<Integer> sortedCombination) {
        //TODO
    }

    //C_CODE
//    Entry operator [](Array<unsigned>sortedCombination) {
//        return Entry(*this, encode(sortedCombination));
//    }
    //TODO ???

    public Entry hintGet(Integer indexHint,
                         Vector<Integer> columnsHint,
                         Vector<Integer> firstsHint,
                         Vector<Integer> countsHint,
                         Vector<Integer> sortedCombination) {
        //TODO
    }

    public Entry hintGet(Vector<Integer> columnsHint,
                         Vector<Integer> firstsHint,
                         Vector<Integer> countsHint,
                         Vector<Integer> sortedCombination) {
        //TODO
    }

    //TODO iterators

    public Integer getSize() {
        return contents.getSize();
    }

    public void fill(T filler) {
        contents.fill(filler);
    }
}
