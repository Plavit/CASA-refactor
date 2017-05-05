package search;

public class ChildrenAsk {
    float proportion;
    long count;

    public ChildrenAsk() {
    }

    public ChildrenAsk(float proportion, long count) {
        this.proportion = proportion;
        this.count = count;
    }

    public float getProportion() {
        return proportion;
    }

    public void setProportion(float proportion) {
        this.proportion = proportion;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
