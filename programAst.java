import java.util.*;

//expr ->
//      assign_expr
class exprAst extends Ast {
    assign_exprAst statement;

    public exprAst(assign_exprAst statement) {
        this.statement = statement;
    }

    public String generate(int level) {
        String res;
        res = statement.generate(level);
        res = "void";
        return res;
    }
}

// assign_expr -> l_expr '=' operator_expr
class assign_exprAst extends Ast {
    String l_expr;
    operator_exprAst expr_back;

    public assign_exprAst(String l_expr, operator_exprAst expr_back) {
        this.l_expr = l_expr;
        this.expr_back = expr_back;
    }

    public String generate(int level) {
        Order loadOrder;
        Function currentFunction = Functionarrary.getFunctionTable().getCurrentFuction();
        String res0 = "void", res1 = "void", res2 = "void";

        if (currentFunction.is_variable(l_expr)) {
            if (currentFunction.is_const(l_expr, level))
                System.exit(1);
            int index = currentFunction.get_index_variables(l_expr, level);
            if(index == -1)
                System.exit(1);
            loadOrder = new Order("loca", level);
            loadOrder.addOper((long) index);
            Variable current_var = currentFunction.get_variable(l_expr, level);
            if(current_var == null)
                System.exit(1);
            res1 = currentFunction.get_variable(l_expr, level).get_type();
        }
        else if(currentFunction.is_parameters(l_expr)){
            if (currentFunction.is_parameter_const(l_expr))
                System.exit(1);
            int index = currentFunction.get_index_parameter(l_expr);
            if(index == -1)
                System.exit(1);
            loadOrder = new Order("arga", level);
            loadOrder.addOper((long) index);
            Variable current_var = currentFunction.get_parameter(l_expr);
            if(current_var == null)
                System.exit(1);
            res1 = currentFunction.get_parameter(l_expr).get_type();
        }
        else{
            if (!startcode.getStartCodeTable().is_variable(l_expr))
                System.exit(1);
            if (startcode.getStartCodeTable().is_const(l_expr))
                System.exit(1);
            int index = startcode.getStartCodeTable().get_index(l_expr);
            loadOrder = new Order("globa", level);
            loadOrder.addOper((long) index);
            res1 = startcode.getStartCodeTable().get_variable(l_expr).get_type();
        }

        Functionarrary.getFunctionTable().getCurrentFuction().addorders(loadOrder);
        res2 = expr_back.generate(level);
        if (!res1.equals(res2))
            System.exit(1);
        res0 = res1;
        Order Store = new Order("store.64", level);
        Functionarrary.getFunctionTable().getCurrentFuction().addorders(Store);
        return "void";
    }
}

// binary_operator -> '+' | '-'
// operator_expr -> multiplicative_expr (binary_operator multiplicative_expr)*
class operator_exprAst extends Ast {
    multiplicative_exprAst multiplicativeExpressionL;
    ArrayList<String> additiveOperator = new ArrayList<String>();
    ArrayList<multiplicative_exprAst> operator_exprnR = new ArrayList<>();

    public operator_exprAst(multiplicative_exprAst multiplicativeExpressionL, ArrayList<String> additiveOperator,
                            ArrayList<multiplicative_exprAst> operator_exprnR) {
        this.multiplicativeExpressionL = multiplicativeExpressionL;
        this.additiveOperator = additiveOperator;
        this.operator_exprnR = operator_exprnR;
    }

    public operator_exprAst(multiplicative_exprAst multiplicativeExpressionL) {
        this.multiplicativeExpressionL = multiplicativeExpressionL;
    }

    public String generate(int level) {
        String res1 = "void";
        String res2 = "void";
        String res3 = "void";

        res2 = multiplicativeExpressionL.generate(level);
        int len = additiveOperator.size();
        if(len == 0)
            return res2;
        else{
            for(int i=0;i<len;i++)
            {
                res3 = operator_exprnR.get(i).generate(level);
                res1 = res2;
                Order order = new Order();
                String addOperator = additiveOperator.get(i);
                if (!res2.equals(res3)) {
                    System.exit(1);
                }
                if (res2.equals("int") && addOperator.equals("+")) {
                    order.setOpcode("add.i");
                    order.setlevel(level);
                } else if (res2.equals("int") && addOperator.equals("-")) {
                    order.setOpcode("sub.i");
                    order.setlevel(level);
                } else if (res2.equals("double") && addOperator.equals("+")) {
                    order.setOpcode("add.f");
                    order.setlevel(level);
                } else if (res2.equals("double") && addOperator.equals("-")) {
                    order.setOpcode("sub.f");
                    order.setlevel(level);
                }
                if (level == 0)// GLOBAL
                        startcode.getStartCodeTable().orders.add(order);
                else// LOCAL
                    Functionarrary.getFunctionTable().getCurrentFuction().addorders(order);
            }
        }
        return res1;
    }
}

// relational-operator -> '==' | '!=' | '<' | '>' | '<=' | '>='
// condition ->
// operator_expr[relational-operator operator_expr]
class conditionAst extends Ast {
    operator_exprAst expr_front;
    String binary_operator;
    operator_exprAst expr_back;

    public conditionAst(operator_exprAst expr_front, String binary_operator, operator_exprAst expr_back) {
        this.expr_front = expr_front;
        this.expr_back = expr_back;
        this.binary_operator = binary_operator;
    }

    public conditionAst(operator_exprAst expr_front) {
        this.expr_front = expr_front;
    }

