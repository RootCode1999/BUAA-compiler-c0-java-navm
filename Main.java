import java.io.FileReader;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String filename = args[0];
        String outfile = args[2];
        // String filename = "src/test.txt";
        // String outfile = "src/test.o0";
        // File directory = new File(filepath);
        // String filename = directory.getAbsolutePath();
        FileReader fp;
        Tokenizer tokenizer;
        try {
            File f = new File(filename);
            if (f.isDirectory()) {
                System.out.println("This is a directory, please input a file?");
                System.exit(0);
            } else if (!f.exists()) {
                System.out.println("File does not exist!");
                System.exit(0);
            }
            fp = new FileReader(filename);
            tokenizer = new Tokenizer(filename);
            ArrayList<Optional<Token>> tokens = tokenizer.get_all_Tokens();
            ArrayList<Token> token = new ArrayList<Token>();
            for (int i = 0; i < tokens.size(); i++) {
                token.add(tokens.get(i).get());
            }
            Analyser analyser = new Analyser(token);
            Ast c0Program = analyser.program_analyse();
            programAst c0ProgramAst = (programAst) c0Program;
            String res = ((programAst) c0Program).generate(0);
            // Sgenerator middle_o0 = new Sgenerator("src/s0.txt");
            // middle_o0.generate();
            Ogenerator write_o0 = new Ogenerator(outfile);
            write_o0.generate();
            System.out.print("hello");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}