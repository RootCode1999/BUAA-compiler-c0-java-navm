import java.util.ArrayList;

public class Analyser {
    public ArrayList<Token> tokens = new ArrayList<Token>();
    public int offset = 0;
    public Pair<Integer, Integer> currentpos;
    public ArrayList<varia> globalVariable_arry = new ArrayList<>();
    public ArrayList<varia> localVariable_arry = new ArrayList<>();
    public ArrayList<varia> function_arry = new ArrayList<>();
    public ArrayList<varia> function_var = new ArrayList<>();
    public int count_gv = 0;
    public int count_lv = 0;
    public int count_fun = 0;
    public ArrayList<Token.tokentype> tokentypes = new ArrayList<>();

    // expr ->
    //   assign_expr
    public exprAst expr_analyse() {
        assign_exprAst expr;
        operator_exprAst operator;
        Token tk1 = nextToken();
        Token tk2 = nextToken();
        if(tk2.getType() == Token.tokentype.ASSIGN){
            unreadToken();
            unreadToken();
            expr = assign_expr_analyse();
            if (expr == null)
                return null;
            else
                return new exprAst(expr);
        }
        unreadToken();
        unreadToken();
        return null;
    }

    // assign_expr -> l_expr '=' operator_expr
    public assign_exprAst assign_expr_analyse() {
        Token tk1, tk2;
        tk1 = nextToken();
        if (!is_variabale(tk1.getValue())) {
            System.out.println("assign_expr");
            System.exit(1);
        }
        tk2 = nextToken();
        if (tk2.getType() != Token.tokentype.ASSIGN) {
            unreadToken();
            unreadToken();
            return null;
        }
        operator_exprAst operator_expr = operator_expr_analyse();
        if (operator_expr == null)
        {
            System.out.println("assign_expr");
            System.exit(1);
        }
        return new assign_exprAst(tk1.getValue(), operator_expr);
    }

    // binary_operator -> '+' | '-'
    // operator_expr -> multiplicative_expr (binary_operator multiplicative_expr)*
    public operator_exprAst operator_expr_analyse() {
        multiplicative_exprAst multiplicative_expr;
        Token binary_operator;
        ArrayList<String> oper = new ArrayList<>();
        ArrayList<multiplicative_exprAst> operator_expr = new ArrayList<>();
        multiplicative_expr = multiplicative_expr_analyse();
        binary_operator = nextToken();
        if (binary_operator.getType() == Token.tokentype.PLUS)
            oper.add("+");
        else if (binary_operator.getType() == Token.tokentype.MINUS)
            oper.add("-");
        else {
            unreadToken();
            return new operator_exprAst(multiplicative_expr);
        }
        while(true){
            multiplicative_exprAst temp;
            temp = multiplicative_expr_analyse();
            if(temp == null){
                System.out.println("operator_expr");
                System.exit(1);
            }
            operator_expr.add(temp);
            binary_operator = nextToken();
            if (binary_operator.getType() == Token.tokentype.PLUS)
                oper.add("+");
            else if (binary_operator.getType() == Token.tokentype.MINUS)
                oper.add("-");
            else{
                unreadToken();
                break;
            }
        }
        return new operator_exprAst(multiplicative_expr, oper, operator_expr);
    }

