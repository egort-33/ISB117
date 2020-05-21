import java.util.LinkedList;

public class Blank_repeat_until_remover {
    private static LinkedList<String> ScannedList;
    private static LinkedList<String> ClearedList;
    private static Boolean cleared = false;
    private static Boolean useful_block = false;
    public static String remover(String input_str)
    {
         ScannedList = new LinkedList<>();
        ClearedList = new LinkedList<>();
        cleared = false;
        useful_block = false;
        String str = "";
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
                if (ScannedList.getFirst() .contains("repeat") )
                    clearsp();


                else if ((ScannedList.getFirst() .contains("until"))) {
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


            if (ScannedList.getFirst() .contains( "repeat")) {
                ClearedList.addLast(ScannedList.getFirst());

                ScannedList.removeFirst();


                if (ScannedList.getFirst() .contains("until")) //reached until with just spaces
                {

                    ScannedList.removeFirst();
                    ClearedList.removeLast();

                } else {
                    ClearedList.addLast(ScannedList.getFirst());

                    ScannedList.removeFirst();
                }


            } else if (ScannedList.getFirst() .contains( "until")) {
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
        if (ScannedList.getFirst() .contains( "repeat"))
            clearsp();
        else if ((ScannedList.getFirst() .contains( "until")) &&(useful_block ==false))//reached until with just spaces
        {
            ScannedList.removeFirst();
            while (!(ScannedList.getFirst() .contains( ";")))
                ScannedList.removeFirst();
            ScannedList.removeFirst();
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
