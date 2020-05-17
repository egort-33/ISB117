import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public final class Run {

    public static void main(String[] args) throws IOException {

        String str = "";
        String filename = "src/example/1.pas";
        File file = new File(filename);
        Scanner in = new Scanner(file);

        str = Comments_remover.remover(in);
        in.close();

        Files.write(Paths.get(filename),str.getBytes());

String code_optimizer_str = "";
        Scanner in2 = new Scanner(file);
        code_optimizer_str = "";
        while (in2.hasNext())
            code_optimizer_str += in2.nextLine() + "\n ";
        in2.close();

        System.out.print( code_optimizer_str);

int levellength = 0;

System.out.println("Removing empty blocks...");
while (code_optimizer_str.length()!=levellength) {


System.out.print("*");


    str = Blank_begin_end_remover.remover(code_optimizer_str);
    code_optimizer_str = str;



    levellength = code_optimizer_str.length();





   str = Blank_repeat_until_remover.remover(code_optimizer_str);
    code_optimizer_str = str;




}
System.out.println("\nFinished clearing...");
        Files.write(Paths.get(filename),code_optimizer_str.getBytes());









        ArrayList<Token> Tokens_list = Token_reader.read(new File(filename));


        Parser.set_Tokens_list_Iterator(Tokens_list);

        Byte[] instructions = Parser.parse();
        Operations_simulator.set_Instructions(instructions);


        Operations_simulator.simulate();


    }


}
