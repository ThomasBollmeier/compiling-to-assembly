package de.tbollmeier.bolang.frontend

import de.tbollmeier.grammarous.Stream
import de.tbollmeier.grammarous.Token

class ScannerTest {

    @org.junit.jupiter.api.Test
    fun scan() {

        val code = """
            function factorial(n) {
                var result = 1;
                while (n != 1) {
                    result = result * n;
                    n = n - 1;
                }
                return result;
            }
            
            show(factorial(10));
            say_hello("Thomas", "Bollmeier")
            
        """.trimIndent()

        printTokens(Scanner().scan(code))
    }

    @org.junit.jupiter.api.Test
    fun scanReturnStmt() {
        printTokens(Scanner().scan("return 1 / 2;"))
    }

    private fun printTokens(tokenStream: Stream<Token>) {
        while (tokenStream.hasNext()) {
            val token = tokenStream.next()!!
            println("${token.lexeme} (${token.type})")
        }
    }
}