    public String generate(int level) {
        String res0 = "void", res1 = "void";
        String res2 = "void";
        String res3 = "void";
        res2 = expr_front.generate(level);
        if (expr_back == null) {
            return "boolean";
        }
        res3 = expr_back.generate(level);
        if (!((res2.equals(res3)
                || (res2.equals("int") && res3.equals("char")))
            || (res2.equals("char") && res3.equals("int"))))
            System.exit(1);
        res1 = res2;
        ArrayList<Order> Calculation = new ArrayList<>();
        if (res1.equals("int") && binary_operator.equals("==")) {
            Order cmp = new Order("cmp.i", level);
            Order not = new Order("not", level);
            Calculation.add(cmp);
            Calculation.add(not);
            res0 = "int";
        } else if (res1.equals("int") && binary_operator.equals("!=")) {
            Order cmp = new Order("cmp.i", level);
            Calculation.add(cmp);
            res0 = "int";
        } else if (res1.equals("int") && binary_operator.equals("<")) {
            Order cmp = new Order("cmp.i", level);
            Order setlt = new Order("set.lt", level);
            Calculation.add(cmp);
            Calculation.add(setlt);
            res0 = "int";
        } else if (res1.equals("int") && binary_operator.equals(">")) {
            Order cmp = new Order("cmp.i", level);
            Order setgt = new Order("set.gt", level);
            Calculation.add(cmp);
            Calculation.add(setgt);
            res0 = "int";
        } else if (res1.equals("int") && binary_operator.equals("<=")) {
            Order cmp = new Order("cmp.i", level);
            Order setgt = new Order("set.gt", level);
            Order not = new Order("not", level);
            Calculation.add(cmp);
            Calculation.add(setgt);
            Calculation.add(not);
            res0 = "int";
        } else if (res1.equals("int") && binary_operator.equals(">=")) {
            Order cmp = new Order("cmp.i", level);
            Order setlt = new Order("set.lt", level);
            Order not = new Order("not", level);
            Calculation.add(cmp);
            Calculation.add(setlt);
            Calculation.add(not);
            res0 = "int";
        } else if (res1.equals("double") && binary_operator.equals("==")) {
            Order cmp = new Order("cmp.f", level);
            Order not = new Order("not", level);
            Calculation.add(cmp);
            Calculation.add(not);
            res0 = "int";
        } else if (res1.equals("double") && binary_operator.equals("!=")) {
            Order cmp = new Order("cmp.f", level);
            Calculation.add(cmp);
            res0 = "int";
        } else if (res1.equals("double") && binary_operator.equals("<")) {
            Order cmp = new Order("cmp.f", level);
            Order setlt = new Order("set.lt", level);
            Calculation.add(cmp);
            Calculation.add(setlt);
            res0 = "double";
        } else if (res1.equals("double") && binary_operator.equals(">")) {
            Order cmp = new Order("cmp.i", level);
            Order setgt = new Order("set.gt", level);
            Calculation.add(cmp);
            Calculation.add(setgt);
            res0 = "double";
        } else if (res1.equals("double") && binary_operator.equals("<=")) {
            Order cmp = new Order("cmp.f", level);
            Order setgt = new Order("set.gt", level);
            Order not = new Order("not", level);
            Calculation.add(cmp);
            Calculation.add(setgt);
            Calculation.add(not);
            res0 = "double";
        } else if (res1.equals("double") && binary_operator.equals(">=")) {
            Order cmp = new Order("cmp.f", level);
            Order setlt = new Order("set.lt", level);
            Order not = new Order("not", level);
            Calculation.add(cmp);
            Calculation.add(setlt);
            Calculation.add(not);
            res0 = "double";
        }
        int len = Calculation.size();
        for (int i = 0; i < len; i++) {
            Order this_order = Calculation.get(i);
            Functionarrary.getFunctionTable().getCurrentFuction().addorders(this_order);
        }
        return "boolean";
    }
}

// multiplicative_operator -> '*' | '/'
// multiplicative_expr ->
// as_expr (multiplicative_operator as_expr)*
class multiplicative_exprAst extends Ast {
    as_exprAst unaryExpressionL;
    ArrayList<String> mulOperator = new ArrayList<>();
    ArrayList<as_exprAst> unaryExpressionR = new ArrayList<>();

    public multiplicative_exprAst(as_exprAst unaryExpressionL, ArrayList<String> mulOperator,
                                  ArrayList<as_exprAst> unaryExpressionR) {
        this.unaryExpressionL = unaryExpressionL;
        this.mulOperator = mulOperator;
        this.unaryExpressionR = unaryExpressionR;
    }

    public multiplicative_exprAst(as_exprAst unaryExpressionL) {
        this.unaryExpressionL = unaryExpressionL;
    }

    public String generate(int level) {
        String res0 = "void", res1 = "void", res2 = "void";

        res1 = unaryExpressionL.generate(level);
        int len = mulOperator.size();
        if(len == 0)
            return res1;
        for(int i=0;i<len;i++){
            res2 = unaryExpressionR.get(i).generate(level);
            res0 = res1;
            String mulop = mulOperator.get(i);
            Order order = new Order();
            if (!res2.equals(res1))
                System.exit(1);
            if (res2.equals("int") && mulop.equals("*")) {
                order.setOpcode("mul.i");
                order.setlevel(level);
            } else if (res2.equals("int") && mulop.equals("/")) {
                order.setOpcode("div.i");
                order.setlevel(level);
            } else if (res2.equals("double") && mulop.equals("*")) {
                order.setOpcode("mul.f");
                order.setlevel(level);
            } else if (res2.equals("double") && mulop.equals("/")) {
                order.setOpcode("div.f");
                order.setlevel(level);
            }
            if (level == 0)// GLOBAL
                startcode.getStartCodeTable().orders.add(order);
            else// LOCAL
                Functionarrary.getFunctionTable().getCurrentFuction().addorders(order);
        }
        return res0;
    }
}

