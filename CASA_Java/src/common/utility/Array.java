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

import java.util.ArrayList;
import java.util.RandomAccess;

class SharedArray<T extends Comparable> implements RandomAccess {
    private ArrayList<T> array;
    private int references;

    public SharedArray(int count) {
        array = new ArrayList<>(count);
        references = 1;
    }

    private SharedArray(ArrayList<T> array) {
        this.array = new ArrayList<>(array);
        references = 1;
    }

    public int getSize() {
        return array.size();
    }

    public T get(int index) {
        assert references > 0;
        return array.get(index);
    }

    public void set(int index, T value) {
        assert references > 0;
        array.set(index, value);
    }

    void fill(final T filler) {
        assert references > 0;
        final int size = array.size();
        for (int i=0; i < size; i++) {
            array.set(i, filler);
        }
    }

    synchronized void ref() {
        assert references > 0;
        references++;
    }

    synchronized void unref() {
        assert references > 0;
        references--;
        if (references == 0)
            array = null;
    }

    synchronized SharedArray<T> cloneIfMultiref() {
        assert references > 0;
        if (references > 1) {
            references--;
            return new SharedArray<T>(array);
        }
        return this;
    }
}

public class Array<T extends Comparable<T>> implements Comparable<Array<T>> {
    protected SharedArray<T> sharedArray;

    public Array(int size) {
        sharedArray = new SharedArray<>(size);
    }

    public int getSize() {
        return sharedArray.getSize();
    }

    public T get(int index) {
        return sharedArray.get(index);
    }

    public void set(int index, T value) {
        sharedArray.set(index, value);
    }

    public void fill(T filler) {
        sharedArray.fill(filler);
    }

    @Override
    public int compareTo(Array<T> otherArray) {
        SharedArray<T> other = otherArray.sharedArray;
        int size = sharedArray.getSize();
        if (size < other.getSize()) {
            return -1;
        }
        if (size > other.getSize()) {
            return 1;
        }
        int res = 0;
        for (int i = 0; i < size; ++i) {
            res = sharedArray.get(i).compareTo(other.get(i));
            if (res != 0)
                break;
        }
        return res;
    }


}
