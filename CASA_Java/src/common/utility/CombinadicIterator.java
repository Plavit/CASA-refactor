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

import java.util.Vector;

public class CombinadicIterator {
    
    protected Integer populationSize;
    // The iteration will skip sets whose intersection with relevant is empty.
    protected Vector<Integer> relevant;
    protected Vector<Integer> notRelevant;
    
    protected Integer minimumRelevance;
    protected Integer maximumRelevance;
    
    protected Vector<Integer> choiceFromRelevant;
    protected Vector<Integer> choiceFromNotRelevant;
    
    protected Vector<Integer> relevantCombination;
    protected Vector<Integer> combination;
    
    public CombinadicIterator(Integer populationSize, Integer sampleSize, Vector<Integer> relevant) {
        this.populationSize = populationSize;
        this.relevant = relevant;
        this.notRelevant = new Vector<>(populationSize - relevant.size());
        this.minimumRelevance = Math.max(sampleSize - notRelevant.size(), 1);
        this.maximumRelevance = Math.min(relevant.size(), sampleSize);
        this.choiceFromRelevant = Combinadic.begin(maximumRelevance);
        this.choiceFromNotRelevant = Combinadic.begin(sampleSize - maximumRelevance);
        this.relevantCombination = new Vector<>(sampleSize);
        this.combination = new Vector<>(sampleSize);
        
        assert (sampleSize <= populationSize);
        for (int i = 0, j = 0, k = 0; i < notRelevant.size(); ++i, ++j) {
            while ((k < relevant.size()) && (relevant.get(k) == j)) {
                ++j;
                ++k;
            }
            notRelevant.set(i, j);
        }
        updateCombinationFromRelevant();
        updateCombination();
    }
    
    protected void updateCombinationFromRelevant() {
        for (int i = maximumRelevance; i-- > 0;) {
            relevantCombination.set(i, relevant.get(choiceFromRelevant.get(i)));
        }
    }
    
    protected void updateCombination() {
        for (int i = combination.size(); i-- > maximumRelevance;) {
            combination.set(i, notRelevant.get(choiceFromNotRelevant.get(i - maximumRelevance)));
        }
        for (int i = maximumRelevance; i-- > 0;) {
            combination.set(i, relevantCombination.get(i));
        }
        //C_CODE
        //sort(combination + 0, combination + combination.getSize());
        combination.sort(null); //TODO not sure
    }

    //C_CODE
    //const Array<unsigned>CombinadicIterator::operator *() const {
    //#ifndef NDEBUG
    //  for (unsigned i = combination.getSize(); --i;) {
    //      assert(combination[i - 1] < combination[i]);
    //  }
    //#endif
    //  return combination;
    //}
    public Vector<Integer> op_dereference() {
        for (int i = combination.size(); --i > 0;) {
            assert (combination.get(i - 1) < combination.get(i));
        }
        return combination;
    }
    
    
    
    
    //C_CODE
    //CombinadicIterator::operator bool() const {
    //  return combination.getSize();
    //}

    //TODO not sure about this code
    public Integer op_bool() {
        return combination.size();
    }
    
    
    
    
    //C_CODE
    /*
CombinadicIterator&CombinadicIterator::operator ++() {
  if (!combination.getSize()) {
    return *this;
  }
  bool someFromNotRelevant = choiceFromNotRelevant.getSize();
  if (someFromNotRelevant) {
    combinadic.next(choiceFromNotRelevant);
  }
  if (!someFromNotRelevant ||
      choiceFromNotRelevant[choiceFromNotRelevant.getSize() - 1] >=
      populationSize - relevant.getSize()) {
    combinadic.next(choiceFromRelevant);
    if (choiceFromRelevant[maximumRelevance - 1] >= relevant.getSize()) {
      --maximumRelevance;
      if (maximumRelevance < minimumRelevance) {
	combination = Array<unsigned>(0);
	return *this;
      }
      choiceFromRelevant = combinadic.begin(maximumRelevance);
    }
    updateCombinationFromRelevant();
    choiceFromNotRelevant =
      combinadic.begin(combination.getSize() - maximumRelevance);
  }
  updateCombination();
  return *this;
}
    */
    
    //TODO not sure about this code
    public CombinadicIterator op_pre_inc() {
        if (!(combination.size() > 0)) {
            return this;
        }
        boolean someFromNotRelevant = choiceFromNotRelevant.size() > 0;
        if (someFromNotRelevant) {
            Combinadic.next(choiceFromNotRelevant);
        }
        if (!someFromNotRelevant ||
                (choiceFromNotRelevant.get(choiceFromNotRelevant.size() - 1)) >=
                (populationSize - relevant.size())) {
            Combinadic.next(choiceFromRelevant);
            if (choiceFromRelevant.get(maximumRelevance - 1) >= relevant.size()) {
                --maximumRelevance;
                if (maximumRelevance < minimumRelevance) {
                    combination = new Vector<>(0);
                    return this;
                }
                choiceFromRelevant = Combinadic.begin(maximumRelevance);
            }
            updateCombinationFromRelevant();
            choiceFromNotRelevant =
                    Combinadic.begin(combination.size() - maximumRelevance);
        }
        updateCombination();
        return this;
    }
}