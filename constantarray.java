import java.util.ArrayList;

public class constantarray {
    public ArrayList<constant> constants = new ArrayList<>();

    private constantarray() {}

    private static constantarray constant_array = new constantarray();

    public static constantarray getConstantTable() { return constant_array; }

    public boolean is_variable(String name) {
        int len = constants.size(),i=0;
        while(i<len) {
            if (constants.get(i).value.equals(name))
                return true;
            i++;
        }
        return false;
    }

    public int get_index(String name) {
        int len = constants.size(),i=0;
        while(i<len) {
            if (constants.get(i).value.equals(name))
                return i;
            i++;
        }
        return 0;
    }


    public void add_constant(constant constant){
        constants.add(constant);
    }
}
