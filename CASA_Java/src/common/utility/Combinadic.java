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
import javafx.util.Pair;

public class Combinadic {

    public static double TWO_PI = 2 * Math.PI;
    public static double INVERSE_E = 1 / Math.E;

    // We want result to approximately satisfy
    //  cardinality! * encoding = result * (result-1) * ... * (result-(cardinality-1))
    // The right-hand side usually has degree >= 5, so we need to trivialize it a bit.
    // It can be replaced with the overapproximation:
    //  cardinality! * encoding = (result - (cardinality-1) / 2) ^ cardinality
    // Factorials are also expensive, so we use Stirling's approximation:
    //  sqrt(TWO_PI * cardinality) * ((cardinality / e) ^ cardinality) * encoding =
    //    (result - (cardinality-1) / 2) ^ cardinality
    // Now, to solve for result, take the cardinality'th root of both sides:
    //  (cardinality / e) *
    //  (encoding * sqrt(TWO_PI * cardinality)) ^ (1 / cardinality) =
    //    result - (cardinality - 1)/2
    // And then rearrange that and add a half to make the flooring round to nearest:
    //  result =
    //    (cardinality / e) *
    //    (encoding * sqrt(TWO_PI * cardinality)) ^ (1 / cardinality) +
    //    cardinality / 2 =
    //      ((1 / e) *
    //        (encoding * sqrt(TWO_PI * cardinality)) ^ (1 / cardinality) + 0.5) *
    //      cardinality

    //TODO not sure about "static"
    protected static Integer guessLastMember(Integer encoding, Integer cardinality) {
        double scaledEncoding = encoding * Math.sqrt(TWO_PI * cardinality);
        double rootOfEncoding = Math.pow(scaledEncoding, 1.0 / cardinality);
        Double result = ((INVERSE_E * rootOfEncoding + 0.5) * (double) cardinality);
        return result.intValue();
    }

    //TODO not sure about "static"
    protected static Pair<Integer, Integer> getLastMemberAndContribution(Integer encoding, Integer cardinality) {
        int member = guessLastMember(encoding, cardinality);
        int contribution = PascalTriangle.nCr(member, cardinality);
        if (contribution > encoding) {
            do {
                contribution = PascalTriangle.nCr(--member, cardinality);
            } while (contribution > encoding);
        } else {
            int nextContribution = PascalTriangle.nCr(member + 1, cardinality);
            while (nextContribution <= encoding) {
                ++member;
                contribution = nextContribution;
                nextContribution = PascalTriangle.nCr(member + 1, cardinality);
            }
        }
        return new Pair<Integer, Integer>(member, contribution);
    }

    //TODO not sure about "static"
    public static Integer encode(Vector<Integer> sortedSubset) {
        Integer result = 0;
        for (int i = 0; i < sortedSubset.size(); ++i) {
            result += PascalTriangle.nCr(sortedSubset.get(i), i + 1);
        }
        return result;
    }

    //TODO not sure about "static"
    public static Vector<Integer> decode(Integer encoding, Integer cardinality) {
        Vector<Integer> result = new Vector<>(cardinality);
        for (int i = cardinality; i > 0;) {
            Pair<Integer, Integer> memberAndContribution
                    = getLastMemberAndContribution(encoding, i);
            result.set(i, memberAndContribution.getKey());
            --i;
            encoding -= memberAndContribution.getValue();
        }
        return result;
    }

    //TODO not sure about "static"
    public static Vector<Integer> begin(Integer size) {
        Vector<Integer> result = new Vector<>(size);
        for (int i = size; i-- > 0;) {
            result.set(i, i);
        }
        return result;
    }

    public void previous(Vector<Integer> sortedSubset) {
        throw new UnsupportedOperationException();
    }

    //TODO not sure about "static"
    public static void next(Vector<Integer> sortedSubset) {
        assert (sortedSubset.size() > 0);
        int limit = sortedSubset.size() - 1;
        int ceiling = sortedSubset.get(0);
        for (int i = 0; i < limit; ++i) {
            int entry = ceiling + 1;
            ceiling = sortedSubset.get(i + 1);
            if (entry < ceiling) {
                sortedSubset.set(i, entry);
                return;
            }
            sortedSubset.set(i, i);
        }
        //C_CODE
        //++sortedSubset[limit];
        sortedSubset.set(limit, sortedSubset.get(limit) + 1);
    }
}
