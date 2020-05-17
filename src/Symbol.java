/*
Example:
    var x,y,z : integer;
    x := 2
    For variable x:
        name = "x"
        tokenType = "TK_A_VAR"
        dataType = TYPE.I
        value = 2
        address = 0
 */

public class Symbol {
    private String name = "";
    private String Token_Type = "";
    private Parser.TYPE Data_Type = null;
    private int Address;
    private int Address_return; // Возвращает адрес для процедуры

    private Object low_value_range; // Нижняя граница массива
    private Object high_value_range; // Верхняя граница массива

    private Parser.TYPE index_Type; // Тип индекса в массиве
    private Parser.TYPE value_Type; // Тип значений в массиве

    Symbol next_entry_pointer; // Указатель на следующее вхождение в действующем списке таблицы символов

    public Symbol(String name, String Token_Type, Parser.TYPE Data_Type, int address){
        this.name = name;
        this.Token_Type = Token_Type;
        this.Data_Type = Data_Type;
        this.Address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Parser.TYPE getData_Type() {
        return Data_Type;
    }

    public int getAddress() {
        return Address;
    }

    public void set_Address(int address) {
        this.Address = address;
    }

    public String getToken_Type() {
        return Token_Type;
    }

    public void set_Token_Type(String tokenType) {
        this.Token_Type = tokenType;
    }

    public int getAddress_return() {
        return Address_return;
    }

    public void setAddress_return(int address_return) {
        this.Address_return = address_return;
    }

    public Object get_Low() {
        return low_value_range;
    }

    public void set_Low(Object low_range) {
        this.low_value_range = low_range;
    }

    public Object get_High() {
        return high_value_range;
    }

    public void set_High(Object high_range) {
        this.high_value_range = high_range;
    }

    public Parser.TYPE getIndex_Type() {
        return index_Type;
    }

    public void set_Index_Type(Parser.TYPE indexType) {
        this.index_Type = indexType;
    }

    public Parser.TYPE getValue_Type() {
        return value_Type;
    }

    public void set_Value_Type(Parser.TYPE valueType) {
        this.value_Type = valueType;
    }
}