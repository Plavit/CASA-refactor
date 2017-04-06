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

import java.util.Vector;

public class Options {

    protected Vector<Integer> cumulativeValueCounts;
    protected Vector<Integer> owningOptions;

    public Options() {
        //TODO
    }

    public Options(Vector<Integer> valueCounts) {
        //TODO
    }

    public Options(Options copy) {
        //TODO
    }

    public Integer getSize() {
        //TODO
    }

    public Integer getFirstSymbol(Integer option) {
        //TODO
    }

    public Vector<Integer> getFirstSymbols() {
        //TODO
    }

    public Integer getSymbolCount(Integer option) {
        //TODO
    }

    public Vector<Integer> getSymbolCounts() {
        //TODO
    }

    public Integer getLastSymbol(Integer option) {
        //TODO
    }

    public Integer getRandomSymbol(Integer option) {
        //TODO
    }

    public Integer getOtherRandomSymbol(Integer option, Integer exclude) {
        //TODO
    }

    public Integer getOption(Integer symbol) {
        //TODO
    }
}