    // relational-operator -> '==' | '!=' | '<' | '>' | '<=' | '>='
    // condition ->
    // operator_expr[relational-operator operator_expr]
    public conditionAst condition_analyse() {
        operator_exprAst operator_exprl;
        Token relational_operator;
        Token tk = nextToken();
        if(tk.getType() == Token.tokentype.L_PAREN){
            operator_exprl = operator_expr_analyse();
            if (operator_exprl == null)
                return null;
            relational_operator = nextToken();
            String oper="";
            if (relational_operator.getType() == Token.tokentype.EQ)
                oper = "==";
            else if (relational_operator.getType() == Token.tokentype.NEQ)
                oper = "!=";
            else if (relational_operator.getType() == Token.tokentype.LT)
                oper = "<";
            else if (relational_operator.getType() == Token.tokentype.GT)
                oper = ">";
            else if (relational_operator.getType() == Token.tokentype.LE)
                oper = "<=";
            else if (relational_operator.getType() == Token.tokentype.GE)
                oper = ">=";
            else if (relational_operator.getType() == Token.tokentype.R_PAREN){
                return new conditionAst(operator_exprl);
            }
            else {
                System.out.println("condition");
                System.exit(1);
            }
            operator_exprAst operator_exprr;
            operator_exprr = operator_expr_analyse();
            if (operator_exprr == null){
                System.out.println("condition");
                System.exit(1);
            }
            tk = nextToken();
            if(tk.getType() != Token.tokentype.R_PAREN){
                System.out.println("condition");
                System.exit(1);
            }
            return new conditionAst(operator_exprl, oper, operator_exprr);
        }
        else{
            unreadToken();
            operator_exprl = operator_expr_analyse();
            if (operator_exprl == null)
                return null;
            relational_operator = nextToken();
            String oper="";
            if (relational_operator.getType() == Token.tokentype.EQ)
                oper = "==";
            else if (relational_operator.getType() == Token.tokentype.NEQ)
                oper = "!=";
            else if (relational_operator.getType() == Token.tokentype.LT)
                oper = "<";
            else if (relational_operator.getType() == Token.tokentype.GT)
                oper = ">";
            else if (relational_operator.getType() == Token.tokentype.LE)
                oper = "<=";
            else if (relational_operator.getType() == Token.tokentype.GE)
                oper = ">=";
            else {
                unreadToken();
                return new conditionAst(operator_exprl);
            }
            operator_exprAst operator_exprr;
            operator_exprr = operator_expr_analyse();
            if (operator_exprr == null){
                System.out.println("condition");
                System.exit(1);
            }
            return new conditionAst(operator_exprl, oper, operator_exprr);
        }
    }

    // multiplicative_operator -> '*' | '/'
    // multiplicative_expr ->
    // as_expr （multiplicative_operator as_expr）*
    public multiplicative_exprAst multiplicative_expr_analyse() {
        as_exprAst as_expr;
        Token multiplicative_operator;
        ArrayList<String> oper = new ArrayList<>();
        ArrayList<as_exprAst> multiplicative_expr = new ArrayList<>();
        as_expr = as_expr_analyse();
        if (as_expr == null)
            return null;
        multiplicative_operator = nextToken();

        if (multiplicative_operator.getType() == Token.tokentype.MUL)
            oper.add("*");
        else if (multiplicative_operator.getType() == Token.tokentype.DIV)
            oper.add("/");
        else {
            unreadToken();
            return new multiplicative_exprAst(as_expr);
        }
        while(true){
            as_exprAst temp = as_expr_analyse();
            multiplicative_expr.add(temp);
            if (temp == null)
                System.exit(1);
            multiplicative_operator = nextToken();
            if (multiplicative_operator.getType() == Token.tokentype.MUL)
                oper.add("*");
            else if (multiplicative_operator.getType() == Token.tokentype.DIV)
                oper.add("/");
            else {
                unreadToken();
                break;
            }
        }
        return new multiplicative_exprAst(as_expr, oper, multiplicative_expr);
    }

    // as_expr -> primary_expr {'as' ty}
    public as_exprAst as_expr_analyse() {
        primary_exprAst primary_expr;
        ArrayList<Token> as_kws=new ArrayList<>();
        ArrayList<String> tys=new ArrayList<>();
        primary_expr = primary_expr_analyse();
        if (primary_expr == null)
            return null;
        Token as_kw = nextToken();
        if (as_kw.getType() != Token.tokentype.AS_KW) {
            unreadToken();
            return new as_exprAst(primary_expr);
        }
        while(as_kw.getType() == Token.tokentype.AS_KW){
            Token ty = nextToken();
            if (ty.getType() == Token.tokentype.INT) {
                tys.add("int");
            } else if (ty.getType() == Token.tokentype.DOUBLE) {
                tys.add("double");
            } else
                System.exit(1);
            as_kw = nextToken();
        }
        unreadToken();
        return new as_exprAst(primary_expr, tys);
    }

