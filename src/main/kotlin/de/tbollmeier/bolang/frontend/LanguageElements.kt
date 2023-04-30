package de.tbollmeier.bolang.frontend

data class Token(val name: String, val pattern: String)
data class Keyword(val content: String, val name: String)

data class Comment(val name: String, val begin: String, val end: String)

object LanguageElements {

    val LINE_COMMENT = Comment("LINE_COMMENT", "//", "\n")
    val MULT_LINE_COMMENT = Comment("MULT_LINE_COMMENT", "/*", "*/")

    val comments = listOf(LINE_COMMENT, MULT_LINE_COMMENT)

    val FUNCTION = Keyword("function", "FUNCTION")
    val IF = Keyword("if", "IF")
    val WHILE = Keyword("while", "WHILE")
    val ELSE = Keyword("else", "ELSE")
    val RETURN = Keyword("return", "RETURN")
    val VAR = Keyword("var", "VAR")

    val keywords = listOf(
        FUNCTION,
        IF,
        WHILE,
        ELSE,
        RETURN,
        VAR
    )

    val COMMA = Token("COMMA", "[,]")
    val SEMICOLON = Token("SEMICOLON", ";")
    val LEFT_PAREN = Token("LEFT_PAREN", "[(]")
    val RIGHT_PAREN = Token("RIGHT_PAREN", "[)]")
    val LEFT_BRACE = Token("LEFT_BRACE", "[{]")
    val RIGHT_BRACE = Token("RIGHT_BRACE", "[}]")
    val NUMBER = Token("NUMBER", "[0-9]+")
    val ID = Token("ID", "[a-zA-Z_][a-zA-Z0-9_]*")
    val NOT = Token("NOT", "!")
    val EQUAL = Token("EQUAL", "==")
    val NOT_EQUAL = Token("NOT_EQUAL", "!=")
    val PLUS = Token("PLUS", "[+]")
    val MINUS = Token("MINUS", "[-]")
    val STAR = Token("STAR", "[*]")
    val SLASH = Token("SLASH", "/")
    val ASSIGN = Token("ASSIGN", "=")

    val tokens = listOf(
        COMMA,
        SEMICOLON,
        LEFT_PAREN,
        RIGHT_PAREN,
        LEFT_BRACE,
        RIGHT_BRACE,
        NUMBER,
        ID,
        NOT,
        EQUAL,
        NOT_EQUAL,
        PLUS,
        MINUS,
        STAR,
        SLASH,
        ASSIGN
    )

}