// as_expr -> primary_expr {'as' ty}
class as_exprAst extends Ast {
    primary_exprAst l_expr;
    ArrayList<String> ty;

    public as_exprAst(primary_exprAst l_expr, ArrayList<String> ty) {
        this.l_expr = l_expr;
        this.ty = ty;
    }

    public as_exprAst(primary_exprAst l_expr) {
        this.l_expr = l_expr;
    }

    public String generate(int level) {
        String res;
        res = l_expr.generate(level);
        if (ty == null)
            return res;
        for(int i=0;i<ty.size();i++){
            Order Change = new Order();
            if (ty.get(i).equals("double"))
                Change = new Order("itof", level);
            else if (ty.get(i).equals("int"))
                Change = new Order("ftoi", level);
            else
                System.exit(1);
            Functionarrary.getFunctionTable().getCurrentFuction().addorders(Change);
        }
        return ty.get(ty.size()-1);
    }
}

// primary_expr ->
// negate_expr
// | call_expr
// | group_expr
// | ident_expr
// | literal_expr
class primary_exprAst extends Ast {
    Ast primary_expr;

    public primary_exprAst(Ast primary_expr) {
        this.primary_expr = primary_expr;
    }

    public String generate(int level) {
        String res = "void";
        if (primary_expr instanceof negate_exprAst) {
            negate_exprAst negate_expr = (negate_exprAst) primary_expr;
            res = negate_expr.generate(level);
        } else if (primary_expr instanceof call_exprAst) {
            call_exprAst call_expr = (call_exprAst) primary_expr;
            res = call_expr.generate(level);
        } else if (primary_expr instanceof group_exprAst) {
            group_exprAst group_expr = (group_exprAst) primary_expr;
            res = group_expr.generate(level);
        } else if (primary_expr instanceof ident_exprAst) {
            ident_exprAst ident_expr = (ident_exprAst) primary_expr;
            res = ident_expr.generate(level);
        } else if (primary_expr instanceof literal_exprAst) {
            literal_exprAst literal_expr = (literal_exprAst) primary_expr;
            res = literal_expr.generate(level);
        }
        return res;
    }
}

// negate_expr -> '-' primary_expr
class negate_exprAst extends Ast {
    primary_exprAst expr_back;

    public negate_exprAst(primary_exprAst expr_back) {
        this.expr_back = expr_back;
    }

    public String generate(int level) {
        String res = "void";
        res = expr_back.generate(level);
        Order order = new Order();
        if (res.equals("int")) {
            order = new Order("neg.i", level);
        } else if (res.equals("double")) {
            order = new Order("neg.f", level);
        } else {
            System.exit(1);
        }

        if (level == 0)
            startcode.getStartCodeTable().orders.add(order);
        else
            Functionarrary.getFunctionTable().getCurrentFuction().addorders(order);
        return res;
    }
}

// call_param_list -> operator_expr (',' operator_expr)*
class call_param_listAst extends Ast {
    public ArrayList<operator_exprAst> exprs;
    String value;

    public call_param_listAst(ArrayList<operator_exprAst> exprs) {
        this.exprs = exprs;
    }

    // public call_param_listAst(String value) {
    // this.value = value;
    // }

    public String generate(int level) {
        int i = 0, len = exprs.size();
        String res = "void";
        // if(value != null){
        // Variable new_variable = new Variable("String", this.value, true, true,
        // level);
        // int index = startcode.getStartCodeTable().variables.size();
        // startcode.getStartCodeTable().variables.add(new_variable);
        // Order push = new Order("push",level);
        // push.addOper((long)index);
        // Functionarrary.getFunctionTable().getCurrentFuction().addorders(push);
        // return "void";
        // }
        while (i < len) {
            res = exprs.get(i).generate(level);
            i++;
        }
        return "void";
    }
}

// call_expr -> IDENT '(' call_param_list? ')'
class call_exprAst extends Ast {
    String ident;
    call_param_listAst call_param_list;

    public call_exprAst(String ident, call_param_listAst call_param_list) {
        this.ident = ident;
        this.call_param_list = call_param_list;
    }

    public call_exprAst(String ident) {
        this.ident = ident;
    }

    public String generate(int level) {
        if (ident.equals("getint") || ident.equals("getdouble") || ident.equals("getchar") || ident.equals("putln")) {
            Order stackalloc = new Order("stackalloc", level);
            if(ident.equals("putln"))
                stackalloc.addOper(0L);
            else
                stackalloc.addOper(1L);
            Functionarrary.getFunctionTable().getCurrentFuction().addorders(stackalloc);
            int index = startcode.getStartCodeTable().variables.size();
            Variable this_var = new Variable("string", ident, true, true, 0);
            startcode.getStartCodeTable().variables.add(this_var);
            Order call = new Order("callname", level);
            call.addOper((long) index);

            Functionarrary.getFunctionTable().getCurrentFuction().addorders(call);
            if (ident.equals("getint") || ident.equals("getchar"))
                return "int";
            if (ident.equals("getdouble"))
                return "double";
            else
                return "void";
        }
        if (ident.equals("putchar") || ident.equals("putint")
                || ident.equals("putstr") || ident.equals("putdouble")) {
            Order stackalloc = new Order("stackalloc", level);
            stackalloc.addOper(0L);
            Functionarrary.getFunctionTable().getCurrentFuction().addorders(stackalloc);
            String res = call_param_list.generate(level);
            int index = startcode.getStartCodeTable().variables.size();
            Variable this_var = new Variable("string", ident, true, true, 0);
            startcode.getStartCodeTable().variables.add(this_var);
            Order call = new Order("callname", level);
            call.addOper((long) index);
            Functionarrary.getFunctionTable().getCurrentFuction().addorders(call);
            return "void";
        }

        if (!Functionarrary.getFunctionTable().is_function(ident))
            System.exit(1);

        String res = "void";
        res = Functionarrary.getFunctionTable().get_function(ident).get_type();

        int function_return_slots = Functionarrary.getFunctionTable().get_function(ident).get_return_slots();
        Order stackalloc = new Order("stackalloc", level);
        stackalloc.addOper((long) function_return_slots);
        Functionarrary.getFunctionTable().getCurrentFuction().addorders(stackalloc);

        int function_index = constantarray.getConstantTable().get_index(ident);
        if (function_index == -1)
            System.exit(1);
        if (call_param_list != null)
            res = call_param_list.generate(level);
        Order call = new Order("call", level);
        call.addOper((long) function_index);
        Functionarrary.getFunctionTable().getCurrentFuction().addorders(call);

        Function this_func = Functionarrary.getFunctionTable().get_function(this.ident);
        res = this_func.type;

        return res;

    }
}

