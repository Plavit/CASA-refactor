package common.utility;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by NT-39 on 5/4/2017.
 */
public class CombinadicTest {
    Combinadic combinadic;
    int[] sortedSubset;

    @Before
    public void setUp() throws Exception {
        combinadic = new Combinadic();
        sortedSubset = new int[]{1, 2, 4};
    }

    @Test
    public void encode() throws Exception {
        Assert.assertNotNull(combinadic.encode(sortedSubset));
    }

    @Test
    public void decode() throws Exception {
        Assert.assertNotNull(combinadic.decode(1,2));

    }

    @Test
    public void begin() throws Exception {
        Assert.assertNotNull(combinadic.begin(10));

    }

    @Test
    public void previous() throws Exception {
        combinadic.previous(sortedSubset);
        int[] expectedSubset = {0,2,4};
        for (int i = 0; i < sortedSubset.length; i++) {
             Assert.assertEquals(expectedSubset[i],sortedSubset[i]);
        }
    }

    @Test
    public void next() throws Exception {
        combinadic.previous(sortedSubset);
        int[] expectedSubset = {0,2,4};
        for (int i = 0; i < sortedSubset.length; i++) {
            Assert.assertEquals(expectedSubset[i],sortedSubset[i]);
        }
    }

}