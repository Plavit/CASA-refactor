package common.utility;

import covering.cost.CoverageCost;
import search.Node;

public class NodeCostPair {
    private Node node;
    private CoverageCost coverageCost;

    public NodeCostPair(Node node, CoverageCost coverageCost) {
        this.node = node;
        this.coverageCost = coverageCost;
    }

    public Node getNode() {
        return node;
    }

    public CoverageCost getCoverageCost() {
        return coverageCost;
    }
}