// literal_expr -> UINT_LITERAL | DOUBLE_LITERAL | STRING_LITERAL | CHAR_LITERAL
class literal_exprAst extends Ast {
    String LITERAL;

    public literal_exprAst(String LITERAL) {
        this.LITERAL = LITERAL;
    }

    public String judge() {
        String res = "void";
        int len = this.LITERAL.length();
        if (len == 2 && this.LITERAL.charAt(0)=='\'') {
            res = "char";
            return res;
        }
        if (len == 3 && this.LITERAL.charAt(0)=='\'' && this.LITERAL.charAt(1)=='\\') {
            res = "char";
            return res;
        }
        if (len >= 1 && !Character.isDigit(this.LITERAL.charAt(0))) {
            res = "string";
            return res;
        }

        int flag = 0, sum_char = 0;
        res = "int";
        for (int i = 0; i < len; i++) {
            if (Character.isDigit(this.LITERAL.charAt(i)) && flag <= 1)
                continue;
            else if (this.LITERAL.charAt(i) == '.') {
                flag = 1;
                res = "double";
            } else if (Character.isAlphabetic(this.LITERAL.charAt(0))) {
                flag = 2;
                res = "string";
            }

        }
        return res;
    }

    public String generate(int level) {
        String res = judge();
        if (res.equals("int")) {
            Order push = new Order("push", level);
            push.addOper(Long.parseLong(this.LITERAL));
            if (level > 0)
                Functionarrary.getFunctionTable().getCurrentFuction().addorders(push);
            else
                startcode.getStartCodeTable().orders.add(push);
        } else if (res.equals("double")) {
            Order push = new Order("push", level);
            double value = Double.parseDouble(this.LITERAL);
            long l = Double.doubleToLongBits(value);
            push.addOper(l);
            if (level > 0)
                Functionarrary.getFunctionTable().getCurrentFuction().addorders(push);
            else
                startcode.getStartCodeTable().orders.add(push);
        } else if (res.equals("char")) {
            Order push = new Order("push", level);
            char ch ;
            if(this.LITERAL.length()==2){
                ch = this.LITERAL.charAt(1);
            }
            else {
                ch = this.LITERAL.charAt(2);
                if(ch == 'b')
                    ch += '\b';
                else if(ch == 'f')
                    ch += '\f';
                else if(ch == 'r')
                    ch += '\r';
                else if(ch == 'n')
                    ch += '\n';
                else if(ch == 't')
                    ch += '\t';
                else if(ch == '\"')
                    ch += '\"';
                else if(ch == '\'')
                    ch += '\'';
                else if(ch == '\\')
                    ch += '\\';
            }
            int value = Integer.valueOf(ch);
            push.addOper((long) value);
            if (level > 0)
                Functionarrary.getFunctionTable().getCurrentFuction().addorders(push);
            else
                startcode.getStartCodeTable().orders.add(push);
        } else if (res.equals("string")) {
            int len = startcode.getStartCodeTable().variables.size();
            String help_string = "";
            int str_len = this.LITERAL.length();
            for(int p = 0;p<str_len;p++){
                char ch = this.LITERAL.charAt(p);
                if(ch == '\\'){
                    char temp = this.LITERAL.charAt(p+1);
                    if(temp == 'b')
                        help_string += '\b';
                    else if(temp == 'f')
                        help_string += '\f';
                    else if(temp == 'r')
                        help_string += '\r';
                    else if(temp == 'n')
                        help_string += '\n';
                    else if(temp == 't')
                        help_string += '\t';
                    else if(temp == '\"')
                        help_string += '\"';
                    else if(temp == '\'')
                        help_string += '\'';
                    else if(temp == '\\')
                        help_string += '\\';
                    p++;
                }
                else
                    help_string += ch;
            }
            Variable new_constant = new Variable(res, help_string, true, false, 0);
            startcode.getStartCodeTable().variables.add(new_constant);
            Order push = new Order("push", level);
            push.addOper((long) len);
            if (level > 0)
                Functionarrary.getFunctionTable().getCurrentFuction().addorders(push);
            else
                startcode.getStartCodeTable().orders.add(push);
        }
        if(res.equals("char"))
            res="int";
        return res;
    }
}

// ident_expr -> IDENT
class ident_exprAst extends Ast {
    String ident;

    public ident_exprAst(String ident) {
        this.ident = ident;
    }

