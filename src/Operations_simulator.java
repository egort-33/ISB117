import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Stack;

public class Operations_simulator {

    private static int ip = 0;
    public static String s = "";
    private static int data_pointer = 0;

    private static final Stack<Object> stack = new Stack<>();

    private static final Byte[] Array_data = new Byte[1000];

    private static Byte[] instructions;

    public static void simulate() {
        Parser.Operations_code oper_Code;

        do {
            oper_Code = get_Oper_Code();

            switch (oper_Code) { //Вызов соответствующей целевому языку операции по ее "коду"
                case PUSH:
                    push();
                    break;
                case PUSHI:
                    pushi();
                    break;
                case PUSHF:
                    pushf();
                    break;
                case POP:
                    pop();
                    break;
                case GET:
                    get();
                    break;
                case PUT:
                    put();
                    break;
                case CVR:
                    cvr();
                    break;
                case XCHG:
                    xchg();
                    break;
                case JMP:
                    jmp();
                    break;
                case PRINT_REAL:
                    printReal();
                    break;
                case PRINT_INT:
                    printInt();
                    break;
                case PRINT_BOOL:
                    printBool();
                    break;
                case PRINT_CHAR:
                    printChar();
                    break;
                case PRINT_NEWLINE:
                    System.out.println();
                    break;
                case HALT:
                    halt();
                    break;
                case EQL:
                    eql();
                    break;
                case NEQL:
                    neql();
                    break;
                case LSS:
                    less();
                    break;
                case LEQ:
                    lessEql();
                    break;
                case GTR:
                    greater();
                    break;
                case GEQ:
                    greaterEql();
                    break;
                case JFALSE:
                    jfalse();
                    break;
                case JTRUE:
                    jtrue();
                    break;
                case ADD:
                    add();
                    break;
                case FADD:
                    fadd();
                    break;
                case SUB:
                    sub();
                    break;
                case FSUB:
                    fsub();
                    break;
                case MULT:
                    mult();
                    break;
                case FMULT:
                    fmult();
                    break;
                case DIV:
                    div();
                    break;
                case FDIV:
                    fdiv();
                    break;
                default:
                    throw new Error(String.format("Unhandled case: %s", oper_Code));
            }

        }
        while (oper_Code != Parser.Operations_code.HALT);

    }

    private static void pushf() { //В стек компилятора добавляется число с плавающей точкой
        float val = get_Float_val();
        s += ("pushf " + val) + "\n";
        stack.push(val);
    }

    private static void get() { //Из стека извлекается указатель на некоторый элемент
        data_pointer = (int) stack.pop();
        s += ("get " + get_Data(data_pointer) + " : " + data_pointer) + "\n";
        stack.push(get_Data(data_pointer));
    }

    private static void put() { //В область памяти записывается последовательность байт, то есть некоторое значение
        Object val = stack.pop();
        data_pointer = (int) stack.pop();


        byte[] value_Bytes;
        if (val instanceof Integer) {
            value_Bytes = ByteBuffer.allocate(4).putInt((int) val).array();
        } else {
            value_Bytes = ByteBuffer.allocate(4).putFloat((float) val).array();
        }

        s += ("put (pointer:" + data_pointer + ") : (bytes: ");
        for (byte b : value_Bytes) {
            Array_data[data_pointer++] = b;
            s = s + b;
        }
        s += (") : " + val) + "\n";
    }

    private static void jtrue() { //Проверка истинности
        if (stack.pop().toString().equals("true")) {
            ip = get_Addr_Val();
            s += ("jtrue " + ip) + "\n";
        } else {
            get_Addr_Val();
            s += ("jtrue (false) ") + "\n";
        }
    }

    private static void jfalse() { //Проверка ложности
        if (stack.pop().toString().equals("false")) {
            ip = get_Addr_Val();
            s += ("jfalse " + ip) + "\n";
        } else {
            get_Addr_Val();
            s += ("jfalse (false) ") + "\n";
        }
    }

    private static void eql() { //Проверка равны ли элементы между собой
        Integer i2 = (Integer) stack.pop();
        Float val2 = (float) i2;

        Integer i1 = (Integer) stack.pop();
        Float val1 = (float) i1;

        stack.push(val1.equals(val2));

        s += ("eql " + val1 + " =? " + val2 + " is " + val1.equals(val2)) + "\n";
    }

