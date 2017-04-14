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

import java.util.Arrays;

public class CombinadicIterator {
    
    protected Integer populationSize;
    // The iteration will skip sets whose intersection with relevant is empty.
    protected int[] relevant;
    protected int[] notRelevant;
    
    protected int minimumRelevance;
    protected int maximumRelevance;
    
    protected int[] choiceFromRelevant;
    protected int[] choiceFromNotRelevant;
    
    protected int[] relevantCombination;
    protected int[] combination;
    
    public CombinadicIterator(int populationSize, int sampleSize, int[] relevant) {
        this.populationSize = populationSize;
        this.relevant = relevant;
        this.notRelevant = new int[populationSize - relevant.length];
        this.minimumRelevance = Math.max(sampleSize - notRelevant.length, 1);
        this.maximumRelevance = Math.min(relevant.length, sampleSize);
        this.choiceFromRelevant = Combinadic.begin(maximumRelevance);
        this.choiceFromNotRelevant = Combinadic.begin(sampleSize - maximumRelevance);
        this.relevantCombination = new int[sampleSize];
        this.combination = new int[sampleSize];
        
        assert (sampleSize <= populationSize);
        for (int i = 0, j = 0, k = 0; i < notRelevant.length; ++i, ++j) {
            while ((k < relevant.length) && (relevant[k] == j)) {
                ++j;
                ++k;
            }
            notRelevant[i] = j;
        }
        updateCombinationFromRelevant();
        updateCombination();
    }
    
    protected void updateCombinationFromRelevant() {
        for (int i = maximumRelevance; i-- > 0;) {
            relevantCombination[i] = relevant[choiceFromRelevant[i]];
        }
    }
    
    protected void updateCombination() {
        for (int i = combination.length; i-- > maximumRelevance;) {
            combination[i] = notRelevant[choiceFromNotRelevant[i - maximumRelevance]];
        }
        for (int i = maximumRelevance; i-- > 0;) {
            combination[i] = relevantCombination[i];
        }
        Arrays.sort(combination);
    }

    //C_CODE
    //const Array<unsigned>CombinadicIterator::operator *() const {
    //#ifndef NDEBUG
    //  for (unsigned i = combination.getlength; --i;) {
    //      assert(combination[i - 1] < combination[i]);
    //  }
    //#endif
    //  return combination;
    //}
    public int[] op_dereference() {
        for (int i = combination.length; --i > 0;) {
            assert (combination[i - 1] < combination[i]);
        }
        return combination;
    }
    
    
    
    
    //C_CODE
    //CombinadicIterator::operator bool() const {
    //  return combination.getlength;
    //}

    //TODO not sure about this code
    public Integer op_bool() {
        return combination.length;
    }
    
    
    
    
    //C_CODE
    /*
CombinadicIterator&CombinadicIterator::operator ++() {
  if (!combination.getlength) {
    return *this;
  }
  bool someFromNotRelevant = choiceFromNotRelevant.getlength;
  if (someFromNotRelevant) {
    combinadic.next(choiceFromNotRelevant);
  }
  if (!someFromNotRelevant ||
      choiceFromNotRelevant[choiceFromNotRelevant.getlength - 1] >=
      populationSize - relevant.getlength) {
    combinadic.next(choiceFromRelevant);
    if (choiceFromRelevant[maximumRelevance - 1] >= relevant.getlength) {
      --maximumRelevance;
      if (maximumRelevance < minimumRelevance) {
	combination = Array<unsigned>(0);
	return *this;
      }
      choiceFromRelevant = combinadic.begin(maximumRelevance);
    }
    updateCombinationFromRelevant();
    choiceFromNotRelevant =
      combinadic.begin(combination.getlength - maximumRelevance);
  }
  updateCombination();
  return *this;
}
    */
    
    //TODO not sure about this code
    public CombinadicIterator op_pre_inc() {
        if (!(combination.length > 0)) {
            return this;
        }
        boolean someFromNotRelevant = choiceFromNotRelevant.length > 0;
        if (someFromNotRelevant) {
            Combinadic.next(choiceFromNotRelevant);
        }
        if (!someFromNotRelevant ||
                (choiceFromNotRelevant[choiceFromNotRelevant.length - 1]) >=
                (populationSize - relevant.length)) {
            Combinadic.next(choiceFromRelevant);
            if (choiceFromRelevant[maximumRelevance - 1] >= relevant.length) {
                --maximumRelevance;
                if (maximumRelevance < minimumRelevance) {
                    combination = new int[0];
                    return this;
                }
                choiceFromRelevant = Combinadic.begin(maximumRelevance);
            }
            updateCombinationFromRelevant();
            choiceFromNotRelevant =
                    Combinadic.begin(combination.length - maximumRelevance);
        }
        updateCombination();
        return this;
    }
}