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

import java.util.Vector;

public class Coverage<T> {

    protected Integer strength;
    protected Options options;
    protected Vector<Integer> offsets;
    protected SubstitutionArray<T> contents;

    public Coverage(Integer strength, Options options) {
        this.strength = strength;
        this.options = options;
        this.offsets = PascalTriangle.nCr(options.getSize(), strength);

        //TODO
    }

    public Coverage(Coverage copy) {
        //TODO
    }

    protected Integer encode(Integer indexHint,
                             Vector<Integer> columnsHint,
                             Vector<Integer> firstsHint,
                             Vector<Integer> countsHint,
                             Vector<Integer> sortedCombination) {
        //TODO
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
