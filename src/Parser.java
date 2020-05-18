import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public final class Parser {
    enum TYPE {
        INT, REAL, BOOL, CHAR, STRING, PROCEDURE, LABEL, ARRAY     // integer, real, boolean, char, string, procedure, label, array
    }

    private static int data_pointer = 0; // Указатель на область памяти для переменных

    private static final HashMap<String, TYPE> HASH_MAP_types;
    static {
        HASH_MAP_types = new HashMap<>();
        HASH_MAP_types.put("integer", TYPE.INT);
        HASH_MAP_types.put("real", TYPE.REAL);
        HASH_MAP_types.put("boolean", TYPE.BOOL);
        HASH_MAP_types.put("char", TYPE.CHAR);
        HASH_MAP_types.put("string", TYPE.STRING);
        HASH_MAP_types.put("array", TYPE.ARRAY);

    }

    enum Operations_code {
        PUSHI, PUSH, POP,PUSHF,
        JMP, JFALSE, JTRUE,
        CVR, CVI,
        DUP, XCHG, REMOVE,
        ADD, SUB, MULT, DIV, NEG,
        OR, AND,
        FADD, FSUB, FMULT, FDIV, FNEG,
        EQL, NEQL, GEQ, LEQ, GTR, LSS,
        FGTR, FLSS,
        HALT,
        PRINT_INT, PRINT_CHAR, PRINT_BOOL, PRINT_REAL, PRINT_NEWLINE,
        GET, PUT
    }

    private static final int ADDRESS_SIZE = 4;

    private static Token Token_current;
    private static Iterator<Token> Token_iterator;

    private static final int INSTRUCTION_SIZE = 1000;

    private static Byte[] byteArray = new Byte[INSTRUCTION_SIZE];
    private static int ip = 0;

    public static Byte[] parse() {
        getToken(); // Получить изначальный токен

        tokens_match("KW~PROGRAM");
        tokens_match("KW~IDENTIFIER");
        tokens_match("KW~SEMI_COLON");

        program();

        return byteArray;
    }

    
    public static void program() {
        declarations();
        begin();
    }

    
    public static void declarations() {
        while (true) {
            switch (Token_current.get_Token_Type()) {
                case "KW~VAR":
                    var_init();
                    break;
                case "KW~PROCEDURE":
                    procedure_init();
                    break;
                case "KW~LABEL":
                    label_init();
                    break;
                case "KW~BEGIN":
                    return;
            }
        }
    }


    private static void label_init() {
        while(true) {
            if ("KW~LABEL".equals(Token_current.get_Token_Type())) {
                tokens_match("KW~LABEL");
            } else {

                break;
            }

            // Хранение ссылок (label) в спике
            ArrayList<Token> Array_labels = new ArrayList<>();

            while ("KW~IDENTIFIER".equals(Token_current.get_Token_Type())) {
                Token_current.set_Token_Type("KW~A_LABEL");
                Array_labels.add(Token_current);

                tokens_match("KW~A_LABEL");

                if ("KW~COMMA".equals(Token_current.get_Token_Type())) {
                    tokens_match("KW~COMMA");
                }
            }

            // Вставка всех ссылок в таблицу символов
            for (Token label : Array_labels) {


                Symbol symbol = new Symbol(label.get_Token_Val(),
                        "KW~A_LABEL",
                        TYPE.LABEL,
                        0);

                if (Table_symbols.find_and_get(label.get_Token_Val()) == null) {
                    Table_symbols.insert(symbol);
                }
            }

            tokens_match("KW~SEMI_COLON");
        }
    }


    private static void procedure_init() {

        if (Token_current.get_Token_Type().equals("KW~PROCEDURE")) {
            tokens_match("KW~PROCEDURE");
            Token_current.set_Token_Type("KW~A_PROC");

            String procedure_Name = Token_current.get_Token_Val();

            tokens_match("KW~A_PROC");
            tokens_match("KW~SEMI_COLON");

            // Генерация пустого пространства для перемещения через тело программы
            generate_Operation_Code(Operations_code.JMP);
            int body_jump_hole = ip;
            generate_Address(0);

            Symbol symbol = new Symbol(procedure_Name,
                    "KW~A_PROC",
                    TYPE.PROCEDURE,
                    ip);

            // тело программы
            tokens_match("KW~BEGIN");
            statements();
            tokens_match("KW~END");
            tokens_match("KW~SEMI_COLON");

            // пустое пространство для возвращения процедуры
            generate_Operation_Code(Operations_code.JMP);
            symbol.setAddress_return(ip);
            generate_Address(0);

            if (Table_symbols.find_and_get(procedure_Name) == null) {
                Table_symbols.insert(symbol);
            }

            // заполнение пустого пространства для перемещения через тело программы
            int save = ip;

            ip = body_jump_hole;
            generate_Address(save);
            ip = save;
        }
    }



    public static void var_init() {
        while(true) {
            if ("KW~VAR".equals(Token_current.get_Token_Type())) {
                tokens_match("KW~VAR");
            } else {

                break;
            }

            // Хранение переменных в списке
            ArrayList<Token> var_Array = new ArrayList<>();

            while ("KW~IDENTIFIER".equals(Token_current.get_Token_Type())) {
                Token_current.set_Token_Type("KW~A_VAR");
                var_Array.add(Token_current);

                tokens_match("KW~A_VAR");

                if ("KW~COMMA".equals(Token_current.get_Token_Type())) {
                    tokens_match("KW~COMMA");
                }
            }

            tokens_match("KW~COLON");
            String Type_data = Token_current.get_Token_Type();
            tokens_match(Type_data);

            // Добавление соответствующего типа данных для каждого идентификатора и вставка  его в таблицу символов
            for (Token var : var_Array) {

                Symbol symbol = new Symbol(var.get_Token_Val(),
                        "KW~A_VAR",
                        HASH_MAP_types.get(Type_data.toLowerCase().substring(3)),
                        data_pointer);

                data_pointer += 4;


                if (Table_symbols.find_and_get(var.get_Token_Val()) == null) {
                    Table_symbols.insert(symbol);
                }
            }

            if (Type_data.equals("KW~ARRAY")){
                array_init(var_Array);
            }

            tokens_match("KW~SEMI_COLON");

        }
    }



    private static void array_init(ArrayList<Token> var_Array) {
        tokens_match("KW~OPEN_SQUARE_BRACKET");
        String v1 = Token_current.get_Token_Val();
        TYPE index_type1 = get_Litera_Type(Token_current.get_Token_Type());
        tokens_match(Token_current.get_Token_Type());

        tokens_match("KW~RANGE");

        String v2 = Token_current.get_Token_Val();
        TYPE index_type2 = get_Litera_Type(Token_current.get_Token_Type());
        tokens_match(Token_current.get_Token_Type());
        tokens_match("KW~CLOSE_SQUARE_BRACKET");
        tokens_match("KW~OF");

        String val_Type = Token_current.get_Token_Type();
        tokens_match(val_Type);

        if (index_type1 != index_type2){
            throw new Error(String.format("Array index LHS type (%s) is not equal to RHS type: (%s)", index_type1, index_type2));
        } else {

            assert index_type1 != null;
            switch (index_type1) {
                case INT:
                    int i1 = Integer.valueOf(v1);
                    int i2 = Integer.valueOf(v2);
                    if (i1 > i2){
                        throw new Error(String.format("Array range is invalid: %d..%d", i1, i2));
                    }

                    Symbol first_Integer_Array = Table_symbols.find_and_get(var_Array.get(0).get_Token_Val());
                    if (first_Integer_Array != null) {
                        data_pointer = first_Integer_Array.getAddress();
                    }

                    for (Token var: var_Array) {
                        Symbol symbol = Table_symbols.find_and_get(var.get_Token_Val());
                        if (symbol != null){

                            int elementSize = 4;
                            int size = elementSize*(i2 - i1 + 1);

                            symbol.set_Address(data_pointer);
                            symbol.set_Low(i1);
                            symbol.set_High(i2);
                            symbol.set_Token_Type("KW~AN_ARRAY");
                            symbol.set_Index_Type(TYPE.INT);
                            symbol.set_Value_Type(HASH_MAP_types.get(val_Type.toLowerCase().substring(3)));

                            data_pointer += size;
                        }
                    }

                    break;
                case CHAR:
                    char c1 = v1.toCharArray()[0];
                    char c2 = v2.toCharArray()[0];
                    if (c1 > c2){
                        throw new Error(String.format("Array range is invalid: %c..%c", c1, c2));
                    }

                    Symbol first_Char_Array = Table_symbols.find_and_get(var_Array.get(0).get_Token_Val());
                    if (first_Char_Array != null) {
                        data_pointer = first_Char_Array.getAddress();
                    }

                    for (Token var: var_Array) {
                        Symbol symbol = Table_symbols.find_and_get(var.get_Token_Val());
                        if (symbol != null){
                            int size = c2 - c1 + 1;

                            symbol.set_Address(data_pointer);
                            symbol.set_Low(c1);
                            symbol.set_High(c2);
                            symbol.set_Token_Type("KW~AN_ARRAY");
                            symbol.set_Index_Type(TYPE.CHAR);
                            symbol.set_Value_Type(HASH_MAP_types.get(val_Type.toLowerCase().substring(3)));

                            data_pointer += size;
                        }
                    }

                    break;
                case REAL:
                    throw new Error("Array index type: real is invalid");
            }

        }

    }


    public static void begin(){
        tokens_match("KW~BEGIN");
        statements();
        tokens_match("KW~END");
        tokens_match("KW~DOT");
        tokens_match("KW~EOF");
        generate_Operation_Code(Operations_code.HALT);
    }


    public static void statements(){
        while(!Token_current.get_Token_Type().equals("KW~END")) {
            switch (Token_current.get_Token_Type()) {
                case "KW~CASE":
                    Statement_case();
                    break;
                case "KW~GOTO":
                    Statement_goto();
                    break;
                case "KW~WHILE":
                    Statement_while();
                    break;
                case "KW~REPEAT":
                    Statement_repeat();
                    break;
                case "KW~IF":
                    Statement_if();
                    break;
                case "KW~FOR":
                    Statement_for();
                    break;
                case "KW~WRITELN":
                    Statement_writeln();
                    break;
                case "KW~IDENTIFIER":
                    Symbol symbol = Table_symbols.find_and_get(Token_current.get_Token_Val());
                    if (symbol != null) {
                        // Назначение типа токена как переменная процедура или ссылка
                        Token_current.set_Token_Type(symbol.getToken_Type());
                    }
                    break;
                case "KW~A_VAR":
                    Statement_assign();
                    break;
                case "KW~A_PROC":
                    Statement_procedure();
                    break;
                case "KW~A_LABEL":
                    Statement_label();
                    break;
                case "KW~AN_ARRAY":
                    Statement_arrayAssignment();
                    break;
                case "KW~SEMI_COLON":
                    tokens_match("KW~SEMI_COLON");
                    break;
                default:
                    return;
            }
        }

    }

    private static void Statement_label() {
        Symbol symbol = Table_symbols.find_and_get(Token_current.get_Token_Val());
        tokens_match("KW~A_LABEL");
        tokens_match("KW~COLON");
        if (symbol != null) {
            int hole = symbol.getAddress();
            int save = ip;

            // Заполнение пустого пространства под goto
            ip = hole;
            generate_Address(save);

            ip = save;

            statements();
        }
    }

    private static void Statement_procedure() {
        Symbol symbol = Table_symbols.find_and_get(Token_current.get_Token_Val());
        if (symbol != null) {
            int address = symbol.getAddress();
            tokens_match("KW~A_PROC");
            tokens_match("KW~SEMI_COLON");
            // Вызов процедуры
            generate_Operation_Code(Operations_code.JMP);
            generate_Address(address);

            int restore = ip;

            // Заполнение пустого пространства под возврат и восстановление указателя
            ip = symbol.getAddress_return();
            generate_Address(restore);
            ip = restore;
        }
    }

    private static void Statement_goto() {
        tokens_match("KW~GOTO");
        Symbol symbol = Table_symbols.find_and_get(Token_current.get_Token_Val());
        Token_current.set_Token_Type("KW~A_LABEL");
        tokens_match("KW~A_LABEL");
        generate_Operation_Code(Operations_code.JMP);
        int hole = ip;
        generate_Address(0);

        // Пустое пространство для перемещения
        if (symbol != null){
            symbol.set_Address(hole);
        }

        tokens_match("KW~SEMI_COLON");

    }


    // for <имя переменной> := <начало> to <конец> do <действия>
    private static void Statement_for() {
        tokens_match("KW~FOR");

        String varName = Token_current.get_Token_Val();
        Token_current.set_Token_Type("KW~A_VAR");
        Statement_assign();

        int target = ip;


        Symbol symbol = Table_symbols.find_and_get(varName);
        if (symbol != null) {
            int address = symbol.getAddress();
            tokens_match("KW~TO");

            // Генерация кодов операций для x <= <верхняя граница>
            generate_Operation_Code(Operations_code.PUSH);
            generate_Address(address);
            generate_Operation_Code(Operations_code.PUSHI);
            generate_Address(Integer.valueOf(Token_current.get_Token_Val()));

            generate_Operation_Code(Operations_code.LEQ);
            tokens_match("KW~INTLIT");

            tokens_match("KW~DO");

            generate_Operation_Code(Operations_code.JFALSE);
            int hole = ip;
            generate_Address(0);

            tokens_match("KW~BEGIN");
            statements();
            tokens_match("KW~END");
            tokens_match("KW~SEMI_COLON");

            // Генерация кодов операций для увеличения x в цикле
            generate_Operation_Code(Operations_code.PUSH);
            generate_Address(address);
            generate_Operation_Code(Operations_code.PUSHI);
            generate_Address(1);
            generate_Operation_Code(Operations_code.ADD);

            generate_Operation_Code(Operations_code.POP);
            generate_Address(address);


            generate_Operation_Code(Operations_code.JMP);
            generate_Address(target);

            int save = ip;
            ip = hole;
            generate_Address(save);
            ip = save;
        }
    }

    // repeat <действия> until <условие>
    private static void Statement_repeat() {
        tokens_match("KW~REPEAT");
        int target = ip;
        statements();
        tokens_match("KW~UNTIL");
        Condition_type();
        generate_Operation_Code(Operations_code.JFALSE);
        generate_Address(target);
    }


    // while <условие> do <действия>
    private static void Statement_while() {
        tokens_match("KW~WHILE");
        int target = ip;
        Condition_type();
        tokens_match("KW~DO");

        generate_Operation_Code(Operations_code.JFALSE);
        int hole = ip;
        generate_Address(0);

        tokens_match("KW~BEGIN");
        statements();
        tokens_match("KW~END");
        tokens_match("KW~SEMI_COLON");


        generate_Operation_Code(Operations_code.JMP);
        generate_Address(target);

        int save = ip;
        ip = hole;
        generate_Address(save);
        ip = save;

    }

    // if <условие> then <действия>
    // if <условие> then <действия> else <действия в ином случае>
    public static void Statement_if(){
        tokens_match("KW~IF");
        Condition_type();
        tokens_match("KW~THEN");
        generate_Operation_Code(Operations_code.JFALSE);
        int hole1 = ip;
        generate_Address(0);
        statements();

        if(Token_current.get_Token_Type().equals("KW~ELSE")) {
            generate_Operation_Code(Operations_code.JMP);
            int hole2 = ip;
            generate_Address(0);
            int save = ip;
            ip = hole1;
            generate_Address(save);
            ip = save;
            hole1 = hole2;
            statements();
            tokens_match("KW~ELSE");
            statements();
        }

        int save = ip;
        ip = hole1;
        generate_Address(save);
        ip = save;
    }

  //case <выражение с некоторым значением> of (как правило набор из нескольких пар) <значение> : <действие>
    public static void Statement_case() {
        tokens_match("KW~CASE");
        tokens_match("KW~OPEN_PARENTHESIS");
        Token eToken = Token_current;

        TYPE t1 = Expression_type();

        if (t1 == TYPE.REAL) {
            throw new Error("Invalid type of real for case E");
        }

        tokens_match("KW~CLOSE_PARENTHESIS");
        tokens_match("KW~OF");

        ArrayList<Integer> labelsArrayList = new ArrayList<>();

        while(Token_current.get_Token_Type().equals("KW~INTLIT") ||
                Token_current.get_Token_Type().equals("KW~CHARLIT") ||
                Token_current.get_Token_Type().equals("KW~BOOLLIT")) {

            TYPE t2 = Expression_type();
            operation_selector("KW~EQUAL", t1, t2);
            tokens_match("KW~COLON");

            // Пустое пространство выделяется под JFALSE к следующей case ссылке когда операция сравнения (eql) возвращает false
            generate_Operation_Code(Operations_code.JFALSE);
            int hole = ip;
            generate_Address(0);
            statements();

            generate_Operation_Code(Operations_code.JMP);
            labelsArrayList.add(ip);
            generate_Address(0);

            // Заполнение пустого пространства JFALSE
            int save = ip;
            ip = hole;
            generate_Address(save);

            ip = save;

            // Вставка в стек токена для подготовки к eql в следующей case ссылке

            if (!Token_current.get_Token_Val().equals("KW~END")){
                Symbol symbol = Table_symbols.find_and_get(eToken.get_Token_Val());
                if (symbol != null) {
                    generate_Operation_Code(Operations_code.PUSH);
                    generate_Address(symbol.getAddress());
                }
            }
        }

        tokens_match("KW~END");
        tokens_match("KW~SEMI_COLON");

        int save = ip;

        // Заполнение всех пустых пространств выделенных под ссылки для JMP
        for (Integer labelHole: labelsArrayList) {
            ip = labelHole;
            generate_Address(save);
        }

        ip = save;
    }

    //Вывод на экран
    public static void Statement_writeln(){
        tokens_match("KW~WRITELN");
        tokens_match("KW~OPEN_PARENTHESIS");

        while (true) {
            Symbol symbol =  Table_symbols.find_and_get(Token_current.get_Token_Val());
            TYPE t;

            if (symbol != null) {
                if (symbol.getData_Type() == TYPE.ARRAY) {

                    Token_current.set_Token_Type("KW~AN_ARRAY");
                    access_array_type(symbol);

                    generate_Operation_Code(Operations_code.GET);

                    t = symbol.getValue_Type();

                } else {

                    Token_current.set_Token_Type("KW~A_VAR");

                    t = symbol.getData_Type();
                    generate_Operation_Code(Operations_code.PUSH);
                    generate_Address(symbol.getAddress());
                    tokens_match("KW~A_VAR");
                }
            } else {

                t = get_Litera_Type(Token_current.get_Token_Type());
                assert t != null;
                switch (t) {
                    case REAL:
                        generate_Operation_Code(Operations_code.PUSHF);
                        generate_Address(Float.valueOf(Token_current.get_Token_Val()));
                        break;
                    case INT:
                        generate_Operation_Code(Operations_code.PUSHI);
                        generate_Address(Integer.valueOf(Token_current.get_Token_Val()));
                        break;
                    case BOOL:
                        generate_Operation_Code(Operations_code.PUSHI);
                        if (Token_current.get_Token_Val().equals("true")) {
                            generate_Address(1);
                        } else {
                            generate_Address(0);
                        }
                        break;
                    case CHAR:
                        generate_Operation_Code(Operations_code.PUSHI);
                        generate_Address((int)(Token_current.get_Token_Val().charAt(0)));
                        break;
                }

                tokens_match(Token_current.get_Token_Type());
            }

            assert t != null;
            switch (t) {
                case INT:
                    generate_Operation_Code(Operations_code.PRINT_INT);
                    break;
                case CHAR:
                    generate_Operation_Code(Operations_code.PRINT_CHAR);
                    break;
                case REAL:
                    generate_Operation_Code(Operations_code.PRINT_REAL);
                    break;
                case BOOL:
                    generate_Operation_Code(Operations_code.PRINT_BOOL);
                    break;
                default:
                    throw new Error("Cannot write unknown type");

            }

            switch (Token_current.get_Token_Type()) {
                case "KW~COMMA":
                    tokens_match("KW~COMMA");
                    break;
                case "KW~CLOSE_PARENTHESIS":
                    tokens_match("KW~CLOSE_PARENTHESIS");
                    generate_Operation_Code(Operations_code.PRINT_NEWLINE);
                    return;
                default:
                    throw new Error(String.format("Current token type (%s) is neither KW~COMMA nor KW~CLOSE_PARENTHESIS", Token_current.get_Token_Type()));
            }

        }
    }

    public static void Statement_assign() {
        Symbol symbol = Table_symbols.find_and_get(Token_current.get_Token_Val());

        if (symbol != null) {
            TYPE lhsType = symbol.getData_Type();
            int lhsAddress = symbol.getAddress();

            tokens_match("KW~A_VAR");

            tokens_match("KW~ASSIGNMENT");

            TYPE rhsType = Expression_type();
            if (lhsType == rhsType) {
                generate_Operation_Code(Operations_code.POP);
                generate_Address(lhsAddress);
            } else {
                throw new Error(String.format("LHS type (%s) is not equal to RHS type: (%s)", lhsType, rhsType));
            }
        }


    }


    private static void Statement_arrayAssignment() {
        Symbol symbol = Table_symbols.find_and_get(Token_current.get_Token_Val());
        if (symbol != null) {

            access_array_type(symbol);

            tokens_match("KW~ASSIGNMENT");


            TYPE rhsType = Expression_type();

            if (symbol.getValue_Type() == rhsType) {
                generate_Operation_Code(Operations_code.PUT);
            }

        }

    }

    private static void access_array_type(Symbol symbol) {
        tokens_match("KW~AN_ARRAY");
        tokens_match("KW~OPEN_SQUARE_BRACKET");
        TYPE t;


        Symbol varSymbol = Table_symbols.find_and_get(Token_current.get_Token_Val());
        if (varSymbol != null) {
            t = varSymbol.getData_Type();


            if (t != symbol.getIndex_Type()) {
                throw new Error(String.format("Incompatible index type: (%s, %s)", t, symbol.getIndex_Type()));
            }

            Token_current.set_Token_Type("KW~A_VAR");
            generate_Operation_Code(Operations_code.PUSH);
            generate_Address(varSymbol.getAddress());
            tokens_match("KW~A_VAR");

            tokens_match("KW~CLOSE_SQUARE_BRACKET");

            generate_Operation_Code(Operations_code.PUSHI);

            switch (t) {
                case INT:

                    int i1 = (int) symbol.get_Low();
                    int i2 = (int) symbol.get_High();

                    generate_Address(i1);
                    generate_Operation_Code(Operations_code.XCHG);
                    generate_Operation_Code(Operations_code.SUB);

                    // push element size
                    generate_Operation_Code(Operations_code.PUSHI);
                    generate_Address(4);

                    generate_Operation_Code(Operations_code.MULT);

                    generate_Operation_Code(Operations_code.PUSHI);
                    generate_Address(symbol.getAddress());

                    generate_Operation_Code(Operations_code.ADD);

                    break;
                case CHAR:
                    char c1 = (char) symbol.get_Low();
                    char c2 = (char) symbol.get_High();

                    generate_Address(c1);
                    generate_Operation_Code(Operations_code.XCHG);
                    generate_Operation_Code(Operations_code.SUB);

                    generate_Operation_Code(Operations_code.PUSHI);
                    generate_Address(symbol.getAddress());

                    generate_Operation_Code(Operations_code.ADD);

                    break;
            }
        } else {


            String index = Token_current.get_Token_Val();
            t = Expression_type();

            if (t != symbol.getIndex_Type()) {
                throw new Error(String.format("Incompatible index type: (%s, %s)", t, symbol.getIndex_Type()));
            }

            tokens_match("KW~CLOSE_SQUARE_BRACKET");

            generate_Operation_Code(Operations_code.PUSHI);

            switch (t) {
                case INT:

                    int i1 = (int) symbol.get_Low();
                    int i2 = (int) symbol.get_High();


                    if (Integer.valueOf(index) < i1 || Integer.valueOf(index) > i2) {
                        throw new Error(String.format("Index %d is not within range %d to %d",
                                Integer.valueOf(index), i1, i2));
                    }

                    generate_Address(i1);
                    generate_Operation_Code(Operations_code.XCHG);
                    generate_Operation_Code(Operations_code.SUB);


                    generate_Operation_Code(Operations_code.PUSHI);
                    generate_Address(4);

                    generate_Operation_Code(Operations_code.MULT);

                    generate_Operation_Code(Operations_code.PUSHI);
                    generate_Address(symbol.getAddress());

                    generate_Operation_Code(Operations_code.ADD);

                    break;
                case CHAR:
                    char c1 = (char) symbol.get_Low();
                    char c2 = (char) symbol.get_High();


                    if (index.toCharArray()[0] < c1 || index.toCharArray()[0] > c2) {
                        throw new Error(String.format("Index %c is not within range %c to %c",
                                index.toCharArray()[0], c1, c2));
                    }

                    generate_Address(c1);
                    generate_Operation_Code(Operations_code.XCHG);
                    generate_Operation_Code(Operations_code.SUB);

                    generate_Operation_Code(Operations_code.PUSHI);
                    generate_Address(symbol.getAddress());

                    generate_Operation_Code(Operations_code.ADD);

                    break;
            }

        }
    }


    public static TYPE Condition_type(){
        TYPE expr1 = Expression_type();
        while (Token_current.get_Token_Type().equals("KW~LESS_THAN") ||
                Token_current.get_Token_Type().equals("KW~GREATER_THAN") ||
                Token_current.get_Token_Type().equals("KW~LESS_THAN_EQUAL") ||
                Token_current.get_Token_Type().equals("KW~GREATER_THAN_EQUAL") ||
                Token_current.get_Token_Type().equals("KW~EQUAL") ||
                Token_current.get_Token_Type().equals("KW~NOT_EQUAL")) {
            String pred = Token_current.get_Token_Type();
            tokens_match(pred);
            TYPE expr2 = Terminal_type();

            expr1 = operation_selector(pred, expr1, expr2);
        }

        return expr1;
    }



    public static TYPE Expression_type(){
        TYPE term1 = Terminal_type();
        while (Token_current.get_Token_Type().equals("KW~PLUS") || Token_current.get_Token_Type().equals("KW~MINUS")) {
            String operator_tok = Token_current.get_Token_Type();
            tokens_match(operator_tok);
            TYPE term2 = Terminal_type();

            term1 = operation_selector(operator_tok, term1, term2);
        }

        return term1;
    }


    public static TYPE Terminal_type() {
        TYPE factor1 = Factor_type();
        while (Token_current.get_Token_Type().equals("KW~MULTIPLY") ||
                Token_current.get_Token_Type().equals("KW~DIVIDE") ||
                Token_current.get_Token_Type().equals("KW~DIV")) {
            String operator_tok = Token_current.get_Token_Type();
            tokens_match(operator_tok);
            TYPE factor2 = Factor_type();

            factor1 = operation_selector(operator_tok, factor1, factor2);
        }
        return factor1;
    }



    public static TYPE Factor_type() {
        switch (Token_current.get_Token_Type()) {
            case "KW~IDENTIFIER":
                Symbol symbol = Table_symbols.find_and_get(Token_current.get_Token_Val());
                if (symbol != null) {
                    if (symbol.getToken_Type().equals("KW~A_VAR")) {
                        // variable
                        Token_current.set_Token_Type("KW~A_VAR");

                        generate_Operation_Code(Operations_code.PUSH);
                        generate_Address(symbol.getAddress());

                        tokens_match("KW~A_VAR");
                        return symbol.getData_Type();
                    } else if (symbol.getToken_Type().equals("KW~AN_ARRAY")) {
                        Token_current.set_Token_Type("KW~AN_ARRAY");

                        access_array_type(symbol);
                        generate_Operation_Code(Operations_code.GET);

                        return symbol.getValue_Type();
                    }
                } else {
                    throw new Error(String.format("Symbol not found (%s)", Token_current.get_Token_Val()));
                }
            case "KW~INTLIT":
                generate_Operation_Code(Operations_code.PUSHI);
                generate_Address(Integer.valueOf(Token_current.get_Token_Val()));

                tokens_match("KW~INTLIT");
                return TYPE.INT;
            case "KW~FLOATLIT":
                generate_Operation_Code(Operations_code.PUSHF);
                generate_Address(Float.valueOf(Token_current.get_Token_Val()));

                tokens_match("KW~FLOATLIT");
                return TYPE.REAL;
            case "KW~BOOLLIT":
                generate_Operation_Code(Operations_code.PUSHI);
                generate_Address(Boolean.valueOf(Token_current.get_Token_Val()) ? 1 : 0);

                tokens_match("KW~BOOLLIT");
                return TYPE.BOOL;
            case "KW~CHARLIT":
                generate_Operation_Code(Operations_code.PUSHI);
                generate_Address(Token_current.get_Token_Val().charAt(0));

                tokens_match("KW~CHARLIT");
                return TYPE.CHAR;
            case "KW~STRLIT":
                for (char c: Token_current.get_Token_Type().toCharArray()) {
                    generate_Operation_Code(Operations_code.PUSHI);
                    generate_Address(c);
                }

                tokens_match("KW~STRLIT");
                return TYPE.STRING;
            case "KW~NOT":
                tokens_match("KW~NOT");
                return Factor_type();
            case "KW~OPEN_PARENTHESIS":
                tokens_match("KW~OPEN_PARENTHESIS");
                TYPE type_ = Expression_type();
                tokens_match("KW~CLOSE_PARENTHESIS");
                return type_;
            default:
                throw new Error("Unknown data type");
        }

    }


    public static TYPE operation_selector(String op, TYPE t1, TYPE t2){
        switch (op) {
            case "KW~PLUS":
                if (t1 == TYPE.INT && t2 == TYPE.INT) {
                    generate_Operation_Code(Operations_code.ADD);
                    return TYPE.INT;
                } else if (t1 == TYPE.INT && t2 == TYPE.REAL) {
                    generate_Operation_Code(Operations_code.XCHG);
                    generate_Operation_Code(Operations_code.CVR);
                    generate_Operation_Code(Operations_code.FADD);
                    return TYPE.REAL;
                } else if (t1 == TYPE.REAL && t2 == TYPE.INT) {
                    generate_Operation_Code(Operations_code.CVR);
                    generate_Operation_Code(Operations_code.FADD);
                    return TYPE.REAL;
                } else if (t1 == TYPE.REAL && t2 == TYPE.REAL) {
                    generate_Operation_Code(Operations_code.FADD);
                    return TYPE.REAL;
                }
            case "KW~MINUS":
                if (t1 == TYPE.INT && t2 == TYPE.INT) {
                    generate_Operation_Code(Operations_code.SUB);
                    return TYPE.INT;
                } else if (t1 == TYPE.INT && t2 == TYPE.REAL) {
                    generate_Operation_Code(Operations_code.XCHG);
                    generate_Operation_Code(Operations_code.CVR);
                    generate_Operation_Code(Operations_code.FSUB);
                    return TYPE.REAL;
                } else if (t1 == TYPE.REAL && t2 == TYPE.INT) {
                    generate_Operation_Code(Operations_code.CVR);
                    generate_Operation_Code(Operations_code.FSUB);
                    return TYPE.REAL;
                } else if (t1 == TYPE.REAL && t2 == TYPE.REAL) {
                    generate_Operation_Code(Operations_code.FSUB);
                    return TYPE.REAL;
                }
            case "KW~MULTIPLY":
                if (t1 == TYPE.INT && t2 == TYPE.INT) {
                    generate_Operation_Code(Operations_code.MULT);
                    return TYPE.INT;
                } else if (t1 == TYPE.INT && t2 == TYPE.REAL) {
                    generate_Operation_Code(Operations_code.XCHG);
                    generate_Operation_Code(Operations_code.CVR);
                    generate_Operation_Code(Operations_code.FMULT);
                    return TYPE.REAL;
                } else if (t1 == TYPE.REAL && t2 == TYPE.INT) {
                    generate_Operation_Code(Operations_code.CVR);
                    generate_Operation_Code(Operations_code.FMULT);
                    return TYPE.REAL;
                } else if (t1 == TYPE.REAL && t2 == TYPE.REAL) {
                    generate_Operation_Code(Operations_code.FMULT);
                    return TYPE.REAL;
                }
            case "KW~DIVIDE":
                if (t1 == TYPE.INT && t2 == TYPE.INT) {
                    generate_Operation_Code(Operations_code.CVR);
                    generate_Operation_Code(Operations_code.XCHG);
                    generate_Operation_Code(Operations_code.CVR);
                    generate_Operation_Code(Operations_code.XCHG);
                    generate_Operation_Code(Operations_code.FDIV);
                    return TYPE.REAL;
                } else if (t1 == TYPE.INT && t2 == TYPE.REAL) {
                    generate_Operation_Code(Operations_code.XCHG);
                    generate_Operation_Code(Operations_code.CVR);
                    generate_Operation_Code(Operations_code.FDIV);
                    return TYPE.REAL;
                } else if (t1 == TYPE.REAL && t2 == TYPE.INT) {
                    generate_Operation_Code(Operations_code.CVR);
                    generate_Operation_Code(Operations_code.FDIV);
                    return TYPE.REAL;
                } else if (t1 == TYPE.REAL && t2 == TYPE.REAL) {
                    generate_Operation_Code(Operations_code.FDIV);
                    return TYPE.REAL;
                }
            case "KW~DIV":
                if (t1 == TYPE.INT && t2 == TYPE.INT) {
                    generate_Operation_Code(Operations_code.DIV);
                    return TYPE.INT;
                }
            case "KW~LESS_THAN":
                return Boolean_selector(Operations_code.LSS, t1, t2);
            case "KW~GREATER_THAN":
                return Boolean_selector(Operations_code.GTR, t1, t2);
            case "KW~LESS_THAN_EQUAL":
                return Boolean_selector(Operations_code.LEQ, t1, t2);
            case "KW~GREATER_THAN_EQUAL":
                return Boolean_selector(Operations_code.GEQ, t1, t2);
            case "KW~EQUAL":
                return Boolean_selector(Operations_code.EQL, t1, t2);
            case "KW~NOT_EQUAL":
                return Boolean_selector(Operations_code.NEQL, t1, t2);
        }

        return null;
    }

    public static TYPE Boolean_selector(Operations_code pred, TYPE t1, TYPE t2) {
        if (t1 == t2) {
            generate_Operation_Code(pred);
            return TYPE.BOOL;
        } else if (t1 == TYPE.INT && t2 == TYPE.REAL) {
            generate_Operation_Code(Operations_code.XCHG);
            generate_Operation_Code(Operations_code.CVR);
            generate_Operation_Code(pred);
            return TYPE.BOOL;
        } else if (t1 == TYPE.REAL && t2 == TYPE.INT) {
            generate_Operation_Code(Operations_code.CVR);
            generate_Operation_Code(pred);
            return TYPE.BOOL;
        }

        return null;
    }
//Ниже перечислены операции заполняющие выделенные области памяти в байтах

    public static void generate_Operation_Code(Operations_code b){

        byteArray[ip++] = (byte)(b.ordinal());
    }

    public static void generate_Address(int a){

        byte[] bytes_int = ByteBuffer.allocate(ADDRESS_SIZE).putInt(a).array();

        for (byte b: bytes_int) {
            byteArray[ip++] = b;
        }
    }

    public static void generate_Address(float a){

        byte[] intBytes = ByteBuffer.allocate(ADDRESS_SIZE).putFloat(a).array();

        for (byte b: intBytes) {
            byteArray[ip++] = b;
        }
    }

    public static void getToken() {
        if (Token_iterator.hasNext()) {
            Token_current =  Token_iterator.next();
        }
    }

    public static void tokens_match(String tokenType) {
        if (!tokenType.equals(Token_current.get_Token_Type())) {
            throw new Error(String.format("Token type (%s) does not match current token type (%s)", tokenType, Token_current.get_Token_Type()));
        } else {

            getToken();
        }
    }

    public static TYPE get_Litera_Type(String tokenType) {
        switch (tokenType) {
            case "KW~INTLIT":
                return TYPE.INT;
            case "KW~FLOATLIT":
                return TYPE.REAL;
            case "KW~CHARLIT":
                return TYPE.CHAR;
            case "KW~BOOLLIT":
                return TYPE.BOOL;
            default:
                return null;
        }
    }

    public static void set_Tokens_list_Iterator(ArrayList<Token> tokenArrayList) {
        Token_iterator = tokenArrayList.iterator();
    }
}
