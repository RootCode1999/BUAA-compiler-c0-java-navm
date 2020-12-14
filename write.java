import java.io.*;
import java.nio.*;

public class write {
    startcode code_stratcode = startcode.getStartCodeTable();
    Functionarrary code_funcs = Functionarrary.getFunctionTable();

    public void write_binary(String outputFile) {
        try {
            File f = new File(outputFile);
            if (f.exists() && f.isFile())
                f.delete();
            if (f.isDirectory()) {
                System.out.println("this is a directory, are you sure you typed the correct output file?");
                System.exit(0);
            }
            f.createNewFile();
            DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile, true));

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
