/*
Например
KW~FLOATLIT: 0
    token_Type: "KW~FLOATLIT"
    token_Value: "0"
 */

public final class Token {
    private String token_Type = "";
    private String token_Value = "";

    private int line_Column = 0;
    private int line_Row = 0;

    public Token(String token_Type, String token_Value, int line_Column, int line_Row){
        this.token_Type = token_Type;
        this.token_Value = token_Value;

        this.line_Column = line_Column;
        this.line_Row = line_Row;
    }

    @Override
    public String toString(){
        return token_Value;
    }

    public String get_Token_Type() {
        return token_Type;
    }

    public void set_Token_Type(String tokenType) {
        this.token_Type = tokenType;
    }

    public String get_Token_Val() {
        return token_Value;
    }


}