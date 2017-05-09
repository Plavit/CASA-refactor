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


#include "SpecificationFile.H"
#include <iostream>

using namespace std;

SpecificationFile::SpecificationFile(const string&filename) {
   string file = "C:\\Users\\Yevgeniya\\Documents\\RSP\\CASA\\input";
  ifstream fileInputStream(file.data());
  unsigned optionCount;
  fileInputStream >> strength >> optionCount;
  Array<unsigned>values(optionCount);
  std::cout << "Values from input file\n";
  for(unsigned i = 0; i < optionCount; ++i) {
    fileInputStream >> values[i];
    std::cout << values[i] << ' ';
  }
  options = Options(values);
  std::cout << "\nEnd values from input file\n";
  fileInputStream.close();
}
unsigned SpecificationFile::getStrength() const {
  return strength;
}
const Options&SpecificationFile::getOptions() const {
  return options;
}
