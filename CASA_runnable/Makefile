# The target
TARGET = casa-1.1b

# The compiler options
CC = g++
CFLAGS = \
    -Wredundant-decls -Wall -Werror -g \
    -Icasa -Icommon -Icommon/utility -Iminisat/solver -Iminisat/include \
    -DSEARCH_PROGRESS
LFLAGS =
DFLAGS = -MM

#the .C files, without their extensions
COMBINATORICS_SOURCES = PascalTriangle Combinadic CombinadicIterator
MINISAT_SOURCES = Solver
SAT_SOURCES = SAT
IO_SOURCES = Usage SpecificationFile ConstraintFile OutputFile
STATE_SOURCES = CoveringArray CoveringArrayEntry CoveringArrayRow CoveringArraySubRow
SPACE_SOURCES = CoveringArraySpace SingleChangeSpace GraftSpace
BOOKKEEPING_SOURCES = Options
ANNEALING_SOURCES = Anneal AnnealingSuccess Bounds AnnealingPartitioner
ALGORITHM_SOURCES = BinarySearch
MAIN_SOURCES = Main
SOURCES = \
    $(COMBINATORICS_SOURCES:%=common/utility/%) \
    $(MINISAT_SOURCES:%=minisat/solver/%) \
    $(SAT_SOURCES:%=casa/sat/%) \
    $(IO_SOURCES:%=casa/io/%) \
    $(STATE_SOURCES:%=casa/covering/state/%) \
    $(SPACE_SOURCES:%=casa/covering/space/%) \
    $(BOOKKEEPING_SOURCES:%=casa/covering/bookkeeping/%) \
    $(ANNEALING_SOURCES:%=casa/annealing/%) \
    $(ALGORITHM_SOURCES:%=casa/algorithms/%) \
    $(MAIN_SOURCES:%=casa/%)

# The rules
all:	$(TARGET) TAGS

$(TARGET):	$(SOURCES:%=%.o)
	$(CC) -o $@ $(filter %.o,$^) $(LFLAGS)

$(SOURCES:%=%.o):	Makefile
	$(CC) -c -o $@ $(@:%.o=%.C) $(CFLAGS)

$(SOURCES:%=%.d):	%.d:%.C Makefile
	$(CC) $(DFLAGS) $(@:%.d=%.C) $(CFLAGS) | sed 's,.*\.o:,$(@:%.d=%.o) $@:	,g' > $@
ifneq ($(MAKECMDGOALS),clean)
ifneq ($(MAKECMDGOALS),distclean)
-include $(SOURCES:%=%.d)
endif
endif

Dependencies:	$(SOURCES:%=%.d)
	sed -e 's/://g' -e 's/[^ ][^ ]*\.d//g' -e 's/[^ ][^ ]*\.o//g' -e 's/[ 	\\][ 	\\]*/ /g' $(SOURCES:%=%.d) | tr ' ' "\n" | sort | uniq | tr "\n" ' ' | sed 's/^/ALL_INPUTS =/' > $@
ifneq ($(MAKECMDGOALS),clean)
ifneq ($(MAKECMDGOALS),distclean)
-include Dependencies
endif
endif

TAGS:	$(ALL_INPUTS)
	etags $^

clean:
	-$(RM) $(TARGET) $(SOURCES:%=%.o)

distclean:	clean
	-$(RM) $(SOURCES:%=%.d) Dependencies TAGS

.PHONY:	all clean distclean
