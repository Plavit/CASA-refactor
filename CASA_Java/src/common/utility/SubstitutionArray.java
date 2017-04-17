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
        super(size);
        for (int i = size; i-- > 0;) {
            this.array.set(i, raw.get(i));
        }
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * size);
    }

    public SubstitutionArray(Array<T> copy) {
        this.array.addAll(copy.getArray());
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * this.getSize());
    }

    public SubstitutionArray(SubstitutionArray<T> copy) {
        this.array.addAll(copy.getArray());
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
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * this.getSize());
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
        substitutions = copy.substitutions;
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * this.getSize());
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
        }

        //C_CODE
//        Entry&operator --() {
//            T old = operator T();
//            return operator =(--old);
//        }
        public Entry op_pre_dec() {
            //TODO
        }

        //C_CODE
//        Entry&operator ++() {
//            T old = operator T();
//            return operator =(++old);
//        }
        public Entry op_pre_inc() {
            //TODO
        }
    }

    public Entry getEntry(int index) {
        return new Entry(this, index);
    }

    public void fill(T filler) {
        //TODO
    }

    public void finalizeSubstitutions() {
        //TODO
    }

    public void autoFinalizeSubstitutions() {
        //TODO
    }
}