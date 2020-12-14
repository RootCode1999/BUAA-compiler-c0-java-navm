public class Token {
    public enum tokentype {
        // token
        FN_KW, // 'fn'
        LET_KW, // 'let'
        CONST_KW, // 'const'
        AS_KW, // 'as'
        WHILE_KW, // 'while'
        IF_KW, // 'if'
        ELSE_KW, // 'else'
        RETURN_KW, // 'return'
        BREAK_KW, // 'break'
        CONTINUE_KW, // 'continue'
        INT, // 'int'
        DOUBLE, // 'double'
        VOID, // 'void'
        IDENT, //
        PLUS, // '+'
        MINUS, // '-'
        MUL, // '*'
        DIV, // '/'
        ASSIGN, // '='
        EQ, // '=='
        NEQ, // '!='
        LT, // '<'
        GT, // '>'
        LE, // '<='
        GE, // '>='
        L_PAREN, // '('
        R_PAREN, // ')'
        L_BRACE, // '{'
        R_BRACE, // '}'
        ARROW, // '->'
        COMMA, // ','
        COLON, // ':'
        SEMICOLON, // ';'
        STRING,
        CHAR,
        COMMENT, // '//'

        TYPENULL
    }

    // parameter
    tokentype type;
    String value;
    Pair<Integer, Integer> startpos;
    Pair<Integer, Integer> endpos;

    public tokentype getType() {
        return type;
    }

    public String getValue(){return value;}

    public Pair<Integer, Integer> getStartPos() {
        return startpos;
    }

    public Pair<Integer, Integer> getEndPos() {
        return endpos;
    }

    public Token() {
        type = tokentype.TYPENULL;
        value = "";
        startpos = new Pair<>(0, 0);
        endpos = new Pair<>(0, 0);
    }

    public Token(tokentype type, String value, Pair<Integer, Integer> startpos, Pair<Integer, Integer> endpos) {
        this.type = type;
        this.value = value;
        this.startpos = startpos;
        this.endpos = endpos;
    }

}