    public String generate(int level) {
        String res = "void";
        Order loadOrder;
        Function currentFunction = Functionarrary.getFunctionTable().getCurrentFuction();
        String res1 = "void";

        if (currentFunction.is_variable(this.ident)) {
            loadOrder = new Order("loca", level);
            int index = currentFunction.get_index_variables(this.ident, level);
            if(index == -1)
                System.exit(1);
            loadOrder.addOper((long) index);
            Variable current_var = currentFunction.get_variable(this.ident, level);
            if(current_var == null)
                System.exit(1);
            res1 = currentFunction.get_variable(this.ident, level).get_type();
            res = res1;
        }
        else if(currentFunction.is_parameters(this.ident)){
            loadOrder = new Order("arga", level);
            int index = currentFunction.get_index_parameter(this.ident);
            if(index == -1)
                System.exit(1);
            loadOrder.addOper((long) index);
            Variable current_var = currentFunction.get_parameter(this.ident);
            if(current_var == null)
                System.exit(1);
            res1 = current_var.get_type();
            res = res1;
        }
        else {
            if (!startcode.getStartCodeTable().is_variable(this.ident))
                System.exit(1);
            loadOrder = new Order("globa", level);
            loadOrder.addOper((long) startcode.getStartCodeTable().get_index(ident));
            Variable current_var = startcode.getStartCodeTable().get_variable(this.ident);
            if(current_var == null)
                System.exit(1);
            res1 = current_var.get_type();
            res = res1;
        }
        Functionarrary.getFunctionTable().getCurrentFuction().addorders(loadOrder);
        Order load = new Order("load.64", level);
        Functionarrary.getFunctionTable().getCurrentFuction().addorders(load);

        return res;
    }
}

// group_expr -> '(' operator_exprAst ')'

class group_exprAst extends Ast {
    operator_exprAst expr;

    public group_exprAst(operator_exprAst expr) {
        this.expr = expr;
    }

    public String generate(int level) {
        String res = "void";
        res = this.expr.generate(level);
        return res;
    }

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
class stmtAst extends Ast {
    Ast stmtAst;

    public stmtAst(Ast stmtAst) {
        this.stmtAst = stmtAst;
    }

    public String generate(int level) {
        String res = "";

        if (stmtAst instanceof expr_stmtAst) {
            expr_stmtAst expr_stmt = (expr_stmtAst) stmtAst;
            res = expr_stmt.generate(level);
        } else if (stmtAst instanceof decl_stmtAst) {
            decl_stmtAst decl_stmt = (decl_stmtAst) stmtAst;
            res = decl_stmt.generate(level);
        } else if (stmtAst instanceof if_stmtAst) {
            if_stmtAst if_stmt = (if_stmtAst) stmtAst;
            res = if_stmt.generate(level);
        } else if (stmtAst instanceof while_stmtAst) {
            while_stmtAst while_stmt = (while_stmtAst) stmtAst;
            res = while_stmt.generate(level);
        } else if (stmtAst instanceof break_stmtAst) {
            break_stmtAst break_stmt = (break_stmtAst) stmtAst;
            res = break_stmt.generate(level);
        } else if (stmtAst instanceof continue_stmtAst) {
            continue_stmtAst continue_stmt = (continue_stmtAst) stmtAst;
            res = continue_stmt.generate(level);
        } else if (stmtAst instanceof return_stmtAst) {
            return_stmtAst return_stmt = (return_stmtAst) stmtAst;
            res = return_stmt.generate(level);
        } else if (stmtAst instanceof block_stmtAst) {
            block_stmtAst block_stmt = (block_stmtAst) stmtAst;
            res = block_stmt.generate(level);
        } else if (stmtAst instanceof empty_stmtAst) {
            empty_stmtAst empty_stmt = (empty_stmtAst) stmtAst;
            res = empty_stmt.generate(level);
        } else {
            System.exit(1);
        }
        return res;
    }
}

// expr_stmt -> expr|operator_expr ';'
class expr_stmtAst extends Ast {
    Ast expr;

    public expr_stmtAst(Ast expr) {
        this.expr = expr;
    }


    public String generate(int level) {
        String res = "void";
        if(expr instanceof exprAst){
            exprAst this_expr = (exprAst)expr;
            res = this_expr.generate(level);
        }
        else if(expr instanceof operator_exprAst){
            operator_exprAst this_expr = (operator_exprAst)expr;
            res = this_expr.generate(level);
        }
        else
            System.exit(1);
        return res;
    }
}

// let_decl_stmt -> 'let' IDENT ':' ty ('=' operator_expr)? ';'
class let_decl_stmtAst extends Ast {
    String ident;
    String ty;
    operator_exprAst expr;

    public let_decl_stmtAst(String ident, String ty, operator_exprAst expr) {
        this.expr = expr;
        this.ty = ty;
        this.ident = ident;
    }

    public let_decl_stmtAst(String ident, String ty) {
        this.ident = ident;
        this.ty = ty;
    }