    // primary_expr ->
    // negate_expr
    // | call_expr
    // | group_expr
    // | ident_expr
    // | literal_expr
    public primary_exprAst primary_expr_analyse() {
        Token first;
        first = nextToken();
        negate_exprAst negate_expr;
        call_exprAst call_expr;
        group_exprAst group_expr;
        ident_exprAst ident_expr;
        literal_exprAst literal_expr;
        if (first.getType() == Token.tokentype.MINUS) {
            unreadToken();
            negate_expr = negate_expr_analyse();
            if (negate_expr == null)
                System.exit(1);
            return new primary_exprAst(negate_expr);
        } else if (first.getType() == Token.tokentype.IDENT) {
            Token second;
            second = nextToken();
            if (second.getType() == Token.tokentype.L_PAREN) {
                unreadToken();
                unreadToken();
                call_expr = call_expr_analyse();
                if (call_expr == null)
                    System.exit(1);
                return new primary_exprAst(call_expr);
            } else {
                unreadToken();
                unreadToken();
                ident_expr = ident_expr_analyse();
                if (ident_expr == null)
                    System.exit(1);
                return new primary_exprAst(ident_expr);
            }
        } else if (first.getType() == Token.tokentype.L_PAREN) {
            unreadToken();
            group_expr = group_expr_analyse();
            if (group_expr == null)
                System.exit(1);
            return new primary_exprAst(group_expr);
        } else if (first.getType() == Token.tokentype.IDENT) {
            unreadToken();
            ident_expr = ident_expr_analyse();
            if (ident_expr == null)
                System.exit(1);
            return new primary_exprAst(ident_expr);
        } else if (first.getType() == Token.tokentype.INT || first.getType() == Token.tokentype.DOUBLE
                || first.getType() == Token.tokentype.CHAR || first.getType() == Token.tokentype.STRING) {
            unreadToken();
            literal_expr = literal_expr_analyse();
            if (literal_expr == null)
                System.exit(1);
            return new primary_exprAst(literal_expr);
        }
        return null;
    }

    // negate_expr -> '-' primary_expr
    public negate_exprAst negate_expr_analyse() {
        Token ty;
        primary_exprAst primary_expr;
        ty = nextToken();
        if (ty.getType() == Token.tokentype.MINUS) {
            primary_expr = primary_expr_analyse();
            if (primary_expr != null)
                return new negate_exprAst(primary_expr);
            System.exit(1);
        }
        return null;
    }

    // call_param_list -> operator_expr (',' operator_expr)*
    public call_param_listAst call_param_list_analyse() {
        ArrayList<operator_exprAst> operator_exprs = new ArrayList<operator_exprAst>();
        while (true) {
            operator_exprAst operator_expr = operator_expr_analyse();
            if (operator_expr != null)
                operator_exprs.add(operator_expr);
            else
                return null;
            Token tk = nextToken();
            if (tk.getType() != Token.tokentype.COMMA) {
                unreadToken();
                return new call_param_listAst(operator_exprs);
            }
        }
    }

    // call_expr -> IDENT '(' call_param_list? ')'
    public call_exprAst call_expr_analyse() {
        Token ident;
        call_param_listAst call_param_list;
        ident = nextToken();
        String IDENT = ident.getValue();
        if(  IDENT.equals("getint")
                || IDENT.equals("getdouble")
                || IDENT.equals("getchar")
                || IDENT.equals("putchar")
                || IDENT.equals("putdouble")
                || IDENT.equals("putint")
                || IDENT.equals("putln")
                || IDENT.equals("putstr")){
            ;
        }
        else if (!is_function(IDENT))
                System.exit(1);
        ident = nextToken();
        if (ident.getType() != Token.tokentype.L_PAREN) {
            unreadToken();
            unreadToken();
            System.exit(1);
        }
        ident = nextToken();
        if(ident == null)
            System.exit(1);
        if (ident.getType() == Token.tokentype.R_PAREN) {
            return new call_exprAst(IDENT);
        } else {
            unreadToken();
            call_param_list = call_param_list_analyse();
            ident = nextToken();
            if (ident.getType() == Token.tokentype.R_PAREN) {
                return new call_exprAst(IDENT, call_param_list);
            } else
                System.exit(1);
        }
        return null;
    }

