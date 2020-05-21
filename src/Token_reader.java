import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public final class Token_reader {
    private static final HashMap<String, String> KEYWORDS_TOKEN;
    private static final HashMap<String, String> OPERATORS_TOKEN; //Токены операторов
    private static final HashMap<String, TYPE> CHAR_TYPE;
    private static String token_Name = "";
    private static boolean String_isRead = false;
    private static boolean Number_isRead = false;
    private static boolean isFloat = false;
    private static boolean sci_Notation = false;
    private static boolean Colon_isRead = false;
    private static boolean Bool_isRead = false;
    private static boolean Dot_isRead = false;
    private static final ArrayList<Token> Array_List_tokens = new ArrayList<>();

    static {
        KEYWORDS_TOKEN = new HashMap<>();
        String word;
        String filename;


        filename = Run.filepath+"/keywords.txt";

        try {

            Scanner sc = new Scanner(new File(filename));
            while(sc.hasNext()){
                word = sc.next();
                KEYWORDS_TOKEN.put(word, String.format("KW~%s", word.toUpperCase()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static {
        OPERATORS_TOKEN = new HashMap<>();
        OPERATORS_TOKEN.put("(", "KW~OPEN_PARENTHESIS");
        OPERATORS_TOKEN.put(")", "KW~CLOSE_PARENTHESIS");
        OPERATORS_TOKEN.put("[", "KW~OPEN_SQUARE_BRACKET");
        OPERATORS_TOKEN.put("]", "KW~CLOSE_SQUARE_BRACKET");
        OPERATORS_TOKEN.put(".", "KW~DOT");
        OPERATORS_TOKEN.put("..", "KW~RANGE");
        OPERATORS_TOKEN.put(":", "KW~COLON");
        OPERATORS_TOKEN.put(";", "KW~SEMI_COLON");
        OPERATORS_TOKEN.put("+", "KW~PLUS");
        OPERATORS_TOKEN.put("-", "KW~MINUS");
        OPERATORS_TOKEN.put("*", "KW~MULTIPLY");
        OPERATORS_TOKEN.put("/", "KW~DIVIDE");
        OPERATORS_TOKEN.put("<", "KW~LESS_THAN");
        OPERATORS_TOKEN.put("<=", "KW~LESS_THAN_EQUAL");
        OPERATORS_TOKEN.put(">", "KW~GREATER_THAN");
        OPERATORS_TOKEN.put(">=", "KW~GREATER_THAN_EQUAL");
        OPERATORS_TOKEN.put(":=", "KW~ASSIGNMENT");
        OPERATORS_TOKEN.put(",", "KW~COMMA");
        OPERATORS_TOKEN.put("=", "KW~EQUAL");
        OPERATORS_TOKEN.put("<>", "KW~NOT_EQUAL");

    }

    static {
        CHAR_TYPE = new HashMap<>();

        for (int i = 65; i < 91; i++){
            // Добавление строковых символов
            String Char_current = String.valueOf(Character.toChars(i)[0]);
            CHAR_TYPE.put(Char_current, TYPE.LETTER);
            CHAR_TYPE.put(Char_current.toLowerCase(), TYPE.LETTER);
        }
        for (int i = 48; i < 58; i++){
            // Добавление цифр
            String Char_current = String.valueOf(Character.toChars(i)[0]);
            CHAR_TYPE.put(Char_current, TYPE.DIGIT);
        }
        for (int i = 1; i < 33; i++){
            // Добавление пробелов
            String Char_current = String.valueOf(Character.toChars(i)[0]);
            CHAR_TYPE.put(Char_current, TYPE.SPACE);
        }

        for (String key: OPERATORS_TOKEN.keySet()) {
            CHAR_TYPE.put(key, TYPE.OPERATOR);
        }

        // Одиночная кавычка
        CHAR_TYPE.put(String.valueOf(Character.toChars(39)[0]), TYPE.QUOTE);
    }

    public static ArrayList<Token> read(File file) throws FileNotFoundException {
        // Разделитель для чтения символов
        Scanner sc = new Scanner(file).useDelimiter("");

        while (sc.hasNext()) {
            char element = sc.next().toLowerCase().charAt(0);

            Char_type_checker(element);
        }

        token_Name = "EOF";
        Token_generate("KW~EOF");

        return Array_List_tokens;
    }

    public static void Char_type_checker(char element){ //Проверка типа символа - это может строковый литерал, цифра, пробелы, арифметический оператор или кавычка
        switch (CHAR_TYPE.get(String.valueOf(element))){

            case LETTER:
                if (!Number_isRead) {
                    token_Name += element;
                }

                if (element == 'E' && Number_isRead) {
                    token_Name += element;
                    sci_Notation = true;
                }

                break;
            case DIGIT:
                if (token_Name.isEmpty()) {
                    Number_isRead = true;
                }

                token_Name += element;

                break;
            case SPACE:
                if (String_isRead){
                    // Добавить в строку
                    token_Name += element;
                } else if (Colon_isRead) {

                    Token_generate(OPERATORS_TOKEN.get(token_Name));

                    Colon_isRead = false;

                } else if (Bool_isRead) {

                    Token_generate(OPERATORS_TOKEN.get(token_Name));

                    Bool_isRead = false;

                } else if (!Number_isRead) {
                    // Конец слова
                    token_Name = Word_end();

                    // Проверка на новую строку
                } else {
                    Number_handle();
                }
                break;
            case OPERATOR:
                if (Dot_isRead && element == '.') {
                    if (token_Name.equals(".")) {
                        token_Name = "";
                        Token_generate("KW~RANGE");
                    } else {
                        Token_generate(token_Name.substring(0, token_Name.length()-2));
                        Token_generate("KW~DOT");
                        token_Name = "";
                    }
                    Dot_isRead = false;

                } else if(String_isRead) {
                    // Добавление в строку
                    token_Name += element;
                } else if (Number_isRead) {
                    if (isFloat && element == '.') {
                        isFloat = false;
                        token_Name = token_Name.substring(0, token_Name.length()-1);
                        Number_handle();


                        Token_generate("KW~RANGE");
                        token_Name = "";

                    } else if (sci_Notation && (element == '+' || element == '-')) {
                        token_Name += element;
                    } else if (element == '.') {
                        // Десятичная часть в числе
                        isFloat = true;
                        token_Name += element;
                    } else {
                        Number_handle();


                        Token_generate(OPERATORS_TOKEN.get(String.valueOf(element)));
                    }
                } else if (Colon_isRead && element == '=') {

                    // Обработка назначения
                    token_Name += element;

                    Token_generate(OPERATORS_TOKEN.get(token_Name));


                    Colon_isRead = false;
                } else if (Bool_isRead) {
                    if (token_Name.equals("<") && ((element == '=') || (element == '>'))) {
                        token_Name += element;

                        Token_generate(OPERATORS_TOKEN.get(token_Name));
                    } else if (token_Name.equals(">") && (element == '=')) {
                        token_Name += element;

                        Token_generate(OPERATORS_TOKEN.get(token_Name));
                    }

                    Bool_isRead = false;
                } else {
                    if (element == ';') {
                        // Перед тем как строка закончится
                        token_Name = Word_end();

                        token_Name = ";";
                        Token_generate(OPERATORS_TOKEN.get(String.valueOf(element)));

                    } else if (element == ':') {
                        token_Name = Word_end();
                        Colon_isRead = true;
                        token_Name += element;
                    } else if (element == '<' || element == '>') {
                        token_Name = Word_end();
                        Bool_isRead = true;
                        token_Name += element;
                    } else if (element == '.') {
                        token_Name += element;

                        if (token_Name.equals("end.")){
                            Token_generate("KW~END");
                            Token_generate("KW~DOT");
                        } else {
                            Dot_isRead = true;
                        }
                    } else if (OPERATORS_TOKEN.containsKey(String.valueOf(element))) {
                        token_Name = Word_end();


                        token_Name = String.valueOf(element);
                        Token_generate(OPERATORS_TOKEN.get(token_Name));
                    }
                }
                break;
            case QUOTE:
                // Найдена кавычка открывающая / закрывающая
                String_isRead = !String_isRead;
                token_Name += element;

                if (!String_isRead) {
                    // Удаление хвостовых кавычек
                    token_Name = token_Name.substring(1, token_Name.length()-1);

                    // Найдена закрывающая
                    if (token_Name.length() == 1) {

                        Token_generate("KW~CHARLIT");
                    }
                }
                break;
            default:
                throw new Error("Unhandled element scanned");
        }
    }

    public static String Word_end(){  //проверяет конец слова и возвращает имя токена
        if(KEYWORDS_TOKEN.containsKey(token_Name)){

            Token_generate(KEYWORDS_TOKEN.get(token_Name));
        } else {
            if (token_Name.length() > 0) {

                if (token_Name.equals("true") || token_Name.equals("false")) {

                    Token_generate("KW~BOOLLIT");
                } else {

                    Token_generate("KW~IDENTIFIER");
                }
            }
        }

        clearStatuses();

        return token_Name;
    }

    public static void clearStatuses() { //сброс статусов читаемого слова
        String_isRead = false;
        Number_isRead = false;
        isFloat = false;
        sci_Notation = false;
        Colon_isRead = false;
        Bool_isRead = false;
    }

    public static void Token_generate(String tokenType) {
        Token t = new Token(tokenType, token_Name);
        Array_List_tokens.add(t); // Токен добавляется в список

        token_Name = "";
    }

    public static void Number_handle() { //Целочисленный тип или вещественный
        Number_isRead = false;
        if (isFloat) {

            Token_generate("KW~FLOATLIT");
            isFloat = false;
        } else {

            Token_generate("KW~INTLIT");
        }
    }

    enum TYPE {
        LETTER, DIGIT, SPACE, OPERATOR, QUOTE
    }
}