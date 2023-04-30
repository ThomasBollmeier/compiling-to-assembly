package de.tbollmeier.bolang.frontend

import de.tbollmeier.grammarous.LexerGrammar
import de.tbollmeier.grammarous.createLexer
import de.tbollmeier.grammarous.createStringCharStream

class Scanner {

    private val lexerGrammar: LexerGrammar = LexerGrammar().apply {
        for (comment in LanguageElements.comments) {
            defineComment(comment.name, comment.begin, comment.end)
        }

        for (kw in LanguageElements.keywords) {
            defineKeyword(kw.content, kw.name)
        }

        for (tok in LanguageElements.tokens) {
            defineToken(tok.name, tok.pattern)
        }
    }

    fun scan(code: String) =
        createLexer(lexerGrammar).scan(createStringCharStream(code))

}