    public String generate(int level) {
        String res = "void", res1 = "void";
        if (this.ty.equals("int"))
            res = "int";
        else if (this.ty.equals("double"))
            res = "double";
        else
            System.exit(1);
        Variable new_variable = new Variable();
        if (expr != null) {
            new_variable = new Variable(this.ty, this.ident, false, true, level);
            if (level > 0) {
                Functionarrary.getFunctionTable().getCurrentFuction().variables.add(new_variable);
            } else{
                startcode.getStartCodeTable().variables.add(new_variable);
            }
            if (level > 0) {
                Function currentFunction = Functionarrary.getFunctionTable().getCurrentFuction();
                int index = currentFunction.get_index_variables(this.ident, level);
                if(index == -1)
                    System.exit(1);
                Order loca = new Order("loca", level);
                loca.addOper((long) index);
                currentFunction.addorders(loca);
                res1 = expr.generate(level);
                if (!res.equals(res1))
                    System.exit(1);
                Order store = new Order("store.64", level);
                currentFunction.addorders(store);
            } else if (level == 0) {
                int index = startcode.getStartCodeTable().get_index(this.ident);
                Order globa = new Order("globa", level);
                globa.addOper((long) index);
                startcode.getStartCodeTable().orders.add(globa);
                res1 = expr.generate(level);
                if (!res.equals(res1))
                    System.exit(1);
                Order store = new Order("store.64", level);
                startcode.getStartCodeTable().orders.add(store);
            } else
                System.exit(1);
        } else {
            new_variable = new Variable(this.ty, this.ident, false, false, level);
            if (level > 0) {
                Functionarrary.getFunctionTable().getCurrentFuction().variables.add(new_variable);
            } else if (level == 0) {
                startcode.getStartCodeTable().variables.add(new_variable);
            } else
                System.exit(1);
        }
        return res;
    }
}

// const_decl_stmt -> 'const' IDENT ':' ty '=' operator_expr ';'
class const_decl_stmtAst extends Ast {
    String ident;
    String ty;
    operator_exprAst expr;

    public const_decl_stmtAst(String ident, String ty, operator_exprAst expr) {
        this.ident = ident;
        this.ty = ty;
        this.expr = expr;
    }

    public String generate(int level) {
        String res = " ", res1 = " ";
        res = this.ty;
        Variable new_variable = new Variable(this.ty, this.ident, true, true, level);
        if (level > 0) {
            Functionarrary.getFunctionTable().getCurrentFuction().variables.add(new_variable);
            Function currentFunction = Functionarrary.getFunctionTable().getCurrentFuction();
            int index = currentFunction.get_index_variables(this.ident, level);
            if(index == -1)
                System.exit(1);
            Order loca = new Order("loca", level);
            loca.addOper((long) index);
            currentFunction.addorders(loca);
            res1 = expr.generate(level);
            if (!res.equals(res1))
                System.exit(1);
            Order store = new Order("store.64", level);
            currentFunction.addorders(store);
        } else if (level == 0) {
            startcode.getStartCodeTable().variables.add(new_variable);
            int index = startcode.getStartCodeTable().get_index(this.ident);
            Order globa = new Order("globa", level);
            globa.addOper((long) index);
            startcode.getStartCodeTable().orders.add(globa);
            res1 = expr.generate(level);
            if (!res.equals(res1))
                System.exit(1);
            Order store = new Order("store.64", level);
            startcode.getStartCodeTable().orders.add(store);
        } else
            System.exit(1);

        return res;
    }
}

// decl_stmt -> let_decl_stmt | const_decl_stmt
class decl_stmtAst extends Ast {
    Ast decl_stmt;

    public decl_stmtAst(Ast decl_stmt) {
        this.decl_stmt = decl_stmt;
    }

    public String generate(int level) {
        String res = "void";
        if (decl_stmt instanceof let_decl_stmtAst) {
            let_decl_stmtAst let_decl_stmt = (let_decl_stmtAst) decl_stmt;
            res = let_decl_stmt.generate(level);
        }
        else if (decl_stmt instanceof const_decl_stmtAst) {
            const_decl_stmtAst const_decl_stmt = (const_decl_stmtAst) decl_stmt;
            res = const_decl_stmt.generate(level);
        }
        else
            System.exit(1);
        return res;
    }
}

// if_stmt -> 'if' condition block_stmt ('else' 'if' condition block_stmt)*
// ('else' block_stmt)?
class if_stmtAst extends Ast {
    conditionAst condition_if;
    block_stmtAst block_stmt_if;
    ArrayList<conditionAst> condition_else_if = new ArrayList<>();
    ArrayList<block_stmtAst> block_stmt_else_if = new ArrayList<>();
    block_stmtAst block_stmt_else;

    ArrayList<Integer> start = new ArrayList<Integer>();
    ArrayList<Integer> jump_in = new ArrayList<Integer>();
    ArrayList<Integer> jump_out = new ArrayList<Integer>();

    public if_stmtAst(conditionAst condition_if, block_stmtAst block_stmt_if, ArrayList<conditionAst> condition_else_if,
            ArrayList<block_stmtAst> block_stmt_else_if, block_stmtAst block_stmt_else) {
        this.condition_if = condition_if;
        this.block_stmt_if = block_stmt_if;
        this.condition_else_if = condition_else_if;
        this.block_stmt_else_if = block_stmt_else_if;
        this.block_stmt_else = block_stmt_else;
    }

    public String process(int level, conditionAst this_condition, block_stmtAst this_block) {
        String res = "void";
        Function currentFunction = Functionarrary.getFunctionTable().getCurrentFuction();

        if (this_condition != null) {
            int index1 = currentFunction.get_last_order_index();
            start.add(index1);
            res = this_condition.generate(level);
            if (!res.equals("boolean"))
                System.exit(1);
            Order brtrue = new Order("br.true", level);
            brtrue.addOper(1L);
            currentFunction.addorders(brtrue);
            int index2 = currentFunction.get_last_order_index();
            Order br1 = new Order("br", level);
            br1.addOper(0L);
            currentFunction.addorders(br1);
            jump_in.add(index2);
        }

        res = this_block.generate(level);
        int index3 = currentFunction.get_last_order_index();
        Order br2 = new Order("br", level);
        br2.addOper(0L);
        currentFunction.addorders(br2);
        jump_out.add(index3);
        return res;
    }

