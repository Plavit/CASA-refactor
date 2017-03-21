package common.utility;

/*
template<class key_type, class data_type, bool unique_key, bool unique_data,
	 class key_compare = std::less<key_type>,
	 class data_compare = std::less<data_type> >class relation {
 */

/*
 * TODO:
 *  - implement using com.google.common.collect.LinkedListMultimap from https://github.com/google/guava
 *
 *  Only one type is needed:
 *   Relation<Node, CoverageCost, true, false>
 */

import com.google.common.collect.ArrayListMultimap;
import covering.cost.CoverageCost;
import search.Node;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;

public class Relation {

    private Multimap<Node, CoverageCost> byKey = ArrayListMultimap.create();
    private Multimap<CoverageCost, Node> byData = ArrayListMultimap.create();

    public Relation() {
    }

    public void key_insert(Node node, CoverageCost rank) {
        //TODO
    }

    public Collection<Map.Entry<Node, CoverageCost>> key_find(Node node) {
        /* TODO: how to implement this? it seems not possible with Multimaps in Java */
        return null;
    }

    public Collection<Map.Entry<CoverageCost, Node>> getDataEntries() {
        return byData.entries();
    }


    public void clear() {
        byKey.clear();
        byData.clear();
    }
}

