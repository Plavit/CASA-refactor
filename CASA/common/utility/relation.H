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


#ifndef RELATION_H
#define RELATION_H

#include <cassert>
#include <iostream>
#include <set>
#include <map>

// A bidirectional (multi-)map where either the either the key or data type (or
// both) can be forced to be unique.

// Naming conventions (including abbreviations in identifier names) here
// resemble those used in the STL, rather than the more Java-like standard in
// the other code.

template<class key_type, class data_type, bool unique_key, bool unique_data,
	 class key_compare = std::less<key_type>,
	 class data_compare = std::less<data_type> >class relation {
protected:
  typedef relation
    <key_type, data_type, unique_key, unique_data, key_compare, data_compare>
      relation_type;

  std::multimap<key_type, data_type, key_compare>
					by_key;
  std::multimap<data_type, key_type, data_compare>
					by_data;
public:
  typedef unsigned			size_type;
  typedef int				difference_type;

  // By-key iterators:
#define KEY(type) \
  typedef typename std::multimap<key_type, data_type, key_compare>::type \
    key_ ## type
  KEY(iterator);
  KEY(const_iterator);
  KEY(reverse_iterator);
  KEY(const_reverse_iterator);
#undef KEY

  // By-data iterators:
#define DATA(type) \
  typedef typename std::multimap<data_type, key_type, data_compare>::type \
    data_ ## type
  DATA(iterator);
  DATA(const_iterator);
  DATA(reverse_iterator);
  DATA(const_reverse_iterator);
#undef DATA

  // Create, copy, and delete:
  relation() {}
  relation(const key_compare&key_comp, const data_compare&data_comp) :
    by_key(key_comp),
    by_data(data_comp) {}
  relation(const relation_type&copy) :
    by_key(copy.by_key),
    by_data(copy.by_data) {}
  relation&operator =(const relation_type&copy) {
    by_key = copy.by_key;
    by_data = copy.by_data;
  }
  void swap(relation_type&other) {
    by_key.swap(other.by_key);
    by_data.swap(other.by_data);
  }
  virtual ~relation() {}
  // Access and mutate:
  //  By key:
  key_compare key_comp() const {
    return by_key.key_comp();
  }
#define KEY(method, constness) key_ ## method() constness { \
    return by_key.method(); \
  }
  key_iterator KEY(begin,);
  key_iterator KEY(end,);
  key_const_iterator KEY(begin, const);
  key_const_iterator KEY(end, const);
  key_reverse_iterator KEY(rbegin,);
  key_reverse_iterator KEY(rend,);
  key_const_reverse_iterator KEY(rbegin, const);
  key_const_reverse_iterator KEY(rend, const);
#undef KEY
#define KEY(method,constness) key_ ## method(const key_type&key) constness { \
    return by_key.method(key); \
  }
  key_iterator KEY(find,);
  size_type KEY(count,);
  key_iterator KEY(lower_bound,);
  key_const_iterator KEY(lower_bound, const);
  key_iterator KEY(upper_bound,);
  key_const_iterator KEY(upper_bound, const);
  std::pair<key_iterator, key_iterator> KEY(equal_range,);
  std::pair<key_const_iterator, key_const_iterator> KEY(equal_range, const);
#undef KEY
  // (Return whether insertion was successful as second.)
  std::pair<key_iterator, bool>key_insert
    (const key_type&key, const data_type&data) {
    key_iterator key_hint = by_key.find(key);
    data_iterator data_hint = by_data.find(data);
    if ((unique_key && key_hint != by_key.end()) ||
       (unique_data && data_hint != by_data.end())) {
      return std::pair<key_iterator, bool>(by_key.end(), false);
    }
    by_data.insert(data_hint, std::pair<data_type, key_type>(data, key));
    return std::pair<key_iterator, bool>
      (by_key.insert
       (key_hint, std::pair<key_type, data_type>(key, data)), true);
  }
  void key_erase(key_iterator pos) {
    std::pair<data_iterator, data_iterator>data_pos =
      by_data.equal_range(pos->second);
    key_type match = pos->first;
    for (; data_pos.first != data_pos.second; ++data_pos.first) {
      key_type&candidate = data_pos.first->second;
      if (!by_key.key_comp()(candidate, match) &&
	 !by_key.key_comp()(match, candidate)) {
	by_key.erase(pos);
	by_data.erase(data_pos.first);
	return;
      }
    }
    assert(false);
  }
  size_type key_erase(const key_type&key) {
    size_type result = 0;
    std::pair<key_iterator, key_iterator>range = by_key.equal_range(key);
    while (range.first != range.second) {
      key_iterator tag = range.first++;
      erase(tag);
      ++result;
    }
    return result;
  }

  //  By data:
  data_compare data_comp() const {
    return by_data.data_comp();
  }
