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


#ifndef ARRAY_H
#define ARRAY_H

#include <iostream>

template<class T>class Array {
protected:
  unsigned				size;
  T*					array;
  unsigned*				referenceCount;

  void destroy() {
    if (referenceCount && !(--*referenceCount)) {
      delete[] array;
      delete referenceCount;
    }
    array = NULL;
  }

public:
  Array() :
    size(0),
    array(NULL),
    referenceCount(NULL) {}
  Array(unsigned size) :
    size(size),
    array(new T[size]),
    referenceCount(new unsigned(1)) {}
  Array(const T*raw, unsigned size) :
    size(size),
    array(new T[size]),
    referenceCount(new unsigned(1)) {
    for (unsigned i = size; i-- > 0;) {
      array[i] = raw[i];
    }
  }
  Array(const Array&copy) :
    size(copy.size),
    array(copy.array),
    referenceCount(copy.referenceCount) {
    if (referenceCount) {
      ++*referenceCount;
    }
  }

  Array&operator =(const Array&copy) {
    destroy();
    size = copy.size;
    array = copy.array;
    referenceCount = copy.referenceCount;
    ++*referenceCount;
    return *this;
  }

  virtual ~Array() {
    destroy();
  }

  unsigned getSize() const {
    return size;
  }

  operator const T*() const {
    return array;
  }
  operator T*() {
    return array;
  }

  void fill(const T&filler) {
    for (unsigned i = size; i--;) {
      array[i] = filler;
    }
  }
};

template<class T>std::ostream&operator <<
  (std::ostream&out, const Array<T>&array) {
  out << '[';
  for (unsigned i = 0; i < array.getSize(); ++i) {
    out << array[i] << ',';
  }
  return out << "X]";
}

template<class T,class COMPARE = std::less<T> >class ArrayComparator {
public:
  bool operator()(const Array<T>&left, const Array<T>&right) const {
    static COMPARE compare;
    unsigned leftSize = left.getSize();
    unsigned rightSize = right.getSize();
    if (leftSize < rightSize) {
      return true;
    }
    if (leftSize > rightSize) {
      return false;
    }
    for (unsigned i = 0; i < leftSize; ++i) {
      if (compare(left[i], right[i])) {
	return true;
      }
      if (compare(right[i], left[i])) {
	return false;
      }
    }
    return false;
  }
};

#endif
