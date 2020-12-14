public class Pair<temp1 extends Object, temp2 extends Object> {
    private temp1 first;
    private temp2 second;

    public Pair() {
    }

    public Pair(temp1 first, temp2 second) {
        this.first = first;
        this.second = second;
    }

    public temp1 getFirst() {
        return first;
    }

    public void setFirst(temp1 first) {
        this.first = first;
    }

    public temp2 getSecond() {
        return second;
    }

    public void setSecond(temp2 second) {
        this.second = second;
    }
}