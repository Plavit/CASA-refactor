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

//TODO not sure about "static"
public class PascalTriangle {

    protected static ArrayList<Vector<Integer>> table;

    public PascalTriangle() {
        Vector<Integer> root = new Vector<>(1);
        root.set(0, 1);
        table.add(root);
    }

    public static void addRows(Integer targetDepth) {
        while (table.size() <= targetDepth) {
            Integer depth = table.size();
            Vector<Integer> line = new Vector<>(depth + 1);
            Vector<Integer> source = table.get(depth - 1);
            table.add(line);
            line.add(0, 1);
            for (int column = 1, trail = source.get(0); column < depth; ++column) {
                line.set(column, trail);
                //C_CODE
                //line[column] += trail = source[column];
                trail = source.get(column);
                line.set(column, line.get(column) + trail);
            }
        }
    }

    public static Integer nCr(Integer n, Integer r) {
        if (n >= table.size()) {
            addRows(n);
        }
        if (r > n) {
            return 0;
        }
        //C_CODE
        //return table[n][r];
        return table.get(n).get(r);
    }
}
