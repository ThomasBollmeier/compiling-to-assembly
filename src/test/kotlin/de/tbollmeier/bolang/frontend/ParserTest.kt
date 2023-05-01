package de.tbollmeier.bolang.frontend

import de.tbollmeier.grammarous.Ast
import de.tbollmeier.grammarous.AstXmlFormatter
import de.tbollmeier.grammarous.Result
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertIs

class ParserTest {

    private lateinit var parser: Parser

    @BeforeEach
    fun setUp() {
        parser = Parser()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun parseFunctionOK() {
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
            sayHello(firstName, lastName);
        """.trimIndent()

        parseOK(code)
    }

    @Test
    fun parseReturnOK() {

        val code = """
            return 1 + 2;
        """.trimIndent()

        parseOK(code)
    }

    @Test
    fun parseExpressionStmtOk() {

        val code = """
            3 + 4 - 5 * 7;
        """.trimIndent()

        parseOK(code)
    }

    private fun parseOK(code: String) {

        val result = parser.parse(code)
        assertIs<Result.Success<Ast>>(result, (result as? Result.Failure)?.message)

        val ast = result.value

        val formatter = AstXmlFormatter()
        println(formatter.toXml(ast))
    }
}