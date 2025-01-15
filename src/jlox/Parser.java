package jlox;

import javax.swing.*;
import java.util.List;
import jlox.TokenType;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    private Expr expression() {
        return equality();
    }
    private Expr equality() {
        Expr expr = comparsion();

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparsion();

            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

//    private Expr comparsion() {
//
//    }









    // for utils
    private Token advance() {    //consume current tokrn and return this token, last put forward the current pointer
        if (!isAtEnd()) current++;
        return previous();

    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;

    }
    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean match(TokenType... types) {  //match also put forward this current
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }
}
