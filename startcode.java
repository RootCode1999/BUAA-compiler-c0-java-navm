import java.util.*;

public class startcode {
    public ArrayList<Order> orders = new ArrayList<>();
    public ArrayList<Variable> variables = new ArrayList<>();

    private static startcode startCode_array = new startcode();

    private startcode(){}

    public static startcode getStartCodeTable() { return startCode_array; }

    public boolean is_variable(String name) {
        int len=variables.size();
        int i=0;
        while(i<len) {
            if (variables.get(i).name.equals(name))
                return true;
            i++;
        }
        return false;
    }

    public boolean is_const(String name) {
        int len=variables.size(),i=0;
        while(i<len) {
            if (variables.get(i).name.equals(name))
                return variables.get(i).is_const;
            i++;
        }
        return false;
    }

    public int get_index(String name) {
        int len=variables.size(),i=0;
        while(i<len) {
            if (variables.get(i).name.equals(name))
                return i;
            i++;
        }
        return 0;
    }

    public Variable get_variable(String name){
        int len = variables.size();
        int i=0;
        while(i<len) {
            if (variables.get(i).name.equals(name))
                return variables.get(i);
            i++;
        }
        return null;
    }

}
