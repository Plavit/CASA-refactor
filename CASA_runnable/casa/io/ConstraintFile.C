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


#include "ConstraintFile.H"

using namespace std;

ConstraintFile::ConstraintFile(const string&filename) {
    string file = "C:\\Users\\Yevgeniya\\Documents\\RSP\\CASA\\example.constraints";
  if (!filename.size()) {
    return;
  }
  ifstream fileInputStream(file.data());
  unsigned clauseCount;
  fileInputStream >> clauseCount;
  clauses = Array<InputClause>(clauseCount);
  for (unsigned i = 0; i < clauseCount; ++i) {
    InputClause&clause = clauses[i];
    unsigned termCount;
    fileInputStream >> termCount;
    while (termCount--) {
      char sign;
      unsigned symbol;
      do {
	fileInputStream >> sign;
      } while (sign != '-' && sign != '+');
      fileInputStream >> symbol;
      clause.append(InputTerm(sign == '-', symbol));
    }
  }
  fileInputStream.close();
}

const Array<InputClause>&ConstraintFile::getClauses() const {
  return clauses;
}
