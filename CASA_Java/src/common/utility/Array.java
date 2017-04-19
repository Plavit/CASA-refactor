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

    //C_CODE
//    void destroy() {
//        if (referenceCount && !(--*referenceCount)) {
//            delete[] array;
//            delete referenceCount;
//        }
//        array = NULL;
//    }
    //TODO ???
    protected void destroy() {
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

    //C_CODE
//    Array&operator =(const Array&copy) {
//        destroy();
//        size = copy.size;
//        array = copy.array;
//        referenceCount = copy.referenceCount;
//        ++*referenceCount;
//        return *this;
//    }

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

    //C_CODE
//    template<class T,class COMPARE = std::less<T> >class ArrayComparator {
//        public:
//        bool operator()(const Array<T>&left, const Array<T>&right) const {
//            static COMPARE compare;
//            unsigned leftSize = left.getSize();
//            unsigned rightSize = right.getSize();
//            if (leftSize < rightSize) {
//                return true;
//            }
//            if (leftSize > rightSize) {
//                return false;
//            }
//            for (unsigned i = 0; i < leftSize; ++i) {
//                if (compare(left[i], right[i])) {
//                    return true;
//                }
//                if (compare(right[i], left[i])) {
//                    return false;
//                }
//            }
//            return false;
//        }
//    };
    class ArrayComparator<T extends Comparable<T>> {

        private Pless<T> comparator = new Pless<>();

        public boolean op_compare(Array<T> left, Array<T> right) {
            int leftSize = left.getSize();
            int rightSize = right.getSize();
            if (leftSize < rightSize) {
                return true;
            }
            if (leftSize > rightSize) {
                return false;
            }
            for (int i = 0; i < leftSize; ++i) {
                if (comparator.compare(left.getArray().get(i), right.getArray().get(i))) {
                    return true;
                }
                if (comparator.compare(right.getArray().get(i), left.getArray().get(i))) {
                    return false;
                }
            }
            return false;
        }
    }
}
