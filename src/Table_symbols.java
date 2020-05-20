public final class Table_symbols {

    static class Scope {
        Symbol[] Symbol_Table = new Symbol[HASH_TABLE_SIZE]; // Таблица символов для текущей области видимости
        Scope next_scope_pointer = null; // Указатель на следующую внешнюю область
    }

    private static final int HASH_TABLE_SIZE = 211;
    private static final Scope Scope_header = new Scope();

    public static void insert(Symbol symbol) {
        int hash_Value = hash(symbol.getName());

        Symbol Cursor_bucket = Scope_header.Symbol_Table[hash_Value];
        if (Cursor_bucket == null) {
            // Список пуст
            Scope_header.Symbol_Table[hash_Value] = symbol;
        } else {
            // Существующие символы в списке
            while (Cursor_bucket.next_entry_pointer != null) {
                Cursor_bucket = Cursor_bucket.next_entry_pointer;
            }

            // Добавить в конец списка
            Cursor_bucket.next_entry_pointer = symbol;
        }
    }

    public static Symbol find_and_get(String symbol_Name) {
        int hash_Value = hash(symbol_Name);
        Symbol Cursor_bucket = Scope_header.Symbol_Table[hash_Value];
        Scope Cursor_scope = Scope_header;

        while (Cursor_scope != null) {
            while (Cursor_bucket != null) {
                if (Cursor_bucket.getName().equals(symbol_Name)) {
                    return Cursor_bucket;
                }
                Cursor_bucket = Cursor_bucket.next_entry_pointer;
            }
            Cursor_scope = Cursor_scope.next_scope_pointer;
        }

        // Символ не существует
        return null;
    }

    public static int hash(String symbol_Name) {
        int hash_int_val = 0;
        for (int i = 0; i < symbol_Name.length(); i++) {
            hash_int_val = hash_int_val + hash_int_val + symbol_Name.charAt(i);
        }

        hash_int_val = hash_int_val % HASH_TABLE_SIZE;

        return hash_int_val;
    }


}