    private static void neql() {//Проверка не равны ли элементы между собой
        Integer i2 = (Integer) stack.pop();
        Float val2 = (float) i2;

        Integer i1 = (Integer) stack.pop();
        Float val1 = (float) i1;

        stack.push(!val1.equals(val2));
        s += ("neql " + val1 + " !=? " + val2 + " is " + !val1.equals(val2)) + "\n";
    }

    private static void less() { //Проверка меньше ли первый элемент чем второй
        Integer i2 = (Integer) stack.pop();
        float val2 = (float) i2;

        Integer i1 = (Integer) stack.pop();
        float val1 = (float) i1;

        stack.push(val1 < val2);
        s += ("less " + val1 + " <? " + val2 + " is " + (val1 < val2)) + "\n";
    }

    private static void greater() { //Проверка больше ли первый элемент чем второй
        Integer i2 = (Integer) stack.pop();
        float val2 = (float) i2;

        Integer i1 = (Integer) stack.pop();
        float val1 = (float) i1;

        stack.push(val1 > val2);
        s += ("greater " + val1 + " >? " + val2 + " is " + (val1 > val2)) + "\n";
    }

    private static void lessEql() { //Проверка меньше/равен ли первый элемент второму
        Integer i2 = (Integer) stack.pop();
        float val2 = (float) i2;

        Integer i1 = (Integer) stack.pop();
        float val1 = (float) i1;

        stack.push(val1 <= val2);
        s += ("lessEql " + val1 + " <=? " + val2 + " is " + (val1 <= val2)) + "\n";
    }

    private static void greaterEql() { //Проверка больше/равен ли первый элемент второму
        Integer i2 = (Integer) stack.pop();
        float val2 = (float) i2;

        Integer i1 = (Integer) stack.pop();
        float val1 = (float) i1;

        stack.push(val1 >= val2);
        s += ("greaterEql " + val1 + " >=? " + val2 + " is " + (val1 >= val2)) + "\n";
    }

    private static void printReal() { //Вывод на экран числа с плавающей точкой

        Object val = stack.pop();

        if (val instanceof Integer) {
            byte[] valArray = ByteBuffer.allocate(4).putInt((int) val).array();
            s += ("printReal " + Arrays.toString(valArray) + " : " + " : " + ByteBuffer.wrap(valArray) + " : " + ByteBuffer.wrap(valArray).getFloat()) + "\n";
            System.out.print(ByteBuffer.wrap(valArray).getFloat());
        } else {
            s += (val) + "\n";
            System.out.print(val);
        }

    }

    private static void printBool() { //Вывод на экран булева значения
        int val = (int) stack.pop();
        if (val == 1) {
            s += ("printBool " + val + " (True)") + "\n";
            System.out.print("True");
        } else {
            s += ("printBool " + val + " (False)") + "\n";
            System.out.print("False");
        }
    }

    public static void printInt() { //Вывод на экран целочисленного значения

        int x = (Integer) stack.pop();
        s += ("printInt (From stack) " + x) + "\n";
        System.out.print(x);

    }

    public static void printChar() { //Вывод на экран символа

        int x = (Integer) stack.pop();
        s += ("printChar (From stack) " + Character.toChars(x)[0]) + "\n";
        System.out.print(Character.toChars(x)[0]);

    }

    public static void add() { //Операция сложения
        int val1 = (int) stack.pop();
        int val2 = (int) stack.pop();
        stack.push(val1 + val2);
        s += ("add " + val1 + " + " + val2) + "\n";
    }

    private static void fadd() { //Операция сложения чисел с плавающей точкой
        float val1 = (float) stack.pop();
        float val2 = (float) stack.pop();
        stack.push(val1 + val2);
        s += ("fadd " + val1 + " + " + val2) + "\n";
    }


    public static void sub() { //Операция вычитания
        int val1 = (int) stack.pop();
        int val2 = (int) stack.pop();
        stack.push(val1 - val2);
        s += ("sub " + val1 + " - " + val2) + "\n";
    }

