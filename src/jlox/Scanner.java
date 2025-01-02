package jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Character.isDigit;

public class Scanner {
    //field
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start;      //point to the start of a lexeme
    private int current;    //point to the current of a lexeme
    private int line;

    // table to record the reserved word in lox language
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }
    //instance methods
    Scanner(String source) {
        this.source = source;
    }


    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        //add an EOF Token at the end of Tokens
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        // for single char lexemes
        char i = forward();
        switch (i) {
            case '(' -> addToken(TokenType.LEFT_PAREN);
            case ')' -> addToken(TokenType.RIGHT_PAREN);
            case '{' -> addToken(TokenType.LEFT_BRACE);
            case '}' -> addToken(TokenType.RIGHT_BRACE);
            case ',' -> addToken(TokenType.COMMA);
            case '.' -> addToken(TokenType.DOT);
            case '-' -> addToken(TokenType.MINUS);
            case '+' -> addToken(TokenType.PLUS);
            case ';' -> addToken(TokenType.SEMICOLON);
            case '*' -> addToken(TokenType.STAR);

            // for one or two char lexemes
            case '!' -> addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
            case '=' -> addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
            case '>' -> addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
            case '<' -> addToken(match('=') ? TokenType.LESS_EQUAL: TokenType.LESS);
            // a special case
            case '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {   //ignore comment
                        forward();
                    }
                } else {
                    addToken(TokenType.SLASH);
                }
            }
            case ' ' -> { }
            case '\r' -> { }
            case '\t' -> { }
            case '\n' -> line++;

            //for deal with literal
            case '"' -> string();
            // for dealing with error
            default -> {
                if (isDigit(i)) {
                    number();
                } else if (isAlpha(i)) {
                    identifier();
                }else {
                    Lox.error(line, "lexcial error: Unexcepted cachacter");
                }
            }
        }

    }


    /* utils*/
    private void identifier() {
        while (isAlpha(peek()) || isDigit(peek())) {
            forward();
        }

        // if identifier in keywords table use different Type to deal with
        String word = source.substring(start, current);
        TokenType type = keywords.get(word);
        if (type == null) {
            addToken(TokenType.IDENTIFIER);
        } else {
            addToken(type);
        }
    }
    private boolean isAlpha(final char i) {
        return ((i >= 'a' && i <= 'z')
                || (i >= 'A' && i <= 'Z')
                || (i == '_'));
    }
    private void number() {
        while (isDigit(peek())) forward();
        if (peek() == '.' && isDigit(peekNext())) {
            forward();

            while (isDigit(peek())) forward();
        }

        addToken(TokenType.NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }
    private char peekNext() {
        return (current + 1 > source.length() ? '\0' : source.charAt(current + 1));
    }
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            forward();

            if (isAtEnd()) {
                Lox.error(line, "Untermained string literal!");
            }
        }

        forward();

        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }
    private char peek() {    //see the current char
        return isAtEnd() ? '\0' : source.charAt(current);
    }
    private boolean match(char next) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != next) return false;

        current++;
        return true;
    }
    private void addToken(final TokenType type) {
        tokens.add(new Token(type, "", null, line));
    }
    private void addToken(final TokenType type, Object literal) {
        String lexme = source.substring(start, current);
        tokens.add(new Token(type, lexme, literal, line));
    }
    private char forward() {
        current++;
        return source.charAt(current - 1);
    }
    private boolean isAtEnd() {
        return current >= source.length();
    }
    //static methods
}
