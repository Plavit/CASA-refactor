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


#ifndef COVERAGE_H
#define COVERAGE_H

#include "PascalTriangle.H"
#include "Combinadic.H"

#include "SubstitutionArray.H"

#include "covering/bookkeeping/Options.H"

template<class T>class Coverage {
protected:
  unsigned				strength;
  Options				options;
  // The offsets are indices into the contents array; entry offsets[i], where i
  // is the combinadic encoding of a set of columns, is the beginning of the
  // contents for that column set's t-sets.
  Array<unsigned>			offsets;
  SubstitutionArray<T>			contents;

public:
  Coverage(unsigned strength, Options options) :
    strength(strength),
    options(options),
    offsets(triangle.nCr(options.getSize(), strength)) {
    unsigned size = 0;
    unsigned offsetIndex = 0;
    for (Array<unsigned>columns = combinadic.begin(strength);
	 columns[strength - 1] < options.getSize();
	 combinadic.next(columns)) {
      unsigned blockSize = 1;
      for (unsigned i = strength; i--;) {
	blockSize *= options.getSymbolCount(columns[i]);
      }
      offsets[offsetIndex++] = size;
      size += blockSize;
    }
    contents = Array<T>(size);
  }

  Coverage(const Coverage&copy) :
    strength(copy.strength),
    options(copy.options),
    offsets(copy.offsets),
    contents(copy.contents) {}

protected:
  // The method encode, in its various forms, is responsible for converting a
  // sorted t-set to an index into the contents array.

  // These hinted versions of encode are for when some information has already
  // been computed.  indexHint is the combinadic encoding of the set of columns;
  // columnsHint is this set of columns in sorted order; firstsHint is an array
  // of corresponding first symbols; and countsHint is an array of symbol counts
  // for each column.
  unsigned encode
    (unsigned indexHint,
     Array<unsigned>columnsHint,
     Array<unsigned>firstsHint,
     Array<unsigned>countsHint,
     Array<unsigned>sortedCombination) const {
    assert(sortedCombination.getSize() == strength);
    assert(indexHint < offsets.getSize());
    unsigned offset = offsets[indexHint];
    unsigned index = 0;
    for (unsigned i = strength; i--;) {
      unsigned column = columnsHint[i];
      unsigned base = firstsHint[column];
      unsigned count = countsHint[column];
      index *= count;
      index += sortedCombination[i] - base;
    }
    return offset + index;
  }

  unsigned encode
    (Array<unsigned>columnsHint,
     Array<unsigned>firstsHint,
     Array<unsigned>countsHint,
     Array<unsigned>sortedCombination) const {
    return encode
      (combinadic.encode(columnsHint),
       columnsHint,
       firstsHint,
       countsHint,
       sortedCombination);
  }

  unsigned encode(Array<unsigned>sortedCombination) const {
    assert(sortedCombination.getSize() == strength);
    Array<unsigned>columns(strength);
    for (unsigned i = strength; i--;) {
      columns[i] = options.getOption(sortedCombination[i]);
    }
    unsigned offsetIndex = combinadic.encode(columns);
    assert(offsetIndex < offsets.getSize());
    unsigned offset = offsets[offsetIndex];
    unsigned index = 0;
    for (unsigned i = strength; i--;) {
      unsigned column = columns[i];
      unsigned base = options.getFirstSymbol(column);
      unsigned count = options.getSymbolCount(column);
      index *= count;
      index += sortedCombination[i] - base;
    }
    return offset + index;
  }

  // The method decode, of course, does the opposite of encode.
  Array<unsigned>decode(unsigned encoding) const {
    unsigned offsetIndex = offsets.getSize();
    while (offsets[--offsetIndex] > encoding);
    unsigned index = encoding - offsets[offsetIndex];
    Array<unsigned>columns = combinadic.decode(offsetIndex, strength);
    Array<unsigned>result(strength);
    for (unsigned i = 0; i < strength; ++i) {
      unsigned column = columns[i];
      unsigned base = options.getFirstSymbol(column);
      unsigned count = options.getSymbolCount(column);
      unsigned digit = index % count;
      index -= digit;
      index /= count;
      result[i] = base + digit;
    }
    assert(encode(result) == encoding);
    return result;
  }

public:
  unsigned getStrength() const {
    return strength;
  }
  const Options&getOptions() const {
    return options;
  }

  class Entry{
  protected:
    Coverage&				owner;
    unsigned				index;
  public:
    Entry(const Coverage&owner,unsigned index) :
      owner(const_cast<Coverage&>(owner)),
      index(index) {}
    operator T() const {
      return owner.contents[index];
    }
    Entry&operator =(const T&value) {
      owner.contents[index] = value;
      return *this;
    }
    Entry&operator --() {
      --owner.contents[index];
      return *this;
    }
    Entry&operator ++() {
      ++owner.contents[index];
      return *this;
    }
  };

  const Entry operator[](Array<unsigned>sortedCombination) const {
    return Entry(*this, encode(sortedCombination));
  }
  Entry operator [](Array<unsigned>sortedCombination) {
    return Entry(*this, encode(sortedCombination));
  }

  // The methods named hintGet work like the [] operator with the aforementioned
  // hints.
  const Entry hintGet
    (unsigned indexHint,
     Array<unsigned>columnsHint,
     Array<unsigned>firstsHint,
     Array<unsigned>countsHint,
     Array<unsigned>sortedCombination) const {
    return Entry
      (*this,
       encode
         (indexHint, columnsHint, firstsHint, countsHint, sortedCombination));
  }

  Entry hintGet
    (unsigned indexHint,
     Array<unsigned>columnsHint,
     Array<unsigned>firstsHint,
     Array<unsigned>countsHint,
     Array<unsigned>sortedCombination) {
    return Entry
      (*this,
       encode
         (indexHint, columnsHint, firstsHint, countsHint, sortedCombination));
  }

  const Entry hintGet
    (Array<unsigned>columnsHint,
     Array<unsigned>firstsHint,
     Array<unsigned>countsHint,
     Array<unsigned>sortedCombination) const {
    return Entry
      (*this,
       encode(columnsHint, firstsHint, countsHint, sortedCombination));
  }

  Entry hintGet
    (Array<unsigned>columnsHint,
     Array<unsigned>firstsHint,
     Array<unsigned>countsHint,
     Array<unsigned>sortedCombination) {
    return Entry
      (*this,
       encode(columnsHint, firstsHint, countsHint, sortedCombination));
  }

  class iterator {
  protected:
    Coverage&				owner;
    unsigned				index;
  public:
    iterator(Coverage&owner, unsigned index) :
      owner(owner),
      index(index) {}
    const Entry operator *() const {
      return Entry(owner, index);
    }
    Entry operator *() {
      return Entry(owner, index);
    }
    iterator&operator ++() {
      ++index;
      return *this;
    }
    bool operator ==(const iterator&other) const {
      return &owner == &other.owner && index == other.index;
    }
    bool operator !=(const iterator&other) const {
      return &owner != &other.owner || index != other.index;
    }
    Array<unsigned>getCombination() const {
      return owner.decode(index);
    }
  };

  class const_iterator {
  protected:
    const Coverage&			owner;
    unsigned				index;
  public:
    const_iterator(const Coverage&owner, unsigned index) :
      owner(owner),
      index(index) {}
    const_iterator(const iterator&copy) :
      owner(copy.owner),
      index(copy.index) {}
    const Entry operator *() const {
      return Entry(owner, index);
    }
    const_iterator&operator ++() {
      ++index;
      return *this;
    }
    bool operator ==(const const_iterator&other) const {
      return &owner == &other.owner && index == other.index;
    }
    bool operator  != (const const_iterator&other) const {
      return &owner != &other.owner || index != other.index;
    }
    Array<unsigned>getCombination() const {
      return owner.decode(index);
    }
  };

  iterator begin() {
    return iterator(*this, 0);
  }
  const_iterator begin() const {
    return const_iterator(*this, 0);
  }
  iterator end() {
    return iterator(*this, contents.getSize());
  }
  const_iterator end() const {
    return const_iterator(*this, contents.getSize());
  }

  unsigned getSize() const {
    return contents.getSize();
  }

  void fill(const T&filler) {
    contents.fill(filler);
  }
};

#endif
