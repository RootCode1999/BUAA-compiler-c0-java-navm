import java.io.FileReader;
import java.io.*;
import java.util.*;

public class Tokenizer {

    public ArrayList<Optional<Token>> get_all_Tokens() {
        file_end = false;
        ArrayList<Optional<Token>> result = new ArrayList<>();
        while (true) {
            Optional<Token> p = NextToken();
            if (file_end)
                break;
            if (pos.getFirst() > lines.size() - 1)
                break;
            result.add(p);
        }
        return result;

    }

    public Optional<Token> NextToken() {
        if (!read_flag)
            readfile();
        Optional<Token> p = next_token();
        return p;
    }

    Optional<Token> next_token() {
        StringBuffer bf = new StringBuffer();
        Optional<Token> res = Optional.of(new Token());
        Pair<Integer, Integer> start_token_pos = new Pair<>(0, 0);
        DFA current_state = DFA.INITIAL;
        start_token_pos = pos;
        while (true) {
            if (pos.getFirst() > lines.size() - 1) {
                file_end = true;
                return res;
            }
            Optional<Character> currentChar = nextChar();

            if (current_state == DFA.INITIAL) {
                if (currentChar == null) {
                    file_end = true;
                    return Optional.of(new Token());
                }
                char ch = currentChar.get();
                boolean flag = true;
                if (Character.isWhitespace(ch))
                    current_state = DFA.INITIAL;
                else if (Character.isISOControl(ch)) // Invisible
                    flag = false;
                else if (Character.isDigit(ch) && ch == '0')// 0
                    current_state = DFA.INITIAL;
                else if (Character.isDigit(ch) && ch != '0')// 10
                    current_state = DFA.DECIMAL;
                else if (Character.isAlphabetic(ch) || ch == '_')// alphabet
                    current_state = DFA.IDENTIFIER;
                else {
                    if (ch == ':')
                        current_state = DFA.COLON;
                    else if (ch == '(')
                        current_state = DFA.LEFT_BRACKET;
                    else if (ch == ')')
                        current_state = DFA.RIGHT_BRACKET;
                    else if (ch == '{')
                        current_state = DFA.LEFT_BRACE;
                    else if (ch == '}')
                        current_state = DFA.RIGHT_BRACE;
                    else if (ch == ',')
                        current_state = DFA.COMMA;
                    else if (ch == ';')
                        current_state = DFA.SEMICOLON;
                    else if (ch == '!')
                        current_state = DFA.NOT;
                    else if (ch == '<')
                        current_state = DFA.LESS;
                    else if (ch == '>')
                        current_state = DFA.GREATER;
                    else if (ch == '=')
                        current_state = DFA.ASSIGN;
                    else if (ch == '-')
                        current_state = DFA.MINUS_SIGN;
                    else if (ch == '/')
                        current_state = DFA.DIVIDE_SIGN;
                    else if (ch == '+')
                        current_state = DFA.PLUS_SIGN;
                    else if (ch == '*')
                        current_state = DFA.MULTIPLY_SIGN;
                    else if (ch == '\'')
                        current_state = DFA.CHAR;
                    else if (ch == '\"')
                        current_state = DFA.STRING;
                    else
                        flag = false;
                }
                if (current_state != DFA.INITIAL) {
                    start_token_pos = previouspos();
                    bf.append(ch);
                }
                if (flag == false) {
                    System.exit(1);
                }
            }

            else if(current_state == DFA.STRING){
                char ch = currentChar.get();
                if (ch != '\"') {
                    bf.append(ch);
                } else {
                    unreadlast();
                    String tokenValue = bf.toString();
                    return Optional.of(new Token(Token.tokentype.STRING, bf.toString(), start_token_pos, currentpos()));
                }
            }

            else if(current_state == DFA.CHAR){
                char ch = currentChar.get();
                if (ch != '\'') {
                    bf.append(ch);
                } else {
                    unreadlast();
                    String tokenValue = bf.toString();
                    if(tokenValue.length()!=1)
                        System.exit(1);
                    return Optional.of(new Token(Token.tokentype.CHAR, bf.toString(), start_token_pos, currentpos()));
                }
            }

            // 10
            else if (current_state == DFA.DECIMAL) {
                Token.tokentype type = Token.tokentype.INT;
                if (currentChar == null) {
                    System.exit(1);
                }
                char ch = currentChar.get();
                if (Character.isDigit(ch))
                    bf.append(ch);
                else if(ch == '.'){
                    bf.append(ch);
                    type = Token.tokentype.DOUBLE;
                }
                else {
                    if(Character.isAlphabetic(ch))
                        System.exit(1);
                    unreadlast();
                    String tokenvalue = bf.toString();
                    if (tokenvalue.length() >= max_int.length() || tokenvalue.compareTo(max_int) > 0) {
                        System.exit(1);
                    }
                    return Optional.of(new Token(type, bf.toString(), start_token_pos, currentpos()));
                }
            }

            // ident || keyword
            else if (current_state == DFA.IDENTIFIER) {
                if (currentChar == null) {
                    System.exit(1);
                }
                char ch = currentChar.get();
                if (Character.isDigit(ch) || Character.isAlphabetic(ch))
                    bf.append(ch);
                else {
                    unreadlast();
                    return get_the_ident(bf, start_token_pos);
                }
            }

            // +
            else if (current_state == DFA.PLUS_SIGN) {
                unreadlast();
                return Optional.of(new Token(Token.tokentype.PLUS, "+", start_token_pos, currentpos()));
            }

            // - || ->
            else if (current_state == DFA.MINUS_SIGN) {
                char ch = currentChar.get();
                if (ch == '>') {
                    bf.append(ch);
                    current_state = DFA.NOTEQUAL;
                    return Optional.of(new Token(Token.tokentype.ARROW, "->", start_token_pos, currentpos()));
                } else {
                    unreadlast();
                    String tokenValue = bf.toString();
                    return Optional.of(new Token(Token.tokentype.MINUS, "-", start_token_pos, currentpos()));
                }
            }

            // *
            else if (current_state == DFA.MULTIPLY_SIGN) {
                unreadlast();
                return Optional.of(new Token(Token.tokentype.MUL, "*", start_token_pos, currentpos()));
            }

            // /
            else if (current_state == DFA.DIVIDE_SIGN) {
                char ch = currentChar.get();
                if(ch == '/'){
                    while(true) {
                        currentChar = nextChar();
                        ch = currentChar.get();
                        if(ch == '\n'){
                            current_state = DFA.INITIAL;
                            break;
                        }
                    }
                }
                unreadlast();
                return Optional.of(new Token(Token.tokentype.DIV, "/", start_token_pos, currentpos()));
            }

            // =|==
            else if (current_state == DFA.ASSIGN) {
                char ch = currentChar.get();
                if (ch == '=') {
                    bf.append(ch);
                    current_state = DFA.EQUAL;
                    return Optional.of(new Token(Token.tokentype.EQ, "==", start_token_pos, currentpos()));
                } else {
                    unreadlast();
                    String tokenValue = bf.toString();
                    return Optional.of(new Token(Token.tokentype.ASSIGN, "=", start_token_pos, currentpos()));
                }
            }

            // !
            else if (current_state == DFA.NOT) {
                char ch = currentChar.get();
                if (ch == '=') {
                    bf.append(ch);
                    current_state = DFA.NOTEQUAL;
                    return Optional.of(new Token(Token.tokentype.NEQ, bf.toString(), start_token_pos, currentpos()));
                } else {
                    unreadlast();
                    System.exit(1);
                }
            }

            // < | <=
            else if (current_state == DFA.LESS) {
                char ch = currentChar.get();
                if (ch == '=') {
                    bf.append(ch);
                    current_state = DFA.GREATER_EQUAL;
                    return Optional.of(new Token(Token.tokentype.LE, ">=", start_token_pos, currentpos()));
                } else {
                    unreadlast();
                    String tokenValue = bf.toString();
                    return Optional.of(new Token(Token.tokentype.LT, "<", start_token_pos, currentpos()));
                }
            }

            // >
            else if (current_state == DFA.GREATER) {
                char ch = currentChar.get();
                if (ch == '=') {
                    bf.append(ch);
                    current_state = DFA.LESS_EQUAL;
                    return Optional.of(new Token(Token.tokentype.GE, ">=", start_token_pos, currentpos()));
                } else {
                    unreadlast();
                    String tokenValue = bf.toString();
                    return Optional.of(new Token(Token.tokentype.GT, ">", start_token_pos, currentpos()));
                }
            }

            // (
            else if (current_state == DFA.LEFT_BRACKET) {
                unreadlast();
                return Optional.of(new Token(Token.tokentype.L_PAREN, "(", start_token_pos, currentpos()));
            }

            // )
            else if (current_state == DFA.RIGHT_BRACKET) {
                unreadlast();
                return Optional.of(new Token(Token.tokentype.R_PAREN, ")", start_token_pos, currentpos()));
            }

            // {
            else if (current_state == DFA.LEFT_BRACE) {
                unreadlast();
                return Optional.of(new Token(Token.tokentype.L_BRACE, "{", start_token_pos, currentpos()));
            }

            // }
            else if (current_state == DFA.RIGHT_BRACE) {
                unreadlast();
                return Optional.of(new Token(Token.tokentype.R_BRACE, "}", start_token_pos, currentpos()));
            }

            // ,
            else if (current_state == DFA.COMMA) {
                unreadlast();
                return Optional.of(new Token(Token.tokentype.COMMA, ",", start_token_pos, currentpos()));
            }

            // :
            else if (current_state == DFA.COLON) {
                unreadlast();
                return Optional.of(new Token(Token.tokentype.COLON, ":", start_token_pos, currentpos()));
            }

            // ;
            else if (current_state == DFA.SEMICOLON) {
                unreadlast();
                return Optional.of(new Token(Token.tokentype.SEMICOLON, ";", start_token_pos, currentpos()));
            }
        }
    }

