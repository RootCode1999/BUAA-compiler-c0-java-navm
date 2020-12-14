import java.util.ArrayList;

public class Functionarrary {
    public ArrayList<Function> functions = new ArrayList<>();

    private Functionarrary() {
    }

    private static Functionarrary function_arry = new Functionarrary();

    public static Functionarrary getFunctionTable() { return function_arry; }

    public Function getCurrentFuction(){
        int len = Functionarrary.getFunctionTable().functions.size();
        if(len==0)
            return null;
        return Functionarrary.getFunctionTable().functions.get(len-1);
    }

    public boolean is_function(String name) {
        int len = functions.size(),i=0;
        while(i<len)
        {
            if (functions.get(i).name.equals(name))
                return true;
            i++;
        }
        return false;
    }

    public Function get_function(String name){
        int len = functions.size(),i=0;
        while(i<len)
        {
            if (functions.get(i).name.equals(name))
                return functions.get(i);
            i++;
        }
        return null;
    }

    public int  get_index(String name){
        int len = functions.size(),i=0;
        while(i<len)
        {
            if (functions.get(i).name.equals(name))
                return i;
            i++;
        }
        return -1;
    }
}
