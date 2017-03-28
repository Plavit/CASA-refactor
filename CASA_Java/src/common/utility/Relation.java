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

import com.google.common.collect.*;
import covering.cost.CoverageCost;
import search.Node;

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

    public PeekingIterator<Map.Entry<Node, CoverageCost>> key_find(Node node) {
        /* TODO: how to implement this? it seems not possible with Multimaps in Java */
        return null;
    }

    public void key_erase(Map.Entry<Node, CoverageCost> k) {
    }

    /* TODO: called from search, exectutes key_erase() interleaved */
    public Collection<Map.Entry<Node, CoverageCost>> getKeys() {
        return byKey.entries();
    }

    /* TODO: called from search, exectutes remove() */
    public Collection<Map.Entry<CoverageCost, Node>> getDataEntries() {
        return byData.entries();
    }

    public boolean isEmpty() {
        return true; // TODO
    }

    public void clear() {
        byKey.clear();
        byData.clear();
    }

}

