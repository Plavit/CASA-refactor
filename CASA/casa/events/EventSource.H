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


#ifndef EVENT_SOURCE_H
#define EVENT_SOURCE_H

#include <set>
#include "events/Listener.H"

template<class MESSAGE>class EventSource {
public:
  typedef Listener<MESSAGE>		ListenerT;

private:
  std::set<ListenerT*>			listeners;

public:
  virtual ~EventSource() {}
  bool isListener(const ListenerT&listener) const {
    return listeners.find(&listener) != listeners.end();
  }
  void addListener(ListenerT&listener) {
    listeners.insert(&listener);
  }
  void removeListener(ListenerT&listener) {
    listeners.erase(&listener);
  }
  void removeAllListeners() {
    listeners.clear();
  }

protected:
  void dispatch(const MESSAGE&message) {
    for(typename std::set<ListenerT*>::iterator iterator =
	  listeners.begin(),
	  end = listeners.end();
	iterator != end;
	++iterator) {
      (*iterator)->signal(message);
    }
  }
};

#endif