    void readfile() {
        if (read_flag)
            return;
        read_flag = true;
        pos = new Pair<>(0, 0);
        try {
            BufferedReader bf = new BufferedReader(fp);
            String str;
            while ((str = bf.readLine()) != null) {
                lines.add(str + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    enum DFA {
        INITIAL, // init
        DECIMAL, // 10
        ZERO, HEXADECIMAL, IDENTIFIER, // alphabet
        RESERVED, SINGLE_QUOTE, CHAR, DOUBLEQUOTE, STRING, COLON, // :
        LEFT_BRACKET, // (
        RIGHT_BRACKET, // )
        RIGHT_BRACE, // }
        LEFT_BRACE, // {
        COMMA, // ,
        SEMICOLON, // ;
        NOT, // !
        NOTEQUAL, LESS, // <
        LESS_EQUAL, // <=
        GREATER, // >
        GREATER_EQUAL, ASSIGN, // =
        EQUAL, MINUS_SIGN, // -
        PLUS_SIGN, // +
        MULTIPLY_SIGN, // *
        DIVIDE_SIGN, // /
        SINGLE_COMMENT, MORE_COMMENT, COMMENT,
    }

    Map<String, Token.tokentype> keywords = new HashMap<String, Token.tokentype>();
    boolean read_flag;
    FileReader fp;
    Pair<Integer, Integer> pos = new Pair<>();
    String max_int = "9223372036854775807";
    String min_int = "-9223372036854775808";
    String max_uint = "18446744073709551615";
    ArrayList<String> lines = new ArrayList<>();
    boolean file_end;

    public Tokenizer(String filename) {
        try {
            File f = new File(filename);
            fp = new FileReader(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.read_flag = false;
        this.pos.setFirst(0);
        this.pos.setSecond(0);
        this.file_end = false;
        this.keywords.put("fn", Token.tokentype.FN_KW);
        this.keywords.put("let", Token.tokentype.LET_KW);
        this.keywords.put("const", Token.tokentype.CONST_KW);
        this.keywords.put("as", Token.tokentype.AS_KW);
        this.keywords.put("while", Token.tokentype.WHILE_KW);
        this.keywords.put("if", Token.tokentype.IF_KW);
        this.keywords.put("else", Token.tokentype.ELSE_KW);
        this.keywords.put("return", Token.tokentype.RETURN_KW);
        this.keywords.put("break", Token.tokentype.BREAK_KW);
        this.keywords.put("continue", Token.tokentype.CONTINUE_KW);
        this.keywords.put("int", Token.tokentype.INT);
        this.keywords.put("double", Token.tokentype.DOUBLE);
        this.keywords.put("void", Token.tokentype.VOID);
    }

    Pair<Integer, Integer> nextpos() {
        if (pos.getFirst() >= lines.size()) {
            System.exit(1);
        }
        if (pos.getSecond() == lines.get(pos.getFirst()).length() - 1)
            return new Pair<>(pos.getFirst() + 1, 0);
        else
            return new Pair<>(pos.getFirst(), pos.getSecond() + 1);
    }

    Pair<Integer, Integer> previouspos() {
        if (pos.getFirst() == 0 && pos.getSecond() == 0) {
            System.exit(1);
        }
        if (pos.getSecond() == 0)
            return new Pair<>(pos.getFirst() - 1, lines.get(pos.getFirst() - 1).length() - 1);
        else
            return new Pair<>(pos.getFirst(), pos.getSecond() - 1);
    }

    Pair<Integer, Integer> currentpos() {
        return pos;
    }

    boolean isEOF() {
        return pos.getFirst() >= lines.size();
    }

    Optional<Character> nextChar() {
        if (isEOF())
            return Optional.empty();
        char result = lines.get(pos.getFirst()).charAt(pos.getSecond());
        pos = nextpos();
        return Optional.of(result);
    }

    void unreadlast() {
        pos = previouspos();
    }

    Optional<Token> get_the_ident(StringBuffer s, Pair<Integer, Integer> start_pos) {
        String token_str = s.toString();
        if (keywords.get(token_str) != null)
            return Optional.of(new Token(keywords.get(token_str), token_str, start_pos, currentpos()));
        else
            return Optional.of(new Token(Token.tokentype.IDENT, token_str, start_pos, currentpos()));
    }
}