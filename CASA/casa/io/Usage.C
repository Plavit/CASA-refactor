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
#include <iostream>
#include <posix/getopt.h>

#include "io/Usage.H"

using namespace std;

const char*PROGRAM_NAME =
  "Covering Arrays by Simulated Annealing (CASA)";

const char*PROGRAM_VERSION =
  "1.1b";

const char*BUG_ADDRESS =
  "bgarvin@cse.unl.edu";

static const char*PROGRAM_DOC =
  "Builds mixed-level covering arrays under constraints\n"
  "\n"
  "Copyright 2008, 2009 Brady J. Garvin\n"
  "\n"
  "CASA is free software: you can redistribute it and/or modify it\n"
  "under the terms of the GNU General Public License as published by\n"
  "the Free Software Foundation, either version 3 of the License, or\n"
  "(at your option) any later version.\n"
  "\n"
  "CASA is distributed in the hope that it will be useful, but\n"
  "WITHOUT ANY WARRANTY; without even the implied warranty of\n"
  "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
  "GNU General Public License for more details.\n"
  "\n"
  "You should have received a copy of the GNU General Public License\n"
  "along with CASA.  If not, see <http://www.gnu.org/licenses/>.\n";

static const char*USAGE_DOC =
  "[OPTIONS] [MODEL_FILE]";

static const char*ARG_DOC =
  "  -o, --output         [FILE]   write to the given file, regardless of seed\n"
  "  -c, --constrain      [FILE]   incorporate the given constraint file\n"
  "\n"
  "  -s, --seed           [SEED]   set the seed value for the random number generator\n"
  "\n"
  "  -i, --iterations     [COUNT]  set the initial number of iterations allowed at each array size\n"
  "  -r, --retries        [COUNT]  set the number of retries allowed at the same array size\n"
  "  -p, --partition      [RATIO]  set the weight of the upper bound in the binary search parition\n"
  "\n"
  "  -t, --temperature    [TEMP]   set the initial temperature\n"
  "  -d, --multiplier     [RATIO]  set the temperature multiplier applied each iteration\n"
  "\n"
  "  -l, --lower-bound    [SIZE]   let the covering array be no smaller than the given size\n"
  "  -u, --upper-bound    [SIZE]   let the covering array be no larger than the given size\n"
  "  -n, --known-size     [SIZE]   lock the covering array at the given size\n"
  "\n"
  "  -v, --version                 show the current version and exit\n"
  "  -h, --help                    show this help and exit\n";

static const char*shortOptions =
  "o:c:s:i:r:p:t:d:l:u:n:vh";

static struct option longOptions[] = {
  {"output",		required_argument,	NULL,	'o'},
  {"constrain",		required_argument,	NULL,	'c'},
  {"seed",		required_argument,	NULL,	's'},
  {"iterations",	required_argument,	NULL,	'i'},
  {"retries",		required_argument,	NULL,	'r'},
  {"partition",		required_argument,	NULL,	'p'},
  {"temperature",	required_argument,	NULL,	't'},
  {"decrement",		required_argument,	NULL,	'd'},
  {"lower-bound",	required_argument,	NULL,	'l'},
  {"upper-bound",	required_argument,	NULL,	'u'},
  {"known-size",	required_argument,	NULL,	'n'},
  {"version",		no_argument,		NULL,	'v'},
  {"help",		no_argument,		NULL,	'h'},
  {0,			0,			0,	 0 }};

bool				verbose				= true;
const char*			modelFile			= NULL;
const char*			constraintFile			= NULL;
const char*			outputFile			= NULL;
bool				seeded				= false;
int				seed;
double				startingTemperature		= 0.5L;
double				decrement			= 0.99999L;
unsigned			iterations			= 256;
unsigned			retries				= 2;
unsigned			lowerBound			= 0;
unsigned			upperBound			= 0;
double				searchPartition			= 2.L/3.L;

static void version() {
  cerr << PROGRAM_NAME << ' ' << PROGRAM_VERSION << '\n';
  exit(0);
}

static const char*name;
static void usage(int error) {
  cerr << PROGRAM_NAME << ' ' << PROGRAM_VERSION << " - " << PROGRAM_DOC <<
    "\n\nUsage: " << name << ' ' << USAGE_DOC << "\n\n" << ARG_DOC << "\n\n" <<
    "Send bug reports to <" << BUG_ADDRESS << ">.\n";
  exit(error);
}

void parseOptions(int argc, char*const*argv) {
  name = *argv;
  bool seen[256];
  (void)seen; // Workaround for a bug in some versions of GCC
  for (unsigned i = 256; i-- > 0;) {
    seen[i] = false;
  }
  for (;;) {
    int index = 0;
    int found = getopt_long(argc, argv, shortOptions, longOptions, &index);
    switch (found) {
    case 'o': // output
      outputFile = optarg;
      break;
    case 'c': // constrain
      constraintFile = optarg;
      break;
    case 's': // seed
      seeded = true;
      seed = atoi(optarg);
      break;
    case 'i': // iterations
      iterations = atoi(optarg);
      break;
    case 'r': // retries
      retries = atoi(optarg);
      break;
    case 'p': // partition
      searchPartition = atof(optarg);
      break;
    case 't': // temperature
      startingTemperature = atof(optarg);
      break;
    case 'd': // decrement
      decrement = atof(optarg);
      break;
    case 'l': // lower-bound
      lowerBound = atoi(optarg);
      break;
    case 'u': // upper-bound
      upperBound = atoi(optarg);
      break;
    case 'n': // known-size
      lowerBound = upperBound = atoi(optarg);
      break;
    case 'v': // version
      version();
      break;
    case 'h': // help
      usage(0);
      break;
    default: // done with options
      if (argc - optind < 1) {
	usage(1);
      }
      if (argc - optind > 1) {
	cerr << "Warning: ignoring extraneous arguments after model file ``" <<
	  argv[optind] << "''." << endl;
      }
      modelFile = argv[optind];
      return;
    }
    seen[(unsigned)found] = true;
  }
}
