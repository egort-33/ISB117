import java.util.LinkedList;

public class Blank_begin_end_remover {
    private static LinkedList<String> ScannedList;
    private static LinkedList<String> ClearedList;
    private static Boolean cleared = false;
    private  static Boolean useful_block = false;
    private static int loop_type = 0; // 0 - for, 1 - while
    public static String remover(String input_str)
    {
        ScannedList = new LinkedList<>();
        ClearedList = new LinkedList<>();
        loop_type = 0;
cleared = false;
        useful_block = false;
        String str = new String() ;
        String splitter = input_str;


        String[] Operators;

        Operators = splitter.split(" ");


        for (String i : Operators
        ) {
            if ((((String) (i)).length() > 0) && (i != "\r") && (i != ""))
                ScannedList.addLast(i);
        }


        while (cleared == false) {
            cleared = true;

            while (!ScannedList.isEmpty()) {
                if (ScannedList.getFirst() .contains("begin") )
                    clearsp();


                else if ((ScannedList.getFirst() .contains("end"))) {
                    ClearedList.addLast(ScannedList.getFirst());

                    ScannedList.removeFirst();
                } else {
                    ClearedList.addLast(ScannedList.getFirst());

                    ScannedList.removeFirst();
                }
            }
            if (cleared == false) {


                ScannedList.addAll(ClearedList);

                ClearedList.clear();
            }
        }
        ScannedList.addAll(ClearedList);

        ClearedList.clear();

        while (!ScannedList.isEmpty()) {
            if  ( ScannedList.getFirst() .contains( "for"))
                loop_type = 0;
            if  ( ScannedList.getFirst() .contains( "while"))
                loop_type = 1;

            if (ScannedList.getFirst() .contains( "begin")) {
                ClearedList.addLast(ScannedList.getFirst());

                ScannedList.removeFirst();
            /*    p.addLast(r.get(r.size()-1));
                r.remove((r.size()-1)); //ws for sure*/

                if (ScannedList.getFirst() .contains("end")) //reached end with just spaces
                {
                    //   r.pop_front();
                    ScannedList.removeFirst(); //;
                    ClearedList.removeLast();
                    // p.pop_back();
                } else {
                    ClearedList.addLast(ScannedList.getFirst());

                    ScannedList.removeFirst();
                }


            } else if (ScannedList.getFirst() .contains( "end")) {
                ClearedList.addLast(ScannedList.getFirst());

                ScannedList.removeFirst();
            } else {
                ClearedList.addLast(ScannedList.getFirst());

                ScannedList.removeFirst();
            }
        }
        str ="";
        for (int i = 0; i< ClearedList.size(); i++) {
            str+=(ClearedList.get(i)+" ");

        }



        return str;
    }
    private static void clearsp() {
        useful_block =false;

        ClearedList.addLast(ScannedList.getFirst());

        ScannedList.removeFirst();


        if (ScannedList.getFirst() .contains( "begin"))
            clearsp();


        else if ((ScannedList.getFirst() .contains( "end")) &&(useful_block ==false))//reached end with just spaces
        {
            ScannedList.removeFirst();
            for (int i = 0; i< ClearedList.size(); i++)
                if  ( (ClearedList.get(i)) .contains( "for"))
                    loop_type = 0;
           else if  ( (ClearedList.get(i)) .contains( "while"))
                loop_type = 1;

if (loop_type == 0) {
if (!ClearedList.isEmpty())
    while (!(ClearedList.getLast().contains("for")))  {


        ClearedList.removeLast();
    }

}
else if (loop_type == 1) {
    if (!ClearedList.isEmpty())
    while (!(ClearedList.getLast().contains("while"))) {


        ClearedList.removeLast();
    }
}
            ClearedList.removeLast();



            cleared = false;
        } else if (cleared == true) {
            ClearedList.addLast(ScannedList.getFirst());
            if (!(ScannedList.getFirst().equals("\n")))
                useful_block = true;
            ScannedList.removeFirst();
        }
    }
}