#define DATA(method, constness) data_ ## method() constness { \
    return by_data.method(); \
  }
  data_iterator DATA(begin,);
  data_iterator DATA(end,);
  data_const_iterator DATA(begin, const);
  data_const_iterator DATA(end, const);
  data_reverse_iterator DATA(rbegin,);
  data_reverse_iterator DATA(rend,);
  data_const_reverse_iterator DATA(rbegin, const);
  data_const_reverse_iterator DATA(rend, const);
#undef DATA
#define DATA(method, constness) \
  data_ ## method(const data_type&data) constness { \
    return by_data.method(data); \
  }
  data_iterator DATA(find,);
  size_type DATA(count,);
  data_iterator DATA(lower_bound,);
  data_const_iterator DATA(lower_bound, const);
  data_iterator DATA(upper_bound,);
  data_const_iterator DATA(upper_bound, const);
  std::pair<data_iterator, data_iterator> DATA(equal_range,);
  std::pair<data_const_iterator, data_const_iterator> DATA(equal_range, const);
#undef DATA
  // (Return whether insertion was successful as second.)
  std::pair<data_iterator, bool>data_insert
    (const key_type&key, const data_type&data) {
    key_iterator key_hint = by_key.find(key);
    data_iterator data_hint = by_data.find(data);
    if ((unique_key && key_hint != by_key.end()) ||
	(unique_data && data_hint != by_data.end())) {
      return std::pair<data_iterator, bool>(by_data.end(), false);
    }
    by_key.insert(key_hint, std::pair<key_type, data_type>(key, data));
    return std::pair<data_iterator, bool>
      (by_data.insert
       (data_hint, std::pair<data_type, key_type>(data, key)), true);
  }
  void data_erase(data_iterator pos) {
    std::pair<key_iterator, key_iterator>key_pos = by_key.equal_range(pos->second);
    data_type match = pos->first;
    for (; key_pos.first != key_pos.second; ++key_pos.first) {
      data_type&candidate = key_pos.first->second;
      if (!by_data.key_comp()(candidate, match) &&
	 !by_data.key_comp()(match, candidate)) {
	by_data.erase(pos);
	by_key.erase(key_pos.first);
	return;
      }
    }
    assert(false);
  }
  size_type data_erase(const data_type&data) {
    size_type result = 0;
    std::pair<data_iterator, data_iterator>range = by_data.equal_range(data);
    while (range.first != range.second) {
      data_iterator tag = range.first++;
      erase(tag);
      ++result;
    }
    return result;
  }

  // Others:
  void clear() {
    by_key.clear();
    by_data.clear();
  }
  size_type size() const {
    assert(by_key.size() == by_data.size());
    return by_key.size();
  }
  size_type max_size() const {
    assert(by_key.max_size() == by_data.max_size());
    return by_key.max_size();
  }
  bool empty() const {
    assert(by_key.empty() == by_data.empty());
    return by_key.empty();
  }
};

template<class key_type, class data_type, bool unique_key, bool unique_data,
	 class key_compare, class data_compare>std::ostream&operator <<
  (std::ostream&out,
   const relation<key_type, data_type, unique_key, unique_data, key_compare,
     data_compare>&rel) {
  out << "relation{\n";
  for (typename relation<key_type, data_type, unique_key, unique_data,
	 key_compare, data_compare>::key_const_iterator iterator =
	 rel.key_begin(),
	 end = rel.key_end();
       iterator != end;
       ++iterator) {
    out << "  " << *(iterator->first) << " with " << iterator->second << '\n';
  }
  return out << "}\n";
}

#endif
