public class Variable {
    String type;
    String name;
    String functionname;
    int level;
    boolean is_const;
    boolean is_init;
    boolean is_func=false;


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

    public void setfunc_true() {
        this.is_func = true;
    }

    public String get_type(){return type;}

    public void set_type(String type){this.type=type;}

    public String getValue(){
        if(!is_func && (type.equals("int") || type.equals("double"))){
            String res = "";
            for(int i=0;i<8;i++)
                res += "\0";
            return res;
        }
        return name;
    }

    public boolean judge_level(int level){
        if(level == 0)
            return true;
        if (level<this.level)
            return false;
        return true;
    }
}
