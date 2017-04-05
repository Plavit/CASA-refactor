package common.utility;

// Copyright 2008, 2009 Brady J. Garvin

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


//TODO SubstitutionArray extends Array (we use Vector instead of Array)...what to do?
public class SubstitutionArray<T> extends Vector<T> {

    public static final double MAXIMUM_SUBSTITUTION_PROPORTION = 0.1;

    protected Lazy<Map<Integer, T>> substitutions;
    protected Integer maximumSubstitutions;
    
    public SubstitutionArray() {
        super();
    }
    
    public SubstitutionArray(Integer size) {
        super(size);
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * size);
    }

    public SubstitutionArray(Vector<T> raw, Integer size) {
        super(size);
        for (int i = size; i-- > 0;) {
            this.set(i, raw.get(i));
        }
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * size);
    }

    public SubstitutionArray(Vector<T> copy) {
        this.addAll(copy);
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * this.size());
    }

    public SubstitutionArray(SubstitutionArray<T> copy) {
        this.addAll(copy);
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

    public SubstitutionArray<T> op_copy(Vector<T> copy) {
        this.removeAllElements();
        this.addAll(copy);
        substitutions = null; //TODO ???
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * this.size());
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
        this.removeAllElements();
        this.addAll(copy);
        substitutions = copy.substitutions;
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * this.size());
        return this;
    }

    public Integer getSize() {
        return this.size();
    }

    class Entry {
        //TODO
    }
}