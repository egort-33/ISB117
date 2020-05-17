import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Comments_remover {
    public static String remover(Scanner in)
    {
        String str = "";
        String subs, subs_delete = "";
        while(in.hasNext())
        {
            subs = in.nextLine();
            if (subs.contains("//")) {
                if (subs.contains(" //"))
                    subs_delete = subs.substring(subs.indexOf(" //"));
                    else
                subs_delete = subs.substring(subs.indexOf("//"));
                subs = subs.replace(subs_delete, "");
            }
            if (subs.contains("{")) {
                while (in.hasNext()) {
                    subs += in.nextLine();
                    if (subs.contains("//")) {
                        if (subs.contains(" //"))
                        subs_delete = subs.substring(subs.indexOf(" //"));
else
                            subs_delete = subs.substring(subs.indexOf("//"));
                        subs = subs.replace(subs_delete, "");
                    }
                    if (subs.contains("}"))
                        break;
                }
                if (subs.contains(" {"))
                subs_delete = subs.substring(subs.indexOf(" {"));
                else
                    subs_delete = subs.substring(subs.indexOf("{"));
                if (subs_delete.contains("} "))
                subs_delete = subs_delete.substring(0, subs_delete.indexOf("} ") + 1);
                else
                    subs_delete = subs_delete.substring(0, subs_delete.indexOf("}") + 1);
                subs = subs.replace(subs_delete, "");
            }

            str +=  subs + "\r\n";
        }



        in.close();

        
        
        return str;
    }
}
