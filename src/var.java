public class var {
    String name;
    String type;
    boolean is_const;

    public var() {
    }

    public var(String name, String type ,boolean is_const) {
        this.name = name;
        this.type = type;
        this.is_const = is_const;
    }
}
