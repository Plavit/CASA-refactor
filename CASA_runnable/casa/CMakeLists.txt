cmake_minimum_required(VERSION 3.6)
project(casa)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES
        algorithms/BinarySearch.C
        algorithms/BinarySearch.H
        annealing/Anneal.C
        annealing/Anneal.H
        annealing/AnnealingFilter.H
        annealing/AnnealingPartitioner.C
        annealing/AnnealingPartitioner.H
        annealing/AnnealingSuccess.C
        annealing/AnnealingSuccess.H
        annealing/Bounds.C
        annealing/Bounds.H
        covering/bookkeeping/Coverage.H
        covering/bookkeeping/Options.C
        covering/bookkeeping/Options.H
        covering/cost/CoverageCost.H
        covering/filter/CoveringArrayAnnealingFilter.H
        covering/goal/CoverageGoal.H
        covering/heuristic/CoveringArrayHeuristic.H
        covering/report/IterationReport.H
        covering/space/CoveringArraySpace.C
        covering/space/CoveringArraySpace.H
        covering/space/GraftSpace.C
        covering/space/GraftSpace.H
        covering/space/SingleChangeSpace.C
        covering/space/SingleChangeSpace.H
        covering/state/CoveringArray.C
        covering/state/CoveringArray.H
        covering/state/CoveringArrayEntry.C
        covering/state/CoveringArrayRow.C
        covering/state/CoveringArraySubRow.C
        events/EventSource.H
        events/Listener.H
        io/ConstraintFile.C
        io/ConstraintFile.H
        io/OutputFile.C
        io/OutputFile.H
        io/SpecificationFile.C
        io/SpecificationFile.H
        io/Usage.C
        io/Usage.H
        sat/SAT.C
        sat/SAT.H
        search/Filter.H
        search/Goal.H
        search/GreedyFilter.H
        search/Guide.H
        search/Heuristic.H
        search/Node.H
        search/Search.H
        search/SearchConfiguration.H
        search/SearchFinish.H
        search/SearchIteration.H
        search/StateGuide.H
        search/StateSpace.H
        Main.C)

add_executable(casa ${SOURCE_FILES})