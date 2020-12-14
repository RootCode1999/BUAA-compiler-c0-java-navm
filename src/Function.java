import java.util.*;

public class Function {
    int index;
    int nameindex;
    int return_slots = 0;
    int param_slots = 0;
    String name;
    String type;
    public ArrayList<Order> orders = new ArrayList<>();
    public ArrayList<Variable> variables = new ArrayList<>();

    public Function(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public void setindex(int index) {
        this.index = index;
    }

    public void setnameindex(int nameindex) {
        this.nameindex = nameindex;
    }

    public void set_return_slots(int return_slots){
        this.return_slots = return_slots;
    }

    public void setparamslots(int paramssize) {
        this.param_slots = paramssize;
    }

    public void setreturnslots(int return_slots) {
        this.return_slots = return_slots;
    }

    public void addorders(Order order) {
        this.orders.add(order);
    }

    public void add_variables(Variable variable) {
        this.variables.add(variable);
    }

    public int get_return_slots() {
        return this.return_slots;
    }

    public boolean is_variable(String name) {
        for (int i = 0; i < variables.size(); i++)
            if (variables.get(i).name.equals(name))
                return true;
        return false;
    }

    public String get_type() {
        return this.type;
    }

    public Variable get_variable(String name, int level) {
        int len = variables.size();
        for (int i = len - 1; i >= 0; i--) {
            if (variables.get(i).name.equals(name) && variables.get(i).level <= level)
                return variables.get(i);
        }
        return null;
    }

    public boolean is_const(String name, int level) {
        int len = variables.size();
        for (int i = len - 1; i >= 0; i--) {
            if (variables.get(i).name.equals(name) && variables.get(i).level <= level)
                return variables.get(i).is_const;
        }
        return false;
    }

    public int get_index(String name, int level) {
        int len = variables.size();
        for (int i = len - 1; i >= 0; i--) {
            if (variables.get(i).name.equals(name) && variables.get(i).level <= level)
                return i;
        }
        return -1;
    }

    public int get_last_order_index() {
        return orders.size();
    }
}
