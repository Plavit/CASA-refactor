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

public class SubstitutionArray<T extends Comparable<T>> extends Array<T> {

    private static final double MAXIMUM_SUBSTITUTION_PROPORTION = 0.1;

    private Map<Integer, T> substitutions;
    private int maximumSubstitutions;
    
    public SubstitutionArray(int size) {
        super(size);
        maximumSubstitutions = (int)(MAXIMUM_SUBSTITUTION_PROPORTION * size);
    }

    @Override
    public T get(int index) {
        if (substitutions != null) {
            if (substitutions.containsKey(index)) {
                return substitutions.get(index);
            }
        }
        return sharedArray.get(index);
    }

    @Override
    public void set(int index, T value) {
        if (substitutions == null) {
            substitutions = new HashMap<>();
        }
        if (substitutions.size() < maximumSubstitutions) {
            substitutions.put(index, value);
            maximumSubstitutions++;
        } else {
            sharedArray = sharedArray.cloneIfMultiref();
            substitutions.forEach((index1, value1)-> sharedArray.set(index1, value1));
            substitutions.clear();
            maximumSubstitutions = 0;
        }
    }

    public void fill(T filler) {
        sharedArray = sharedArray.cloneIfMultiref();
        if (substitutions != null) {
            substitutions.clear();
        }
        super.fill(filler);
    }
}