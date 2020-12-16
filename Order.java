import java.util.*;

public class Order {
    int index;
    int level;
    String opcode = "";
    String type = "";
    int while_index;
    public ArrayList<Long> opers = new ArrayList<>();

    public int getindex() {
        return this.index;
    }

    public String type() {
        return this.type;
    }

    public void addOper(Long number){
        opers.add(number);
    }

    public void setOpcode(String name){
        this.opcode=name;
    }

    public void setlevel(int level) { this.level = level; }

    public String getOpcode() {
        return this.opcode;
    }

    public int get_level(){
        return this.level;
    }

    public Long get_oper(){
        int len=opers.size();
        return opers.get(len-1);
    }

    public String get_opcode(){
        return this.opcode;
    }

    public String get_type(){
        return this.type;
    }

    public Order() {
    }

    public Order(int index, String opcode) {
        this.index = index;
        this.opcode = opcode;
        this.level = level;
    }

    public Order(String opcode , int level) {
        this.opcode = opcode;
        this.level = level;
    }

    public long last_oper(){
        int len = opers.size();
        return opers.get(len-1);
    }

}