    public String generate(int level) {
        // if()
        String res = " ";
        res = process(level, condition_if, block_stmt_if);
        // else if()
        if(condition_else_if == null)
            ;
        else{
            int len = condition_else_if.size();
            if (len != 0)
                for (int i = 0; i < len; i++) {
                    String temp = " ";
                    temp = process(level, condition_else_if.get(i), block_stmt_else_if.get(i));
                    res = temp;
                }
        }
        // else
        if (block_stmt_else != null) {
            String temp = " ";
            temp = process(level, null, block_stmt_else);
            res = temp;
        }

        Function currentFunction = Functionarrary.getFunctionTable().getCurrentFuction();
        int last = jump_out.get(jump_out.size() - 1);
        int len_jump_in = jump_in.size();
        int len_jump_out = jump_out.size();
        int len_temp = Math.min(len_jump_in, len_jump_out);
        for (int i = 0; i < len_temp; i++) {
            int out = jump_out.get(i);
            int in = jump_in.get(i);
            currentFunction.orders.get(in).addOper((long) (out - in));
            currentFunction.orders.get(out).addOper((long) last - out);
        }
        return res;
    }
}

// while_stmt -> 'while' condition block_stmt
class while_stmtAst extends Ast {
    conditionAst condition;
    block_stmtAst block_stmt;

    public while_stmtAst(conditionAst condition, block_stmtAst block_stmt) {
        this.condition = condition;
        this.block_stmt = block_stmt;
    }

    public String generate(int level) {

        String res = " ";
        Function currentFunction = Functionarrary.getFunctionTable().getCurrentFuction();
        currentFunction.while_index = currentFunction.while_index + 1;
        int this_while_index=currentFunction.while_index;
        int index1 = currentFunction.get_last_order_index();
        Order br = new Order("br", level);
        br.addOper(0L);
        currentFunction.addorders(br);
        res = condition.generate(level);
        if (!res.equals("boolean"))
            System.exit(1);
        Order brtrue = new Order("br.true", level);
        brtrue.addOper(1L);
        currentFunction.addorders(brtrue);

        int index2 = currentFunction.get_last_order_index();
        Order br1 = new Order("br", level);
        br1.addOper(0L);
        currentFunction.addorders(br1);
        res = block_stmt.generate(level);

        int index3 = currentFunction.get_last_order_index();
        Order br2 = new Order("br", level);
        br2.addOper((long) (index1 - index3));
        currentFunction.addorders(br2);

        currentFunction.orders.get(index2).addOper((long) (index3 - index2));

        int len = currentFunction.get_last_order_index();

        for (int i = len - 1; i >= 0; i--) {
            Order this_order = currentFunction.orders.get(i);
            String op = this_order.get_opcode();
            if (this_order.get_opcode().equals("br") && this_order.get_oper() == -100000000L)
                if (this_order.while_index == this_while_index)
                    currentFunction.orders.get(i).addOper((long) (index3 - i));
            if (this_order.get_opcode().equals("br") && this_order.get_oper() == -100000001L)
                if (this_order.while_index == this_while_index)
                    currentFunction.orders.get(i).addOper((long) (index1 - i));
        }

        return res;
    }
}

// break_stmt -> 'break' ';'
class break_stmtAst extends Ast {
    String break_token;

    public break_stmtAst(String s) {
        this.break_token = s;
    }

    public String generate(int level) {

        String res = "void";
        Order break_loop = new Order("br", level);
        break_loop.addOper(-100000000L);
        Function currentFunction = Functionarrary.getFunctionTable().getCurrentFuction();
        break_loop.while_index  = currentFunction.while_index;
        currentFunction.addorders(break_loop);
        return res;
    }

}

// continue_stmt -> 'continue' ';'
class continue_stmtAst extends Ast {
    String continue_token;

    public continue_stmtAst(String s) {
        this.continue_token = s;
    }

    public String generate(int level) {
        String res = "void";
        Order continue_loop = new Order("br", level);
        continue_loop.addOper(-100000001L);
        Function currentFunction = Functionarrary.getFunctionTable().getCurrentFuction();
        continue_loop.while_index  = currentFunction.while_index;
        currentFunction.addorders(continue_loop);
        return res;
    }

}

// return_stmt -> 'return' operator_expr? ';'
class return_stmtAst extends Ast {
    operator_exprAst expr;

    public return_stmtAst() {
    }

    public return_stmtAst(operator_exprAst expr) {
        this.expr = expr;
    }

    public String generate(int level) {
        String res = "void";
        Function currentFunction = Functionarrary.getFunctionTable().getCurrentFuction();
        if (expr != null) {
            Order arga = new Order("arga", level);
            arga.addOper(0L);
            currentFunction.addorders(arga);
            res = expr.generate(level);
            Order store = new Order("store.64", level);
            currentFunction.addorders(store);
        }
        Order ret = new Order("ret", level);
        currentFunction.addorders(ret);
        return res;
    }
}

// block_stmt -> '{' stmt* '}'
class block_stmtAst extends Ast {
    ArrayList<stmtAst> stmts = new ArrayList<stmtAst>();

    public block_stmtAst(ArrayList<stmtAst> stmts) {
        this.stmts = stmts;
    }

    public block_stmtAst() {
    }

    public String generate(int level) {
        if (stmts == null)
            return "void";
        String res = "void";
        level = level + 1;
        int len = stmts.size();
        for (int i = 0; i < len; i++) {
            res = stmts.get(i).generate(level);
        }
        return res;
    }
}

// empty_stmt -> ';'
class empty_stmtAst extends Ast {
    String semicolon;

    public empty_stmtAst(String s) {
        this.semicolon = s;
    }

    public String generate(int level) {
        String res = "void";
        return res;
    }
}

// function_param -> 'const'? IDENT ':' ty
class function_paramAst extends Ast {
    String constant;
    String ident;
    String ty;

