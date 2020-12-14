public class constant {
    int index;
    String type;
    String value;

    public constant() {
    }

    public constant(int index, String type, String value) {
        this.type = type;
        this.index = index;
        this.value = value;
    }

    public constant(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getindex() {
        return this.index;
    }

    public String gettype() {
        return this.type;
    }

    public String getvalue() {
        return this.value;
    }

    public void setindex(int index) {
        this.index = index;
    }

    public void settype(String type) {
        this.type = type;
    }

    public void setvalue(String value) {
        this.value = value;
    }
}