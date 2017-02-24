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


#include "io/OutputFile.H"

using namespace std;

OutputFile::OutputFile(const string&filename) :
  filename(filename) {}

void OutputFile::setCoveringArray(const CoveringArray&array) {
  unsigned rows = array.getRows();
  unsigned options = array.getOptions();
  this->array = Array<Array<unsigned> >(rows);
  for (unsigned i = rows; i--;) {
    Array<unsigned>&row = this->array[i] = Array<unsigned>(options);
    for (unsigned j = options; j--;) {
      row[j] = array(i, j);
    }
  }
}

void OutputFile::write() const {
  ofstream fileOutputStream(filename.data());
  fileOutputStream << array.getSize() << '\n';
  for (unsigned i = 0;i < array.getSize(); ++i) {
    const Array<unsigned>&row = array[i];
    fileOutputStream << row[0];
    for (unsigned j = 1; j < row.getSize(); ++j) {
      fileOutputStream << ' ' << row[j];
    }
    fileOutputStream << '\n';
  }
  fileOutputStream.close();
}
