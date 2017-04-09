package common.utility;

import com.google.common.collect.*;
import covering.cost.CoverageCost;
import covering.state.CoveringArray;
import search.Node;

import java.util.*;

public class Relation {

    private TreeMap<CoveringArray, NodeCostPair> byKey = new TreeMap<>();
    private TreeMultimap<CoverageCost, Node> byData = TreeMultimap.create();

    public Relation() {
    }

    public void key_insert(Node node, CoverageCost rank) {
        if (!byKey.containsKey(node.getState()))
            return;
        byKey.put(node.getState(), new NodeCostPair(node, rank));
        byData.put(rank, node);
    }

    public boolean isEmpty() {
        assert byKey.isEmpty() == byData.isEmpty();
        return byKey.isEmpty();
    }

    public NodeCostPair popBest() {
        CoverageCost c = byData.keySet().iterator().next();
        NavigableSet<Node> ns = byData.get(c);
        Node n = ns.pollFirst();
        assert n != null;
        NodeCostPair ncp = byKey.remove(n.getState());
        assert ncp != null;
        return ncp;
    }

    public void prune(Set<Node> nodesToKeep) {
        for (NodeCostPair ncp : byKey.values()) {
            Node node = ncp.getNode();
            if (!nodesToKeep.contains(node)) {
                node.destruct();
                byData.remove(ncp.getCoverageCost(), node);
            }
        }
    }

    public void clear() {
        for (NodeCostPair ncp : byKey.values()) {
            ncp.getNode().destruct();
        }
        byKey.clear();
        byData.clear();
    }

    public Node get_similar_key(Node node) {
        NodeCostPair ncp = byKey.get(node.getState());
        if (ncp != null)
            return ncp.getNode();
        else
            return null;
    }

    public void remove_by_key(Node node) {
        NodeCostPair ncp = byKey.remove(node.getState());
        assert ncp != null;
        assert ncp.getNode() == node;
        byData.remove(ncp.getCoverageCost(), node);
    }
}