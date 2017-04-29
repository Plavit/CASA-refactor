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

class Pair {
    private final int key;
    private final int value;

    Pair(int key, int value) {
        this.key = key;
        this.value = value;
    }

    int getKey() {
        return key;
    }

    int getValue() {
        return value;
    }
}

public class Combinadic {
    private static double TWO_PI = 2 * Math.PI;
    private static double INVERSE_E = 1 / Math.E;

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

    private static int guessLastMember(int encoding, int cardinality) {
        double scaledEncoding = encoding * Math.sqrt(TWO_PI * cardinality);
        double rootOfEncoding = Math.pow(scaledEncoding, 1.0 / cardinality);
        Double result = ((INVERSE_E * rootOfEncoding + 0.5) * (double) cardinality);
        return result.intValue();
    }

    private static Pair getLastMemberAndContribution(int encoding, int cardinality) {
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
        return new Pair(member, contribution);
    }

    public static int encode(int[] sortedSubset) {
        int result = 0;
        for (int i = 0; i < sortedSubset.length; ++i) {
            result += PascalTriangle.nCr(sortedSubset[i], i + 1);
        }
        return result;
    }

    public static int[] decode(int encoding, int cardinality) {
        int[] result = new int[cardinality];
        for (int i = cardinality; i > 0;) {
            Pair memberAndContribution
                    = getLastMemberAndContribution(encoding, i);
            result[i] = memberAndContribution.getKey();
            --i;
            encoding -= memberAndContribution.getValue();
        }
        return result;
    }

    public static int[] begin(int size) {
        int[] result = new int[size];
        for (int i = size; i-- > 0;) {
            result[i] = i;
        }
        return result;
    }

    public static void previous(int[] sortedSubset) {
        assert (sortedSubset.length > 0);
        int limit = sortedSubset.length;
        for (int i = 0; i < limit; ++i) {
            int entry = sortedSubset[i];
            if (entry > i) {
                do {
                    sortedSubset[i] = --entry;
                } while (i-- > 0);
                return;
            }
        }
    }

    public static void next(int[] sortedSubset) {
        assert (sortedSubset.length > 0);
        int limit = sortedSubset.length - 1;
        int ceiling = sortedSubset[0];
        for (int i = 0; i < limit; ++i) {
            int entry = ceiling + 1;
            ceiling = sortedSubset[i + 1];
            if (entry < ceiling) {
                sortedSubset[i] = entry;
                return;
            }
            sortedSubset[i] = i;
        }
        sortedSubset[limit]++;
    }
}
