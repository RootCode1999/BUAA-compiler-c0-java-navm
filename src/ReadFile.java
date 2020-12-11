import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ReadFile {

    public static void main(String[] args) throws Exception{

        String path = "E:\\BUAA\\2020_2_software_semester\\LAB\\BUAACompiler\\c0-java\\src\\test.txt";

        FileInputStream fileInputStream = new FileInputStream(path);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

        String line = null;

        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

        fileInputStream.close();

    }
}