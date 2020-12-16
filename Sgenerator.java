import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Sgenerator {
    String outputFile;

    public Sgenerator(String outputFile) {
        this.outputFile = outputFile;
    }

    public void generate(){
        try {
            File f = new File(outputFile);
            if(f.exists() && f.isFile())
                f.delete();
            if(f.isDirectory()) {
                System.out.println("this is a directory, are you sure you typed the correct output file?");
                System.exit(0);
            }
            FileWriter fr = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fr);
            writeglobals(bw);
            writeStart(bw);
            writeFunctions(bw);
            bw.close();
            fr.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void writeglobals(BufferedWriter bw){
        try {
            bw.write(".globals:\n");
            ArrayList<Variable> globals = startcode.getStartCodeTable().variables;
            for (int i = 0; i < globals.size(); i++) {
                String output = String.format("%-4d static: \"%s\"\n",i,globals.get(i).getValue());
                bw.write(output);
            }
            bw.write('\n');
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void writeStart(BufferedWriter bw){
        try {
            ArrayList<Order> orders = startcode.getStartCodeTable().orders;
            bw.write(".start:\n");
            for(int i=0;i<orders.size();i++){
                Order order = orders.get(i);
                String output="";
                if(order.opers.size()==0)
                    output = String.format("%-4d %s \n",i,order.getOpcode());
                else
                    output = String.format("%-4d %s %-3d\n",i,order.getOpcode(),order.opers.get(order.opers.size()-1));
                bw.write(output);
            }
            bw.write('\n');
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void writeFunctions(BufferedWriter bw){
        try {
            Functionarrary functionTable = Functionarrary.getFunctionTable();
            ArrayList<Function> funcs = functionTable.functions;
            for(int i=1;i<funcs.size();i++){
                String this_func="";
                this_func = String.format("%-4d .functions:%s\n",i,funcs.get(i).name);
                bw.write(this_func);
                Function func = funcs.get(i);
                for(int j=0;j<func.orders.size();j++){
                    Order order = func.orders.get(j);
                    String output="";
                    if(order.opers.size()==0)
                        output = String.format("%-4d %s \n",j,order.getOpcode());
                    else
                        output = String.format("%-4d %s(%d)\n",j,order.getOpcode(),order.opers.get(order.opers.size()-1));
                    bw.write(output);
                }
                bw.write('\n');
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
