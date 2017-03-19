package events;

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

import java.util.Set;

public class EventSource<MESSAGE extends Comparable<MESSAGE>> {

    private Set<Listener<MESSAGE>> listeners;

    public EventSource() {
    }

    public EventSource(Set<Listener<MESSAGE>> listeners) {
        this.listeners = listeners;
    }

    public boolean isListener(Listener<MESSAGE> listener){
        for(Listener<MESSAGE> me : this.listeners){
            if(me.equals(listener)) return true;
        }
        return false;
    }

    public void addListener(Listener<MESSAGE> listener){
        this.listeners.add(listener);
    }
    
    public void removeListener(Listener<MESSAGE> listener) {
        this.listeners.remove(listener);
    }

    public void removeAllListeners() {
        this.listeners.clear();
    }

    protected void dispatch(MESSAGE message) {
        for(Listener<MESSAGE> me : this.listeners){
            me.signal(message);
        }
    }
};
