package common.utility;

import covering.cost.CoverageCost;
import covering.state.CoveringArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import search.Node;

/**
 * Created by NT-39 on 5/4/2017.
 */
public class ArrayTest {
    Array array;
    int size;
    Node node1, node2, node3, node4;
    CoveringArray coveringArray;
    CoverageCost coverageCostTraveled, coverageCostEstimate;
    Node[] nodeArray;

    @Before
    public void setUp() throws Exception {
        size = 4;
        array = new Array(size);

        coveringArray = new CoveringArray();
        coverageCostTraveled = new CoverageCost();
        coverageCostEstimate = new CoverageCost();

        node1 = new Node(null, coveringArray, coverageCostTraveled, coverageCostEstimate);
        node2 = new Node(node1, coveringArray, coverageCostTraveled, coverageCostEstimate);
        node3 = new Node(node2, coveringArray, coverageCostTraveled, coverageCostEstimate);
        node4 = new Node(node3, coveringArray, coverageCostTraveled, coverageCostEstimate);

        nodeArray = new Node[size];
        nodeArray[0] = node1;
        nodeArray[1] = node2;
        nodeArray[2] = node3;
        nodeArray[3] = node4;

        for (int i = 0; i < array.getSize(); i++) {
            array.set(i, nodeArray[i]);
        }

    }

    @Test
    public void getSize() throws Exception {
        Assert.assertEquals(size, array.getSize());
    }

    @Test
    public void get() throws Exception {
        for (int i = 0; i < array.getSize(); i++) {
            Assert.assertNotNull(array.get(i));
            Assert.assertEquals(nodeArray[i],array.get(i));
        }

    }

    @Test
    public void set() throws Exception {
        for (int i = 1; i <= array.getSize(); i++) {
            array.set(i, nodeArray[size-i]);
        }

        for (int i = 0; i < array.getSize(); i++) {
            Assert.assertNotNull(array.get(i));
            Assert.assertEquals(nodeArray[size-i+1],array.get(i));
        }

    }

    @Test
    public void fill() throws Exception {

    }

    @Test
    public void compareTo() throws Exception {

    }

    @Test
    public void getArray() throws Exception {

    }

}