    // literal_expr -> UINT_LITERAL | DOUBLE_LITERAL | STRING_LITERAL | CHAR_LITERAL
    public literal_exprAst literal_expr_analyse() {
        Token tk = nextToken();
        if (tk.getType() == Token.tokentype.INT || tk.getType() == Token.tokentype.DOUBLE
                || tk.getType() == Token.tokentype.STRING || tk.getType() == Token.tokentype.CHAR) {
            String res = tk.getValue();
            return new literal_exprAst(res);
        }
        unreadToken();
        return null;
    }

    // ident_expr -> IDENT
    public ident_exprAst ident_expr_analyse() {
        Token tk = nextToken();
        if (tk.getType() == Token.tokentype.IDENT)
            return new ident_exprAst(tk.getValue());
        unreadToken();
        return null;
    }

    // group_expr -> '(' operator_expr ')'
    public group_exprAst group_expr_analyse() {
        Token tk = nextToken();
        operator_exprAst operator_expr;
        if (tk.getType() != Token.tokentype.L_PAREN) {
            unreadToken();
            return null;
        } else
            operator_expr = operator_expr_analyse();
        if (operator_expr == null)
            System.exit(1);
        tk = nextToken();
        if (tk.getType() != Token.tokentype.R_PAREN) {
            System.exit(1);
        }
        return new group_exprAst(operator_expr);
    }

    // stmt ->
    // expr_stmt
    // | decl_stmt
    // | if_stmt
    // | while_stmt
    // | break_stmt
    // | continue_stmt
    // | return_stmt
    // | block_stmt
    // | empty_stmt
    public stmtAst stmt_analyse() {
        decl_stmtAst decl_stmt;
        if_stmtAst if_stmt;
        while_stmtAst while_stmt;
        break_stmtAst break_stmt;
        continue_stmtAst continue_stmt;
        Token tk = nextToken();
        if(tk==null)
            System.exit(1);
        if (tk.getType() == Token.tokentype.R_BRACE) {
            unreadToken();
            return null;
        }
        if (tk.getType() == Token.tokentype.LET_KW || tk.getType() == Token.tokentype.CONST_KW) {
            unreadToken();
            decl_stmt = decl_stmt_analyse();
            if (decl_stmt == null)
                System.exit(1);
            return new stmtAst(decl_stmt);
        } else if (tk.getType() == Token.tokentype.IF_KW) {
            unreadToken();
            if_stmt = if_stmt_analyse();
            if (if_stmt == null)
                System.exit(1);
            return new stmtAst(if_stmt);
        } else if (tk.getType() == Token.tokentype.WHILE_KW) {
            unreadToken();
            while_stmt = while_stmt_analyse();
            if (while_stmt == null)
                System.exit(1);
            return new stmtAst(while_stmt);
        } else if (tk.getType() == Token.tokentype.BREAK_KW) {
            unreadToken();
            break_stmt = break_stmt_analyse();
            if (break_stmt == null)
                System.exit(1);
            return new stmtAst(break_stmt);
        } else if (tk.getType() == Token.tokentype.CONTINUE_KW) {
            unreadToken();
            continue_stmt = continue_stmt_analyse();
            if (continue_stmt == null)
                System.exit(1);
            return new stmtAst(continue_stmt);
        } else if (tk.getType() == Token.tokentype.RETURN_KW) {
            unreadToken();
            return_stmtAst return_stmt = return_stmt_analyse();
            if (return_stmt == null)
                System.exit(1);
            return new stmtAst(return_stmt);
        } else if (tk.getType() == Token.tokentype.L_BRACE) {
            unreadToken();
            block_stmtAst block_stmt = block_stmt_analyse();
            if (block_stmt == null)
                System.exit(1);
            return new stmtAst(block_stmt);
        } else if (tk.getType() == Token.tokentype.SEMICOLON) {
            unreadToken();
            empty_stmtAst empty_stmt = empty_stmt_analyse();
            if (empty_stmt == null)
                System.exit(1);
            return new stmtAst(empty_stmt);
        } else {
            unreadToken();
            expr_stmtAst expr_stmt = expr_stmt_analyse();
            if (expr_stmt == null)
                System.exit(1);
            return new stmtAst(expr_stmt);
        }
    }