    public function_paramAst(String constant, String ident, String ty) {
        this.ty = ty;
        this.ident = ident;
        this.constant = constant;
    }

    public function_paramAst(String ident, String ty) {
        this.ty = ty;
        this.ident = ident;
    }

    public String generate(int level) {
        boolean flag = false;
        if (constant != null)
            flag = true;
        Variable new_variable = new Variable(ty, ident, flag, false, level);
        Function currentfunction = Functionarrary.getFunctionTable().getCurrentFuction();
        currentfunction.parameters.add(new_variable);
        return "void";
    }
}

// function_param_list -> function_param (',' function_param)*
class function_param_listAst extends Ast {
    function_paramAst function_param_front;
    ArrayList<function_paramAst> function_params = new ArrayList<>();

    public function_param_listAst(function_paramAst function_param_front) {
        this.function_param_front = function_param_front;
    }

    public function_param_listAst(function_paramAst function_param_front,
            ArrayList<function_paramAst> function_params) {
        this.function_param_front = function_param_front;
        this.function_params = function_params;
    }

    String generate(int level) {
        String res = "void";
        res = function_param_front.generate(level);
        int len = function_params.size();
        for (int i = 0; i < len; i++)
            res = function_params.get(i).generate(level);
        return "void";
    }
}

// function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
class functionAst extends Ast {
    String ident;
    function_param_listAst function_param_list;
    String ty;
    block_stmtAst block_stmt;

    public functionAst(String ident, function_param_listAst function_param_list, String ty, block_stmtAst block_stmt) {
        this.ident = ident;
        this.ty = ty;
        this.function_param_list = function_param_list;
        this.block_stmt = block_stmt;
    }

    public functionAst(String ident, String ty, block_stmtAst block_stmt) {
        this.ident = ident;
        this.ty = ty;
        this.block_stmt = block_stmt;
    }

    public String generate(int level) {

        String res = "void";
        Function this_func = new Function(ident, ty);
        constant con = new constant(this.ty,this.ident);
        constantarray.getConstantTable().constants.add(con);
        if (!ty.equals("void"))
            this_func.set_return_slots(1);
        Functionarrary.getFunctionTable().functions.add(this_func);
        if(!this.ty.equals("void")){
            Function current_func = Functionarrary.getFunctionTable().getCurrentFuction();
            Variable temp = new Variable(this.ty,"return",false,false,level);
            current_func.parameters.add(temp);
        }
        if (function_param_list != null)
            res = function_param_list.generate(level);
        res = block_stmt.generate(level);
        if(res!=this.ty) {
            ArrayList<Order> fun_order = Functionarrary.getFunctionTable().getCurrentFuction().orders;
            int len = fun_order.size();
            for(int i=len-1;i>=0;i--){

            }
        }
        Variable func = new Variable(this.ty,this.ident,true,true,0);
        func.setfunc_true();
        startcode.getStartCodeTable().variables.add(func);
        Function current_fun = Functionarrary.getFunctionTable().getCurrentFuction();
        ArrayList<Order> func_order = current_fun.orders;
        int len = func_order.size();
        if(len>0 && !func_order.get(len-1).getOpcode().equals("ret")){
            Order ret = new Order("ret",level);
            Functionarrary.getFunctionTable().getCurrentFuction().orders.add(ret);
        }
        if(!res.equals(this.ty))
            System.exit(1);
        if(len==0){
            Order ret = new Order("ret",level);
            Functionarrary.getFunctionTable().getCurrentFuction().orders.add(ret);
        }
        res = this.ty;
        return res;
    }

}

//// # 程序
// item -> function | decl_stmt
class itemAst extends Ast {
    Ast item;

    public itemAst(Ast item) {
        this.item = item;
    }

    public String generate(int level) {
        String res;
        if (item instanceof functionAst) {
            functionAst function = (functionAst) item;
            res = function.generate(level);
        } else if (item instanceof decl_stmtAst) {
            decl_stmtAst decl_stmt = (decl_stmtAst) item;
            res = decl_stmt.generate(level);
        } else
            System.exit(1);
        return "void";
    }
}

// program -> item*
public class programAst extends Ast {
    ArrayList<itemAst> items;

    public programAst(ArrayList<itemAst> items) {
        this.items = items;
    }

    public String generate(int level) {
        constant con = new constant("void","_start");
        constantarray.getConstantTable().constants.add(con);
        Variable start_var = new Variable("string","_start",true,true,0);
        Function start_func = new Function("_start","void");
        ArrayList<Function> funcs = Functionarrary.getFunctionTable().functions;
        funcs.add(start_func);
        String res;
        int len = items.size();
        for (int i = 0; i < len; i++)
            res = items.get(i).generate(level);
        startcode.getStartCodeTable().variables.add(start_var);
        Function main_func = Functionarrary.getFunctionTable().get_function("main");
        int index = Functionarrary.getFunctionTable().get_index("main");
        if(main_func.type.equals("void")){
            Order stackAlloc = new Order("stackalloc",0);
            stackAlloc.addOper(0L);
            startcode.getStartCodeTable().orders.add(stackAlloc);
            Order call = new Order("call",0);
            call.addOper((long)index);
            startcode.getStartCodeTable().orders.add(call);
        }
        else{
            Order stackAlloc = new Order("stackalloc",0);
            stackAlloc.addOper(1L);
            startcode.getStartCodeTable().orders.add(stackAlloc);
            Order call = new Order("call",0);
            call.addOper((long)index);
            startcode.getStartCodeTable().orders.add(call);
            Order popn = new Order("popn",0);
            popn.addOper(1L);
            startcode.getStartCodeTable().orders.add(popn);
        }
        return "void";
    }
}