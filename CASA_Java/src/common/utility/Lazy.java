package common.utility;

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

public class Lazy<T> {

    T implementation;
    long referenceCount;


    public Lazy() {
        this.referenceCount = 0;
        this.implementation = null;
    }

    public Lazy(T implementation) {
        this.implementation = implementation;
        if(implementation != null) this.referenceCount = 1;
    }

    public Lazy(final Lazy<T> copy){
        this.implementation = copy.getImplementation();
        this.referenceCount = copy.getReferenceCount();
        if(this.implementation != null) this.referenceCount++;
    }

    public T getImplementation() {
        return implementation;
    }

    public void setImplementation(T implementation) {
        this.implementation = implementation;
    }

    public long getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(long referenceCount) {
        this.referenceCount = referenceCount;
    }

    public void destroy(){
        this.implementation = null;
        this.referenceCount = 0;
    }
}