    public static void fsub() { //Операция вычитания чисел с плавающей точкой
        float val1 = (float) stack.pop();
        float val2 = (float) stack.pop();
        stack.push(val1 - val2);
        s += ("fsub " + val1 + " - " + val2) + "\n";
    }

    public static void mult() {  //Операция умножения
        int val1 = (int) stack.pop();
        int val2 = (int) stack.pop();
        stack.push(val1 * val2);
        s += ("mult " + val1 + " * " + val2) + "\n";
    }

    public static void fmult() { //Операция умножения чисел с плавающей точкой
        float val1 = (float) stack.pop();
        float val2 = (float) stack.pop();
        stack.push(val1 * val2);
        s += ("fmult " + val1 + " * " + val2) + "\n";
    }

    public static void fdiv() { //Операция деления чисел с плавающей точкой
        float val2 = (float) stack.pop();
        float val1 = (float) stack.pop();

        stack.push(val1 / val2);
        s += ("fdiv " + val1 + " / " + val2) + "\n";
    }

    public static void div() { //Операция деления нацело
        int val2 = (int) stack.pop();
        int val1 = (int) stack.pop();
        stack.push(val1 / val2);
        s += ("div " + val1 + " / " + val2) + "\n";
    }

    public static void cvr() { //Извлечение значения с плавающей точкой из строкового представления элемента стека
        float val = Float.parseFloat(String.valueOf(stack.pop()));
        stack.push(val);
        s += ("cvr (from Stack) : " + val) + "\n";
    }

    public static void xchg() { //Обмен пары значений с верхушки стека
        Object val1 = stack.pop();
        Object val2 = stack.pop();
        stack.push(val1);
        stack.push(val2);
        s += ("xchg : pop " + val1 + ", pop " + val2 + ", push " + val1 + " , push " + val2) + "\n";
    }

    public static void pushi() { //Добавление элемента в стек
        int val = get_Addr_Val();
        stack.push(val);
        s += ("pushi :" + val) + "\n";
    }

    public static void push() { //Добавление элемента по указателю в стек
        data_pointer = get_Addr_Val();
        stack.push(get_Data(data_pointer));
        s += ("push : (pointer:" + data_pointer + ") : " + get_Data(data_pointer)) + "\n";
    }

    public static void pop() { //Извлечение элемента из стека по указателю
        Object val = stack.pop();
        data_pointer = get_Addr_Val();


        byte[] value_Bytes;
        if (val instanceof Integer) {
            value_Bytes = ByteBuffer.allocate(4).putInt((int) val).array();
        } else {
            value_Bytes = ByteBuffer.allocate(4).putFloat((float) val).array();
        }

        s += ("put (pointer:" + data_pointer + ") : (bytes :");
        for (byte b : value_Bytes) {
            Array_data[data_pointer++] = b;
            s += b;
        }

        s += (") : " + val) + "\n";
    }

    public static void jmp() {

        ip = get_Addr_Val();
        s += ("jmp : (address: " + ip) + ") " + "\n";
    }


    public static void halt() { //Выход из программы с кодом 0, компиляция выполнена
        s += ("halt : code 0") + "\n";
        System.out.print("\nProgram finished with exit code 0\n");
        try {
            Files.write(Paths.get("src/example/1-operations.txt"), s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static int get_Addr_Val() { //Получение значения из байтового представления в виде целочисленного значения
        byte[] valArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            valArray[i] = instructions[ip++];
        }

        return ByteBuffer.wrap(valArray).getInt();
    }

    public static float get_Float_val() { //Получение значения из байтового представления в виде числа с плавающей точкой
        byte[] valArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            valArray[i] = instructions[ip++];
        }

        return ByteBuffer.wrap(valArray).getFloat();
    }

    public static int get_Data(int dp) { //Получение значения по указателю
        byte[] valArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            valArray[i] = Array_data[dp++];
        }

        return ByteBuffer.wrap(valArray).getInt();
    }


    public static Parser.Operations_code get_Oper_Code() {
        return Parser.Operations_code.values()[instructions[ip++]];
    } //Получение непосредственно "кода" операции

    public static void set_Instructions(Byte[] instructions) {
        Operations_simulator.instructions = instructions;
    }

}