    // expr_stmt -> expr|operator_expr ';'
    public expr_stmtAst expr_stmt_analyse() {
        Token tk1 = nextToken();
        Token tk2 = nextToken();
        if( tk1.getType() == Token.tokentype.IDENT &&
            tk2.getType() == Token.tokentype.ASSIGN){
            unreadToken();
            unreadToken();
            exprAst expr;
            Token tk;
            expr = expr_analyse();
            if (expr == null)
                return null;
            tk = nextToken();
            if (tk.getType() != Token.tokentype.SEMICOLON)
                System.exit(1);
            return new expr_stmtAst(expr);
        }
        else{
            unreadToken();
            unreadToken();
            operator_exprAst expr;
            Token tk;
            expr = operator_expr_analyse();
            if (expr == null)
                return null;
            tk = nextToken();
            if (tk.getType() != Token.tokentype.SEMICOLON)
                System.exit(1);
            return new expr_stmtAst(expr);
        }
    }

    // let_decl_stmt -> 'let' IDENT ':' ty ('=' operator_expr)? ';'
    public let_decl_stmtAst let_decl_stmt_analyse() {
        Token tk1, tk2, tk3, tk4, tk5;
        tk1 = nextToken();
        tk2 = nextToken();
        tk3 = nextToken();
        tk4 = nextToken();
        tk5 = nextToken();
        if (tk1.getType() == Token.tokentype.LET_KW && tk2.getType() == Token.tokentype.IDENT
                && tk3.getType() == Token.tokentype.COLON
                && (tk4.getType() == Token.tokentype.INT || tk4.getType() == Token.tokentype.DOUBLE)
                && tk5.getType() == Token.tokentype.ASSIGN) {
            operator_exprAst operator_expr = operator_expr_analyse();
            if (operator_expr == null)
                System.exit(1);
            Token tk6 = nextToken();
            varia this_variabale = new varia(tk2.getValue(), tk4.getValue(), false);
            if (function_arry.size() == 0)
                globalVariable_arry.add(this_variabale);
            else
                localVariable_arry.add(this_variabale);
            if (tk6.getType() == Token.tokentype.SEMICOLON)
                return new let_decl_stmtAst(tk2.getValue(), tk4.getValue(), operator_expr);
            System.exit(1);
        } else if (tk1.getType() == Token.tokentype.LET_KW && tk2.getType() == Token.tokentype.IDENT
                && tk3.getType() == Token.tokentype.COLON
                && (tk4.getType() == Token.tokentype.INT || tk4.getType() == Token.tokentype.DOUBLE)
                && tk5.getType() == Token.tokentype.SEMICOLON) {
            varia this_variabale = new varia(tk2.getValue(), tk4.getValue(), false);
            if (function_arry.size() == 0)
                globalVariable_arry.add(this_variabale);
            else
                localVariable_arry.add(this_variabale);
            return new let_decl_stmtAst(tk2.getValue(), tk4.getValue());
        }
        return null;
    }

    // const_decl_stmt -> 'const' IDENT ':' ty '=' operator_expr ';'
    public const_decl_stmtAst const_decl_stmt_analyse() {
        Token tk1, tk2, tk3, tk4, tk5;
        tk1 = nextToken();
        tk2 = nextToken();
        tk3 = nextToken();
        tk4 = nextToken();
        tk5 = nextToken();
        if (tk1.getType() == Token.tokentype.CONST_KW && tk2.getType() == Token.tokentype.IDENT
                && tk3.getType() == Token.tokentype.COLON
                && (tk4.getType() == Token.tokentype.INT || tk4.getType() == Token.tokentype.DOUBLE)
                && tk5.getType() == Token.tokentype.ASSIGN) {
            operator_exprAst operator_expr = operator_expr_analyse();
            if (operator_expr == null)
                System.exit(1);
            varia this_variabale = new varia(tk2.getValue(), tk4.getValue(), true);
            if (function_arry.size() == 0)
                globalVariable_arry.add(this_variabale);
            else
                localVariable_arry.add(this_variabale);
            Token tk6 = nextToken();
            if (tk6.getType() == Token.tokentype.SEMICOLON)
                return new const_decl_stmtAst(tk2.getValue(), tk4.getValue(), operator_expr);
            System.exit(1);
        } else {
            System.exit(1);
        }
        return null;
    }

