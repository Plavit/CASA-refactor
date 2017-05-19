package covering.bookkeeping;

import common.utility.Array;

import java.util.ArrayList;
import java.util.Vector;

public class Entry<T extends Comparable<T>> {

    // TODO THEORETICLY WE DONT NEED ITERATORS FOR ENTRY

    protected Coverage<T> owner;
    protected Integer index;

    public Entry(Coverage owner, Integer index) {
        this.owner = owner;
        this.index = index;
    }

    public T op_getContent() {
        return owner.getContents().getArray().get(index);
    }

    public Entry op_setValue(T value) {
        owner.setValueInContents(value,index);
        return this;
    }

    public Entry op_decrement() {
        Integer value;
        try {
            value = Integer.parseInt(owner.getContents().getArray().get(index).toString());
            value--;
            owner.setValueInContents((T) value,index);
        }catch (Exception e){
            System.err.println("#1-bookkeeping.entry - Parsing problem or unsafe (T)value casting");
        }
        return this;
    }

    public Entry op_increment() {
        Integer value;
        try {
            value = Integer.parseInt(owner.getContents().getArray().get(index).toString());
            value++;
            owner.setValueInContents((T) value,index);
        }catch (Exception e){
            System.err.println("#2-bookkeeping.entry - Parsing problem or unsafe (T)value casting");
        }
        return this;
    }


    public Entry op_brackets(Array<Integer> sortedCombination){
        return new Entry(this.owner,this.owner.encode(getBasicFromArrayList(sortedCombination.getArray())));
    }

    public Entry hintGet(Integer indexHint,
                         Array<Integer>columnsHint,
                         Array<Integer>firstsHint,
                         Array<Integer>countsHint,
                         Array<Integer>sortedCombination){
        int columnHint2[] = getBasicFromArrayList(columnsHint.getArray());
        int firstsHint2[] = getBasicFromArrayList(firstsHint.getArray());
        int countsHint2[] = getBasicFromArrayList(countsHint.getArray());
        int sortedCombination2[] = getBasicFromArrayList(sortedCombination.getArray());
        return new Entry(this.owner,this.owner.encode(indexHint,columnHint2,firstsHint2,countsHint2,sortedCombination2));
    }

    public Entry hintGet(Array<Integer>columnsHint,
                         Array<Integer>firstsHint,
                         Array<Integer>countsHint,
                         Array<Integer>sortedCombination){
        int columnHint2[] = getBasicFromArrayList(columnsHint.getArray());
        int firstsHint2[] = getBasicFromArrayList(firstsHint.getArray());
        int countsHint2[] = getBasicFromArrayList(countsHint.getArray());
        int sortedCombination2[] = getBasicFromArrayList(sortedCombination.getArray());
        return new Entry(this.owner,this.owner.encode(columnHint2,firstsHint2,countsHint2,sortedCombination2));
    }

    public void op_incrementIndex(){
        this.index++;
    }

    public int getSize(){
        return this.owner.getSize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entry<?> entry = (Entry<?>) o;

        if (!owner.equals(entry.owner)) return false;
        return index.equals(entry.index);
    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + index.hashCode();
        return result;
    }

    public Coverage<T> getOwner() {
        return owner;
    }

    public void setOwner(Coverage<T> owner) {
        this.owner = owner;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    private int[] getBasicFromVector(Vector<Integer> arr){
        int basic[] = new int[arr.size()];
        for(int i = 0; i < basic.length; i++){
            basic[i] = arr.get(i);
        }
        return basic;
    }

    private int[] getBasicFromArrayList(ArrayList<Integer> arr){
        int basic[] = new int[arr.size()];
        for(int i = 0; i < basic.length; i++){
            basic[i] = arr.get(i);
        }
        return basic;
    }

}
