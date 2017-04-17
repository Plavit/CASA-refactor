package common.utility;

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

import java.util.Vector;

public class Array<T> {

    protected int size;
    protected Vector<T> array;
    protected int referenceCount;

    private void destroy() {
        if (referenceCount > 0) {
            this.size = 0;
            this.array = null;
            this.referenceCount = 0;
        }
    }

    public Array() {
        this.size = 0;
        this.array = null;
        this.referenceCount = 0;
    }

    public Array(int size) {
        this.size = size;
        this.array = new Vector<>(size);
        this.referenceCount = 1;
    }

    public Array(Vector<T> raw, int size) {
        this.size = size;
        this.array = new Vector<>(size);
        this.referenceCount = 1;
        this.array.addAll(raw);
    }

    public Array(Array<T> copy) {
        this.size = copy.getSize();
        this.array = copy.getArray();
        this.referenceCount = copy.getReferenceCount();
        if (referenceCount > 0) {
            ++referenceCount;
        }
    }

    public Array op_arrayCopy(Array<T> copy) {
        destroy();
        this.size = copy.getSize();
        this.array = copy.getArray();
        this.referenceCount = copy.getReferenceCount();
        ++referenceCount;
        return this;
    }

    public void fill(T filler) {
        for (int i = size; i > 0; i--) {
            this.array.set(i, filler);
        }
    }

    public int getSize() {
        return size;
    }

    public Vector<T> getArray() {
        return array;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    //TODO class ArrayComparator
}