    // decl_stmt -> let_decl_stmt | const_decl_stmt
    public decl_stmtAst decl_stmt_analyse() {
        Token tk;
        tk = nextToken();
        if (tk.getType() == Token.tokentype.LET_KW || tk.getType() == Token.tokentype.CONST_KW) {
            unreadToken();
            if (tk.getType() == Token.tokentype.LET_KW) {
                let_decl_stmtAst let_decl_stmt = let_decl_stmt_analyse();
                return new decl_stmtAst(let_decl_stmt);
            } else {
                const_decl_stmtAst const_decl_stmt = const_decl_stmt_analyse();
                return new decl_stmtAst(const_decl_stmt);
            }
        }
        return null;
    }

    // if_stmt -> 'if' condition block_stmt ('else' 'if' condition block_stmt)*
    // ('else' block_stmt)?
    public if_stmtAst if_stmt_analyse() {
        Token tk1 = nextToken();
        conditionAst condition_if = null;
        block_stmtAst block_stmt_if = null;
        ArrayList<conditionAst> condition_else_if = null;
        ArrayList<block_stmtAst> block_stmt_else_if = null;
        block_stmtAst block_stmt_else = null;
        if (tk1.getType() == Token.tokentype.IF_KW) {
            condition_if = condition_analyse();
            if (condition_if == null)
                System.exit(1);
            block_stmt_if = block_stmt_analyse();
            if (block_stmt_if == null)
                System.exit(1);
        }
        Token tk2 = nextToken();
        Token tk3 = nextToken();
        if (tk2.getType() == Token.tokentype.ELSE_KW && tk3.getType() == Token.tokentype.IF_KW) {
            condition_else_if = new ArrayList<conditionAst>();
            block_stmt_else_if = new ArrayList<block_stmtAst>();
            while (true) {
                conditionAst condition_temp = condition_analyse();
                if (condition_temp == null)
                    System.exit(1);
                condition_else_if.add(condition_temp);
                block_stmtAst block_temp = block_stmt_analyse();
                if (block_temp == null)
                    System.exit(1);
                block_stmt_else_if.add(block_temp);
                tk2 = nextToken();
                tk3 = nextToken();
                if (!(tk2.getType() == Token.tokentype.ELSE_KW && tk3.getType() == Token.tokentype.IF_KW)) {
                    unreadToken();
                    unreadToken();
                    break;
                }
            }
        } else {
            if(tk2 != null)
                unreadToken();
            if(tk3 != null)
                unreadToken();
        }
        Token tk4 = nextToken();
        if (tk4.getType() == Token.tokentype.ELSE_KW) {
            block_stmt_else = block_stmt_analyse();
            if (block_stmt_else == null)
                System.exit(1);
        } else {
            unreadToken();
        }
        return new if_stmtAst(condition_if, block_stmt_if, condition_else_if, block_stmt_else_if, block_stmt_else);
    }

    // while_stmt -> 'while' condition block_stmt
    public while_stmtAst while_stmt_analyse() {
        Token tk = nextToken();
        if (tk.getType() != Token.tokentype.WHILE_KW) {
            System.exit(1);
        }
        conditionAst condition = condition_analyse();
        if (condition == null)
            System.exit(1);
        block_stmtAst block_stmt = block_stmt_analyse();
        if (block_stmt == null)
            System.exit(1);
        return new while_stmtAst(condition, block_stmt);
    }

    // break_stmt -> 'break' ';'
    public break_stmtAst break_stmt_analyse() {
        Token tk = nextToken();
        if (tk.getType() != Token.tokentype.BREAK_KW) {
            System.exit(1);
        }
        break_stmtAst break_stmt = new break_stmtAst("break");
        tk = nextToken();
        if (tk.getType() != Token.tokentype.SEMICOLON)
            System.exit(1);
        return break_stmt;
    }

    // continue_stmt -> 'continue' ';'
    public continue_stmtAst continue_stmt_analyse() {
        Token tk = nextToken();
        if (tk.getType() != Token.tokentype.CONTINUE_KW) {
            System.exit(1);
        }
        continue_stmtAst continue_stmt = new continue_stmtAst("continue");
        tk = nextToken();
        if (tk.getType() != Token.tokentype.SEMICOLON)
            System.exit(1);
        return continue_stmt;
    }

