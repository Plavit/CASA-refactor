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


#include <cstdlib>
#include <ctime>
#include <iostream>
#include <sstream>

#include "io/Usage.H"
#include "io/SpecificationFile.H"
#include "io/ConstraintFile.H"
#include "io/OutputFile.H"

#include "covering/state/CoveringArray.H"

#include "annealing/Anneal.H"

using namespace std;

int main(int argc, char**argv) {
  // Absorb the command line arguments into shared variables.
  parseOptions(argc, argv);
  // Process the random seed.
  if (!seeded) {
    seed = time(NULL);
    cout << "Choosing random seed " << seed << endl;
  }
  srand(seed);
  // Process the specification file.
  SpecificationFile specification(modelFile);
  // Process the constraints file.
  ConstraintFile constraints(constraintFile ? constraintFile : "");
  // ready the output file
  ostringstream defaultOutputFile("anneal", ios::out | ios::app);
  if (!outputFile) {
    defaultOutputFile << '.' << seed;
    defaultOutputFile << ".out";
    cout << "Using output filename " << defaultOutputFile.str() << endl;
  }
  OutputFile output(outputFile ? outputFile : defaultOutputFile.str());

  // Start the core algorithm.
  if (verbose) {
    cout << "Passing control to primary algorithm" << endl;
  }
  CoveringArray solution =
    anneal
      (specification,
       constraints,
       iterations,
       startingTemperature,
       decrement);
  if (verbose) {
    cout << "Control returned from primary algorithm" << endl;
  }

  // Store the results in the output file.
  output.setCoveringArray(solution);
  output.write();

  if (verbose) {
    cout << "Done" << endl;
  }
  return 0;
}
