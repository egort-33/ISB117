/*
Например
KW~FLOATLIT: 0
    token_Type: "KW~FLOATLIT"
    token_Value: "0"
 */

public final class Token {
    private String token_Type;
    private final String token_Value;

    public Token(String token_Type, String token_Value){
        this.token_Type = token_Type;
        this.token_Value = token_Value;

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