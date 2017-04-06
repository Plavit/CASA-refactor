package covering.state;

import java.lang.reflect.Array;

/**
 * Created by NT-39 on 4/6/2017.
 */
public class CoveringArraySubRow {

    void updateTracking(final Integer[] values){
        Integer size = values.length;
        assert (size == owner.getOptions());
        Integer strength = owner.coverage.getStrength();
        Integer limit = owner.coverage.getOptions().getSize();
        Integer[] oldRow = new Integer[size];
        for (Integer i = size; i > 0;i--) {
            oldRow[i] = owner(row, i);
        }

    }
}