    // return_stmt -> 'return' operator_expr? ';'
    public return_stmtAst return_stmt_analyse() {
        Token tk = nextToken();
        if (tk.getType() != Token.tokentype.RETURN_KW) {
            System.exit(1);
        }
        tk = nextToken();
        if (tk.getType() == Token.tokentype.SEMICOLON) {
            return new return_stmtAst();
        }
        unreadToken();
        operator_exprAst expr_stmt = operator_expr_analyse();
        tk = nextToken();
        if(tk.getType() != Token.tokentype.SEMICOLON)
            System.exit(1);
        return new return_stmtAst(expr_stmt);
    }

    // block_stmt -> '{' stmt* '}'
    public block_stmtAst block_stmt_analyse() {
        ArrayList<stmtAst> stmts = new ArrayList<stmtAst>();
        Token tk = nextToken();
        if (tk.getType() != Token.tokentype.L_BRACE) {
            System.exit(1);
        }
        stmtAst stmt = stmt_analyse();
        if (stmt == null) {
            tk = nextToken();
            if (tk.getType() == Token.tokentype.R_BRACE)
                return new block_stmtAst();
            System.exit(1);
        } else {
            while (true) {
                stmts.add(stmt);
                stmt = stmt_analyse();
                if (stmt == null)
                    break;
            }
        }
        tk = nextToken();
        if (tk.getType() != Token.tokentype.R_BRACE)
            System.exit(1);
        return new block_stmtAst(stmts);
    }

    // empty_stmt -> ';'
    public empty_stmtAst empty_stmt_analyse() {
        Token tk = nextToken();
        if (tk.getType() == Token.tokentype.SEMICOLON) {
            return new empty_stmtAst(";");
        }
        System.exit(1);
        return null;
    }

    // function_param -> 'const'? IDENT ':' ty
    public function_paramAst function_param_analyse() {
        Token tk1 = nextToken();
        if (tk1.getType() == Token.tokentype.CONST_KW) {
            Token tk2 = nextToken();
            Token tk3 = nextToken();
            Token tk4 = nextToken();
            if (tk2.getType() == Token.tokentype.IDENT && tk3.getType() == Token.tokentype.SEMICOLON
                    && (tk4.getType() == Token.tokentype.INT || tk4.getType() == Token.tokentype.DOUBLE)) {
                varia var_new = new varia(tk2.getValue(),tk4.getValue(),false);
                function_var.add(var_new);
                return new function_paramAst("const", tk2.getValue(), tk4.getValue());
            }
        } else if (tk1.getType() == Token.tokentype.IDENT) {
            Token tk2 = nextToken();
            Token tk3 = nextToken();
            if (tk2.getType() == Token.tokentype.COLON
                    && (tk3.getType() == Token.tokentype.INT || tk3.getType() == Token.tokentype.DOUBLE)) {
                varia var_new = new varia(tk1.getValue(),tk3.getValue(),false);
                function_var.add(var_new);
                return new function_paramAst(tk1.getValue(), tk3.getValue());
            }
        }
        System.exit(1);
        return null;
    }

    // function_param_list -> function_param (',' function_param)*
    public function_param_listAst function_param_list_analyse() {
        ArrayList<function_paramAst> function_params = new ArrayList<function_paramAst>();
        function_paramAst function_param1 = null;
        int flag = 0;
        while (true) {
            if(flag==0)
                function_param1 = function_param_analyse();
            Token tk = nextToken();
            if (tk.getType() == Token.tokentype.R_PAREN && flag == 0) {
                unreadToken();
                return new function_param_listAst(function_param1);
            } else if (tk.getType() == Token.tokentype.R_PAREN && flag == 1) {
                unreadToken();
                return new function_param_listAst(function_param1, function_params);
            }
            unreadToken();
            tk = nextToken();
            if (tk.getType() == Token.tokentype.COMMA) {
                function_paramAst function_param = function_param_analyse();
                function_params.add(function_param);
            } else {
                System.exit(1);
            }
            flag = 1;
        }
    }

