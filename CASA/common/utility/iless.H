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


#ifndef ILESS_H
#define ILESS_H

// Compares two indices by the elements in an array at those locations.  Returns
// true if the left indexth element is less than the right indexth element.

template<class T>class iless {
protected:
  const T*				array;
public:
  iless(const T*array) :
    array(array) {}
  bool operator ()(unsigned left, unsigned right) const {
    return array[left] < array[right];
  }
};

#endif
