package common.utility;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by NT-39 on 5/4/2017.
 */
public class CombinadicIteratorTest {
    CombinadicIterator combinadicIterator;
    int populationSize, sampleSize;
    int[] relevant, notRelevant;

    @Before
    public void setUp() throws Exception {
        populationSize = 10;
        sampleSize = 2;
        relevant = new int[]{1,2,4};
        notRelevant = new int[]{0,3, 5, 6, 7, 8, 9};
        combinadicIterator = new CombinadicIterator(populationSize,sampleSize,relevant);
    }

    @Test
    public void updateCombinationFromRelevant() throws Exception {
        combinadicIterator.updateCombinationFromRelevant();

        for (int i = 0; i < combinadicIterator.notRelevant.length; i++) {
            Assert.assertEquals(notRelevant[i],combinadicIterator.notRelevant[i]);
        }
        for (int i = 0; i < combinadicIterator.relevant.length; i++) {
            Assert.assertEquals(relevant[i],combinadicIterator.relevant[i]);
        }

    }

    @Test
    public void updateCombination() throws Exception {
        combinadicIterator.updateCombination();

        for (int i = 0; i < combinadicIterator.notRelevant.length; i++) {
            Assert.assertEquals(notRelevant[i],combinadicIterator.notRelevant[i]);
        }
        for (int i = 0; i < combinadicIterator.relevant.length; i++) {
            Assert.assertEquals(relevant[i],combinadicIterator.relevant[i]);
        }
    }

    @Test
    public void op_dereference() throws Exception {
        int[] res =  combinadicIterator.op_dereference();
        int[] expectedRes = {1,2};
        for (int i = 0; i < res.length; i++) {
            Assert.assertEquals(expectedRes[i],res[i]);
        }

    }

    @Test
    public void op_bool() throws Exception {
        Assert.assertTrue(combinadicIterator.op_bool());
    }

    @Test
    public void op_pre_inc() throws Exception {
        Object o = combinadicIterator.op_pre_inc();
    }

}