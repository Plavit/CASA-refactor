package annealing;

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

import common.utility.Array;
import covering.bookkeeping.Options;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

public class Bounds implements IBounds{

    final private Integer UPPER_BOUND_SCALING_FACTOR = 5;

    public static Comparator<Integer> BackwardsComparator
            = (integer1, integer2) -> {
        Integer a = integer1.intValue();
        Integer b = integer2.intValue();
        return b.compareTo(a);
    };

    @Override
    public Integer computeLowerBound(Integer strength, Options options) {
        // TODO C++ Code lowerBound from #include <algorithm> returnig forwardIterator.... USELESS ???
//        if (lowerBound) {
//            return lowerBound;
//        }

        Integer result = 1;
        Array<Integer> symbolCounts = new Array<>(options.getSize());
        for (int i = symbolCounts.getSize()-1; i >= 0; i--) {
            symbolCounts.setValueOnIntex(options.getSymbolCount(i),i);
        }

        Arrays.sort(getBasicFromVector(symbolCounts.getArray()),strength,
                symbolCounts.getSize()-1,Bounds.BackwardsComparator);

        Vector<Integer> vector = symbolCounts.getArray();
        for (int i = strength; i >= 0; i--) {
            result *= vector.get(i);
        }

        return result;
    }

    @Override
    public Integer computeUpperBound(Integer strength, Options options) {
        Integer max = 0;
        Array<Integer> symbolCounts = new Array<>(options.getSize());

        for(int i = symbolCounts.getSize(); i >= 0; i--){
            Integer candidate = options.getSymbolCount(i);
            if(candidate > max) max = candidate;
        }

        double maxD = Double.parseDouble(max.toString());
        double strengthD = Double.parseDouble(strength.toString());

        return UPPER_BOUND_SCALING_FACTOR * (int)Math.pow(maxD,strengthD);
    }

    private Integer[] getBasicFromVector(Vector<Integer> arr){
        Integer basic[] = new Integer[arr.size()];
        for(int i = 0; i < basic.length; i++){
            basic[i] = arr.get(i);
        }
        return basic;
    }


}
