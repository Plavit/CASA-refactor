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


#ifndef IGREATER_H
#define IGREATER_H

// Compares two indices by the elements in an array at those locations.  Returns
// true if the left indexth element is greater than the right indexth element.

template<class T>class igreater {
protected:
  const T*				array;
public:
  igreater(const T*array) :
    array(array) {}
  bool operator ()(unsigned left, unsigned right) const {
    return array[right] < array[left];
  }
};

#endif
