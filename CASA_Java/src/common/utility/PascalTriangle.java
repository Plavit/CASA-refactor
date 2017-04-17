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

import java.util.ArrayList;
import java.util.Vector;

public class PascalTriangle {

    protected static ArrayList<int[]> table;

    public PascalTriangle() {
        int[] root = new int[1];
        root[0] = 1;
        table.add(root);
    }

    public static void addRows(int targetDepth) {
        while (table.size() <= targetDepth) {
            int depth = table.size();
            int[] line = new int[depth + 1];
            int[] source = table.get(depth - 1);
            table.add(line);
            line[0] = 1;
            for (int column = 1, trail = source[0]; column < depth; ++column) {
                line[column] = trail;
                line[column] += trail = source[column];
            }
            line[depth] = 1;
        }
    }

    public static Integer nCr(int n, int r) {
        if (n >= table.size()) {
            addRows(n);
        }
        if (r > n) {
            return 0;
        }
        //C_CODE
        //return table[n][r];
        return table.get(n)[r];
    }
}