    // function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
    public functionAst function_analyse() {
        Token tk1 = nextToken();
        Token tk2 = nextToken();
        Token tk3 = nextToken();
        if (tk1.getType() == Token.tokentype.FN_KW && tk2.getType() == Token.tokentype.IDENT
                && tk3.getType() == Token.tokentype.L_PAREN) {
            if(is_function(tk2.getValue()))
                System.exit(1);
            Token tk4 = nextToken();
            if (tk4.getType() == Token.tokentype.R_PAREN) {
                Token tk5 = nextToken();
                Token tk6 = nextToken();
                if (tk5.getType() == Token.tokentype.ARROW && (tk6.getType() == Token.tokentype.INT
                        || tk6.getType() == Token.tokentype.DOUBLE || tk6.getType() == Token.tokentype.VOID)) {
                    varia func = new varia(tk2.getValue(),tk6.getValue(),true);
                    this.function_arry.add(func);
                    block_stmtAst block_stmt = block_stmt_analyse();
                    if (block_stmt == null)
                        System.exit(1);
                    function_var = new ArrayList<varia>();
                    return new functionAst(tk2.getValue(), tk6.getValue(), block_stmt);
                }
            } else {
                unreadToken();
                function_param_listAst function_param_list = function_param_list_analyse();
                if (function_param_list == null)
                    System.exit(1);
                Token help = nextToken();
                Token tk5 = nextToken();
                Token tk6 = nextToken();
                if (tk5.getType() == Token.tokentype.ARROW && (tk6.getType() == Token.tokentype.INT
                        || tk6.getType() == Token.tokentype.DOUBLE || tk6.getType() == Token.tokentype.VOID)) {
                    varia func = new varia(tk2.getValue(),tk5.getValue(),true);
                    this.function_arry.add(func);
                    block_stmtAst block_stmt = block_stmt_analyse();
                    if (block_stmt == null)
                        System.exit(1);
                    function_var = new ArrayList<varia>();
                    return new functionAst(tk2.getValue(), function_param_list, tk6.getValue(), block_stmt);
                }
            }
        }
        System.exit(1);
        function_var = new ArrayList<varia>();
        return null;
    }

    // item -> function | decl_stmt
    public itemAst item_analyse() {
        Token tk = nextToken();
        if (tk.getType() == Token.tokentype.FN_KW) {
            unreadToken();
            functionAst function = function_analyse();
            if (function == null)
                System.exit(1);
            return new itemAst(function);
        } else if (tk.getType() == Token.tokentype.LET_KW || tk.getType() == Token.tokentype.CONST_KW) {
            unreadToken();
            decl_stmtAst decl_stmt = decl_stmt_analyse();
            if (decl_stmt == null)
                System.exit(1);
            return new itemAst(decl_stmt);
        }
        return null;
    }

    // program -> item*
    public programAst program_analyse() {
        ArrayList<itemAst> items = new ArrayList<itemAst>();
        Token tk = nextToken();
        int i = 0;
        while (tk != null) {
            unreadToken();
            itemAst item;
            item = item_analyse();
            if (item != null)
                items.add(item);
            else
                System.exit(1);
            tk = nextToken();
            i++;
        }
        return new programAst(items);
    }

    public Token nextToken() {
        if (offset == tokens.size())
            return null;
        currentpos = tokens.get(offset).getEndPos();
        return tokens.get(offset++);
    }

    public void unreadToken() {
        if (offset == 0)
            System.exit(1);
        currentpos = tokens.get(offset - 1).getStartPos();
        offset--;
    }

    public boolean is_variabale(String name) {
        if (function_arry.size() != 0) {
            for (int i = localVariable_arry.size() - 1; i >= 0; i--) {
                varia this_one = localVariable_arry.get(i);
                if (this_one.name.equals(name))
                    if(!this_one.is_const)
                        return true;
            }
        }
        for (int i = 0; i < globalVariable_arry.size(); i++) {
            varia this_one = globalVariable_arry.get(i);
            if (this_one.name.equals(name))
                if(!this_one.is_const)
                    return true;
        }
        for (int i = 0; i < function_var.size(); i++) {
            varia this_one = function_var.get(i);
            if (this_one.name.equals(name))
                if(!this_one.is_const)
                    return true;
        }

        return false;
    }

    public boolean is_function(String name) {
        if(function_arry.size() == 0)
            return false;
        for (int i = function_arry.size() - 1; i >= 0; i--) {
            varia this_one = function_arry.get(i);
            if (this_one.name.equals(name))
                return true;
        }

        return false;
    }

    public Analyser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }
}
