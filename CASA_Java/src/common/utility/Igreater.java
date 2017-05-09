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

/**
 * Compares two indices by the elements in an array at those locations.  Returns
 * true if the left indexth element is greater than the right indexth element.
 */
public class Igreater<T> {

    private Array array;

    public Igreater(Array array) {
        this.array = array;
    }

    public boolean op_compare(int left, int right) {
        return array.get(right).compareTo(array.get(left)) < 0;
    }
}