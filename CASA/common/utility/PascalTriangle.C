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


#include "PascalTriangle.H"

PascalTriangle::PascalTriangle() {
  Array<unsigned>root(1);
  root[0] = 1;
  table.push_back(root);
}

void PascalTriangle::addRows(unsigned targetDepth) {
  while (table.size() <= targetDepth) {
    unsigned depth = table.size();
    Array<unsigned>line(depth + 1);
    Array<unsigned>source = table[depth - 1];
    table.push_back(line);
    line[0] = 1;
    for (unsigned column = 1, trail = source[0]; column < depth; ++column) {
      line[column] = trail;
      line[column] += trail = source[column];
    }
    line[depth] = 1;
  }
}

unsigned PascalTriangle::nCr(unsigned n, unsigned r) {
  if (n >= table.size()) {
    addRows(n);
  }
  if (r > n) {
    return 0;
  }
  return table[n][r];
}

PascalTriangle triangle;
