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

import common.utility.Combinadic;
import common.utility.PascalTriangle;
import common.utility.SubstitutionArray;

public class Coverage<T extends Comparable<T>> {

    private Integer strength;
    private Options options;
    private int[] offsets;
    private SubstitutionArray<T> contents;

    public Coverage(Integer strength, Options options) {
        this.strength = strength;
        this.options = options;
        this.offsets = new int[PascalTriangle.nCr(options.getSize(), strength)];
        int size = 0;
        int offsetIndex = 0;
        for (int[] columns = Combinadic.begin(strength);
                columns[strength - 1] < options.getSize();
                Combinadic.next(columns)) {
            int blockSize = 1;
            for (int i = strength; i > 0; i--) {
                blockSize *= options.getSymbolCount(columns[i]);
            }
            offsets[offsetIndex] = size;
            offsetIndex++;
            size += blockSize;
        }
        contents = new SubstitutionArray<>(size);
    }

    public Coverage(Coverage<T> copy) {
        this.strength = copy.getStrength();
        this.options = copy.getOptions();
        this.offsets = copy.offsets;
        this.contents = copy.contents;
    }

    public int encode(int indexHint,
                         int[] columnsHint,
                         int[] firstsHint,
                         int[] countsHint,
                         int[] sortedCombination) {
        assert (sortedCombination.length == strength);
        assert (indexHint < offsets.length);
        int offset = offsets[indexHint];
        int index = 0;
        for (int i = strength; i > 0; i--) {
            int column = columnsHint[i];
            int base = firstsHint[column];
            int count = countsHint[column];
            index *= count;
            index += sortedCombination[i] - base;
        }
        return offset + index;
    }

    public int encode(int[] columnsHint,
                             int[] firstsHint,
                             int[] countsHint,
                             int[] sortedCombination) {
        return encode(Combinadic.encode(columnsHint),
                        columnsHint,
                        firstsHint,
                        countsHint,
                        sortedCombination);
    }

    public int encode(int[] sortedCombination) {
        assert (sortedCombination.length == strength);
        int[] columns = new int[strength];
        for (int i = strength; i > 0; i--) {
            columns[i] = options.getOption(sortedCombination[i]);
        }
        int offsetIndex = Combinadic.encode(columns);
        assert (offsetIndex < offsets.length);
        int offset = offsets[offsetIndex];
        int index = 0;
        for (int i = strength; i > 0; i--) {
            int column = columns[i];
            int base = options.getFirstSymbol(column);
            int count = options.getSymbolCount(column);
            index *= count;
            index += sortedCombination[i] - base;
        }
        return offset + index;
    }

    public int[] decode(int encoding) {
        int offsetIndex = offsets.length;
        while (offsets[--offsetIndex] > encoding) ;
        int index = encoding - offsets[offsetIndex];
        int[] columns = Combinadic.decode(offsetIndex, strength);
        int[] result = new int[strength];
        for (int i = 0; i < strength; ++i) {
            int column = columns[i];
            int base = options.getFirstSymbol(column);
            int count = options.getSymbolCount(column);
            int digit = index % count;
            index -= digit;
            index /= count;
            result[i] = base + digit;
        }
        /* TODO: tohle muze zbytecne spomalovat */
        assert (encode(result) == encoding);
        return result;
    }

    public Integer getStrength() {
        return strength;
    }

    public Options getOptions() {
        return options;
    }

    //    const Entry operator[](Array<unsigned>sortedCombination) const {
//        return Entry(*this, encode(sortedCombination));
//    }
    public Entry op_getEntry(int[] sortedCombination) {
        return new Entry(this, encode(sortedCombination));
    }

    public Entry hintGet(Integer indexHint,
                         int[] columnsHint,
                         int[] firstsHint,
                         int[] countsHint,
                         int[] sortedCombination) {
        return new Entry(this, encode(indexHint, columnsHint, firstsHint, countsHint, sortedCombination));
    }

    public Entry hintGet(int[] columnsHint,
                         int[] firstsHint,
                         int[] countsHint,
                         int[] sortedCombination) {
        return new Entry(this, encode(columnsHint, firstsHint, countsHint, sortedCombination));
    }

    //TODO iterators begin() & end() or implement then better way

    public Integer getSize() {
        return contents.getSize();
    }

    public void fill(T filler) {
        contents.fill(filler);
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public int[] getOffsets() {
        return offsets;
    }

    public void setOffsets(int[] offsets) {
        this.offsets = offsets;
    }

    public SubstitutionArray<T> getContents() {
        return contents;
    }

    public void setContents(SubstitutionArray<T> contents) {
        this.contents = contents;
    }

    public void setValueInContents(T value, Integer index){
        this.contents.set(index, value);
    }
}
