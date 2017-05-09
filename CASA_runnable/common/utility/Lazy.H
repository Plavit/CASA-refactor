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


#ifndef LAZY_H
#define LAZY_H

#include <cassert>
#include <iostream>

template<class T>class Lazy {
protected:
  T*					implementation;
  unsigned*				referenceCount;

  void destroy() {
    if (referenceCount) {
      assert(implementation);
      if (!(--*referenceCount)) {
	delete implementation;
	delete referenceCount;
      }
      implementation = NULL;
    } else {
      assert(!implementation);
    }
  }

public:
  Lazy() :
    implementation(NULL),
    referenceCount(NULL) {}
  Lazy(T*implementation) :
    implementation(implementation),
    referenceCount(implementation ? new unsigned(1) : NULL) {}
  Lazy(const Lazy&copy) :
    implementation(copy.implementation),
    referenceCount(copy.referenceCount) {
    if (implementation) {
      assert(referenceCount);
      ++*referenceCount;
    } else {
      assert(!referenceCount);
    }
  }

  Lazy&operator =(T*implementation) {
    destroy();
    this->implementation = implementation;
    this->referenceCount = implementation ? new unsigned(1) : NULL;
    return *this;
  }
  Lazy&operator =(const Lazy&copy) {
    destroy();
    implementation = copy.implementation;
    if (implementation) {
      referenceCount = copy.referenceCount;
      assert(referenceCount);
      ++*referenceCount;
    } else {
      assert(!copy.referenceCount);
      referenceCount = NULL;
    }
    return *this;
  }

  virtual ~Lazy(){
    destroy();
  }

  const T*operator ->() const {
    return implementation;
  }
  T*operator ->() {
    if (referenceCount) {
      assert(implementation);
      if (*referenceCount > 1) {
	T*copy = new T(*implementation);
	destroy();
	implementation = copy;
	referenceCount = new unsigned(1);
      }
    } else {
      assert(!implementation);
    }
    return implementation;
  }
  operator const T*() const {
    return operator ->();
  }
  operator T*() {
    return operator ->();
  }
};

#endif
