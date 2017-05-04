package common.utility;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by NT-39 on 5/4/2017.
 */
public class PairTest {
    Pair pair;
    int key, value;

    @Before
    public void setUp() throws Exception {
        key = 1;
        value = 10;
        pair = new Pair(key, value);
    }

    @Test
    public void getKey() throws Exception {
        Assert.assertEquals(key,pair.getKey());

    }

    @Test
    public void getValue() throws Exception {
        Assert.assertEquals(value,pair.getValue());

    }

}