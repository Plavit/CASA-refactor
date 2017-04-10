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

import jdk.internal.org.objectweb.asm.tree.IincInsnNode;

import java.util.Random;
import java.util.Vector;

public class Options {

    protected Vector<Integer> cumulativeValueCounts;
    protected Vector<Integer> owningOptions;

    public Options() {
        cumulativeValueCounts = new Vector<>();
        owningOptions = new Vector<>();
    }

    public Options(Vector<Integer> values) {
        cumulativeValueCounts = new Vector<>(values.size());
        int cumulativeValueCount = 0;
        for (int i = 0; i < values.size(); ++i) {
            cumulativeValueCount += values.get(i);
            cumulativeValueCounts.set(i, cumulativeValueCount);
        }
        owningOptions = new Vector<>(cumulativeValueCount);
        for (int i = 0, j = 0; i < values.size(); ++i) {
            for (int k = values.get(i); k > 0; k--) {
                owningOptions.set(j++, i);
            }
        }
    }

    public Options(Options copy) {
        cumulativeValueCounts = new Vector<>(copy.getCumulativeValueCounts());
        owningOptions = new Vector<>(copy.getOwningOptions());
    }

    public Integer getSize() {
        return cumulativeValueCounts.size();
    }

    public Integer getFirstSymbol(Integer option) {
        return option > 0 ? cumulativeValueCounts.get(option - 1) : 0;
    }

    public Vector<Integer> getFirstSymbols() {
        int size = cumulativeValueCounts.size();
        Vector<Integer> result = new Vector<>(size);
        for (int i = size; i > 1; i--) {
            result.set(i, cumulativeValueCounts.get(i - 1));
        }
        if (size > 0) {
            result.set(0, 0);
        }
        return result;
    }

    public Integer getSymbolCount(Integer option) {
        return option != 0 ?
                (cumulativeValueCounts.get(option) - cumulativeValueCounts.get(option - 1)) :
                cumulativeValueCounts.get(0);
    }

    public Vector<Integer> getSymbolCounts() {
        int size = cumulativeValueCounts.size();
        Vector<Integer> result = new Vector<>(size);
        for (int i = size; i > 1; i--) {
            result.set(i, cumulativeValueCounts.get(i) - cumulativeValueCounts.get(i - 1));
        }
        if (size > 0) {
            result.set(0, cumulativeValueCounts.get(0));
        }
        return result;
    }

    public Integer getLastSymbol(Integer option) {
        return cumulativeValueCounts.get(option) - 1;
    }

    public Integer getRandomSymbol(Integer option) {
        Random rand = new Random();
        int randomNum = rand.nextInt(Integer.MAX_VALUE);
        return randomNum % getSymbolCount(option) + getFirstSymbol(option);
    }

    public Integer getOtherRandomSymbol(Integer option, Integer exclude) {
        int count = getSymbolCount(option);
        if (count == 1) {
            return getFirstSymbol(option);
        }
        Random rand = new Random();
        int randomNum = rand.nextInt(Integer.MAX_VALUE);
        int result = randomNum % (count - 1) + getFirstSymbol(option);
        return (result >= exclude) ? (result + 1) : result;
    }

    public Integer getOption(Integer symbol) {
        return owningOptions.get(symbol);
    }

    public Vector<Integer> getCumulativeValueCounts() {
        return cumulativeValueCounts;
    }

    public Vector<Integer> getOwningOptions() {
        return owningOptions;
    }
}
