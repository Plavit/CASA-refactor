package algorithms;

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

public class BinarySearch {

    protected ExpensiveUnaryPredicate predicate;
    protected Partitioner partitioner;

    public BinarySearch(ExpensiveUnaryPredicate predicate, Partitioner partitioner) {
        this.predicate = predicate;
        this.partitioner = partitioner;
    }

    // Returns the smallest index for which the predicate returns true, using the
    // partitioner to reduce the comparison cost.
    public int op_call(int offset, int size) {
        int result = offset + size;
        while (size > 0) {
            int division = partitioner.op_call(offset, size);
            assert ((division - offset) < size);
            if (predicate.op_call(division)) {
                size = division - offset;
                result = division;
            } else {
                ++division;
                size += offset - division;
                offset = division;
            }
        }
        return result;
    }
}
