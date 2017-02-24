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


#ifndef SUBSTITUTION_ARRAY_H
#define SUBSTITUTION_ARRAY_H

#include <iostream>
#include <map>

#include "Array.H"
#include "Lazy.H"

#ifndef MAXIMUM_SUBSTITUTION_PROPORTION
#define MAXIMUM_SUBSTITUTION_PROPORTION 0.1F
#endif

template<class T>class SubstitutionArray : protected Array<T> {
protected:
  Lazy<std::map<unsigned, T> >		substitutions;
  unsigned				maximumSubstitutions;

public:
  SubstitutionArray() : Array<T>() {}
  SubstitutionArray(unsigned size) :
    Array<T>(size),
    maximumSubstitutions(MAXIMUM_SUBSTITUTION_PROPORTION * size) {}
  SubstitutionArray(const T*raw, unsigned size) :
    Array<T>(raw, size),
    maximumSubstitutions(MAXIMUM_SUBSTITUTION_PROPORTION * size) {}
  SubstitutionArray(const Array<T>&copy) :
    Array<T>(copy),
    maximumSubstitutions(MAXIMUM_SUBSTITUTION_PROPORTION * Array<T>::size) {}
  SubstitutionArray(const SubstitutionArray&copy) :
    Array<T>((const Array<T>&)copy),
    substitutions(copy.substitutions),
    maximumSubstitutions(copy.maximumSubstitutions) {}

  SubstitutionArray&operator =(const Array<T>&copy) {
    Array<T>::operator =(copy);
    substitutions = NULL;
    maximumSubstitutions = MAXIMUM_SUBSTITUTION_PROPORTION * Array<T>::size;
    return *this;
  }
  SubstitutionArray&operator =(const SubstitutionArray<T>&copy) {
    Array<T>::operator =((const Array<T>&)copy);
    substitutions = copy.substitutions;
    maximumSubstitutions = MAXIMUM_SUBSTITUTION_PROPORTION * Array<T>::size;
    return *this;
  }

  unsigned getSize() const {
    return Array<T>::getSize();
  }

  class Entry {
  protected:
    SubstitutionArray&			owner;
    unsigned				index;
  public:
    Entry(const SubstitutionArray&owner, unsigned index) :
      owner(const_cast<SubstitutionArray&>(owner)),
      index(index) {}

    operator T() const {
      if (owner.substitutions) {
	std::map<unsigned, unsigned>::const_iterator substitution =
	  owner.substitutions->find(index);
	if (substitution != owner.substitutions->end()) {
	  return substitution->second;
	}
      }
      return owner.array[index];
    }

    Entry&operator =(const T&value) {
      if (*owner.referenceCount > 1) {
	if (!owner.substitutions) {
	  owner.substitutions = new std::map<unsigned, T>();
	}
	(*owner.substitutions)[index] = value;
	owner.autoFinalizeSubstitutions();
      } else {
	owner.array[index] = value;
      }
      return *this;
    }

    Entry&operator --() {
      T old = operator T();
      return operator =(--old);
    }
    Entry&operator ++() {
      T old = operator T();
      return operator =(++old);
    }
  };

  const Entry operator[](unsigned index) const {
    return Entry(*this, index);
  }
  Entry operator[](unsigned index) {
    return Entry(*this, index);
  }

  void fill(const T&filler) {
    if (*Array<T>::referenceCount > 1) {
      Array<T>::destroy();
      Array<T>::array = new T[Array<T>::size];
      Array<T>::referenceCount = new unsigned(1);
      substitutions = NULL;
    }
    Array<T>::fill(filler);
  }

  void finalizeSubstitutions() {
    if (!substitutions) {
      return;
    }
    T*replacement = new T[Array<T>::size];
    for (unsigned i = Array<T>::size; i--;) {
      replacement[i] = Array<T>::array[i];
    }
    for (typename std::map<unsigned, T>::const_iterator iterator =
	   substitutions->begin(),
	   end = substitutions->end();
	 iterator != end; ++iterator) {
      replacement[iterator->first] = iterator->second;
    }
    Array<T>::destroy();
    Array<T>::array = replacement;
    Array<T>::referenceCount = new unsigned(1);
    substitutions->clear();
  }

  void autoFinalizeSubstitutions(){
    if (substitutions && substitutions->size() > maximumSubstitutions) {
      finalizeSubstitutions();
    }
  }
};

template<class T>std::ostream&operator <<
  (std::ostream&out, const SubstitutionArray<T>&array) {
  out << '[';
  for (unsigned i = 0; i < array.getSize(); ++i) {
    out << array[i] << ',';
  }
  return out << "X]";
}

template<class T, class COMPARE = std::less<T> >
  class SubstitutionArrayComparator {
public:
  bool operator()(const Array<T>&left,const Array<T>&right) {
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
