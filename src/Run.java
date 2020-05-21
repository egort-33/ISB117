import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public final class Run {
    public static String filepath ;
    public static String filename ;
    public static void main(String[] args) throws IOException {

        String str;
       // "src/example/1.pas";
        Scanner finput = new Scanner(System.in);
        System.out.println("Path to /example folder:");

        filepath = finput.nextLine();


        System.out.println(filepath+"/example/"+".Input filename:");
        filename = finput.nextLine();
        finput.close();
String finalpath = filepath+"/example/"+filename;
        File file = new File(finalpath);

        Scanner in = new Scanner(file);

        str = Comments_remover.remover(in);
        in.close();

        Files.write(Paths.get(finalpath), str.getBytes());

        new StringBuilder();
        StringBuilder code_optimizer_str;
        Scanner in2 = new Scanner(file);
        code_optimizer_str = new StringBuilder();
        while (in2.hasNext())
            code_optimizer_str.append(in2.nextLine()).append("\n ");
        in2.close();


        int levellength = 0;

        System.out.println("Removing empty blocks...");
        while (code_optimizer_str.length() != levellength) {


            System.out.print("*");


            str = Blank_begin_end_remover.remover(code_optimizer_str.toString());
            code_optimizer_str = new StringBuilder(str);


            levellength = code_optimizer_str.length();


            str = Blank_repeat_until_remover.remover(code_optimizer_str.toString());
            code_optimizer_str = new StringBuilder(str);


        }
        System.out.println("\nFinished clearing...");
        Files.write(Paths.get(finalpath), code_optimizer_str.toString().getBytes());


        ArrayList<Token> Tokens_list = Token_reader.read(new File(finalpath));


        Parser.set_Tokens_list_Iterator(Tokens_list);

        Byte[] instructions = Parser.parse();
        Operations_simulator.set_Instructions(instructions);


        Operations_simulator.simulate();


    }


}
