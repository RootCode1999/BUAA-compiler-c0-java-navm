package symbol;

public class Variable {
    String type;
    String name;
    String functionname;
    int level;
    boolean is_const;
    boolean is_init;


    public Variable() {

    }

    public Variable(String type, String name, boolean is_const, boolean is_init, int level) {
        this.type = type;
        this.name = name;
        this.is_const = is_const;
        this.is_init = is_init;
        this.level =level;
    }

    public void setfunctionname(String functionname) {
        this.functionname = functionname;
    }

    public String get_type(){return type;}

    public void set_type(String type){this.type=type;}

    public boolean judge_level(int level){
        if(level == 0)
            return true;
        if (level<this.level)
            return false;
        return true;
    }
}
