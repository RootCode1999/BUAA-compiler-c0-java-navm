package Analyser;

public class variable {
    String name;
    String type;
    boolean is_const;

    public variable() {
    }

    public variable(String name, String type ,boolean is_const) {
        this.name = name;
        this.type = type;
        this.is_const = is_const;
    }
}
