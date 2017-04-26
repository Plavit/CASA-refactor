package covering.bookkeeping;

public class Entry<T> {

    protected Coverage<T> owner;
    protected Integer index;

    public Entry(Coverage owner, Integer index) {
        this.owner = owner;
        this.index = index;
    }

    //        operator T() const {
//            return owner.contents[index];
//        }
    public T op_getContent() {
        //TODO
        return owner.contents.getArray().get(index);
    }

    //        Entry&operator =(const T&value) {
//            owner.contents[index] = value;
//            return *this;
//        }
    public Entry op_setValue(T value) {
        owner.contents.getArray().set(index, value);
        return this;
    }

    //        Entry&operator --() {
//            --owner.contents[index];
//            return *this;
//        }
    public Entry op_decrement() {
        //TODO how to decrement T ???
        owner.contents[index]--;
        return this;
    }

    //        Entry&operator ++() {
//            ++owner.contents[index];
//            return *this;
//        }
    public Entry op_increment() {
        //TODO
    }

}
