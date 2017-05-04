package algorithms;;

import org.junit.*;

/**
 * Created by NT-39 on 5/4/2017.
 */
public class BinarySearchTest {
    BinarySearch binarySearch;
    ExpensiveUnaryPredicate expensiveUnaryPredicate;
    Partitioner partitioner;

    @Before
    public void setUp() throws Exception {
        expensiveUnaryPredicate = null;
        partitioner = new Partitioner() {
            @Override
            public int op_call(int offset, int size) {
                return 0;
            }
        };
        binarySearch = new BinarySearch(expensiveUnaryPredicate,partitioner);
    }

    @Test
    public void op_call() throws Exception {
        Assert.assertEquals(0,binarySearch.op_call(0,0));
        Assert.assertEquals(1,binarySearch.op_call(1,10));

    }

}