package common.utility;

// Copyright 2008, 2009 Brady J. Garvin

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


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

public class SubstitutionArray<T> extends Array<T> {

    public static final double MAXIMUM_SUBSTITUTION_PROPORTION = 0.1;

    private Lazy<Map<Integer, T>> substitutions;
    private int maximumSubstitutions;
    
    public SubstitutionArray() {
        super();
    }
    
    public SubstitutionArray(int size) {
        super(size);
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * size);
    }

    public SubstitutionArray(Vector<T> raw, int size) {
        super(raw, size);
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * size);
    }

    public SubstitutionArray(Array<T> copy) {
        super(copy);
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * this.getSize());
    }

    public SubstitutionArray(SubstitutionArray<T> copy) {
        super(copy);
        substitutions = copy.substitutions;
        maximumSubstitutions = copy.maximumSubstitutions;
    }

    //C_CODE
//    SubstitutionArray&operator =(const Array<T>&copy) {
//        Array<T>::operator =(copy);
//        substitutions = NULL;
//        maximumSubstitutions = MAXIMUM_SUBSTITUTION_PROPORTION * Array<T>::size;
//        return *this;
//    }
    public SubstitutionArray<T> op_copy(Array<T> copy) {
        op_arrayCopy(copy);
        this.substitutions = null;
        this.maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * this.getSize());
        return this;
    }

    //C_CODE
//    SubstitutionArray&operator =(const SubstitutionArray<T>&copy) {
//        Array<T>::operator =((const Array<T>&)copy);
//        substitutions = copy.substitutions;
//        maximumSubstitutions = MAXIMUM_SUBSTITUTION_PROPORTION * Array<T>::size;
//        return *this;
//    }
    public SubstitutionArray<T> op_copy(SubstitutionArray<T> copy) {
        op_arrayCopy(copy);
        this.substitutions = copy.substitutions;
        this.maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * this.getSize());
        return this;
    }

    public int getSize() {
        return super.getSize();
    }

    class Entry {

        protected SubstitutionArray<T> owner;
        protected int index;

        public Entry(SubstitutionArray<T> owner, int index) {
            this.owner = owner;
            this.index = index;
        }

        //C_CODE
//        operator T() const {
//            if (owner.substitutions) {
//                std::map<unsigned, unsigned>::const_iterator substitution =
//                        owner.substitutions->find(index);
//                if (substitution != owner.substitutions->end()) {
//                    return substitution->second;
//                }
//            }
//            return owner.array[index];
//        }
        public T op_value() {
            if (owner.substitutions != null) {
                //TODO iterator
            }

            throw new UnsupportedOperationException();
        }

        //C_CODE
//        Entry&operator =(const T&value) {
//            if (*owner.referenceCount > 1) {
//                if (!owner.substitutions) {
//                    owner.substitutions = new std::map<unsigned, T>();
//                }
//                (*owner.substitutions)[index] = value;
//                owner.autoFinalizeSubstitutions();
//            } else {
//                owner.array[index] = value;
//            }
//            return *this;
//        }
        //TODO
        public Entry op_assignment(T value) {
            if (owner.referenceCount > 1) {
                if (owner.substitutions != null) {
                    owner.substitutions = new Lazy<Map<Integer, T>>();
                }
                //TODO... i have no idea what i am doing
            }

            throw new UnsupportedOperationException();
        }

        //C_CODE
//        Entry&operator --() {
//            T old = operator T();
//            return operator =(--old);
//        }
        public Entry op_pre_dec() {
            //TODO
            T old = op_value();
            return op_assignment(--old);
        }

        //C_CODE
//        Entry&operator ++() {
//            T old = operator T();
//            return operator =(++old);
//        }
        public Entry op_pre_inc() {
            //TODO
            T old = op_value();
            return op_assignment(++old);
        }
    }

    public Entry getEntry(int index) {
        return new Entry(this, index);
    }

    public void fill(T filler) {
        if (referenceCount > 1) {
            destroy();
            array = new Vector<>(getSize());
            referenceCount = 1;
            substitutions = null;
        }
        super.fill(filler);
    }

    public void finalizeSubstitutions() {
        if (substitutions == null) {
            return;
        }
        Vector<T> replacement = new Vector<>(getSize());
        for (int i = getSize(); i > 0; i--) {
            replacement.set(i, array.get(i));
        }

        //TODO iterators

        destroy();
        array = replacement;
        referenceCount = 1;
        //TODO substitutions->clear();
    }

    public void autoFinalizeSubstitutions() {
        //TODO
    }

    class SubstitutionArrayComparator<T extends Comparable<T>> {

        private ArrayComparator<T> arrayComparator = new ArrayComparator<>();

        public boolean op_compare(Array<T> left, Array<T> right) {
            return arrayComparator.op_compare(left, right);
        }
    }
}