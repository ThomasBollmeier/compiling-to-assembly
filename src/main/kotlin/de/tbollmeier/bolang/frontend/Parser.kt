package de.tbollmeier.bolang.frontend

import de.tbollmeier.grammarous.Ast
import de.tbollmeier.grammarous.Result
import de.tbollmeier.grammarous.SyntaxParser
import de.tbollmeier.grammarous.grammar
import de.tbollmeier.bolang.frontend.LanguageElements as LE

class Parser {

    private val grammar = grammar {

        transform("ID") {
            Identifier(it.value)
        }

        transform("NUMBER") {
            Number(it.value)
        }

        ruleDef("program") {
            many { rule("statement") }
        }

        ruleDef("statement") {
            oneOf {
                rule("returnStatement")
                rule("ifStatement")
                rule("whileStatement")
                rule("varStatement")
                rule("assignmentStatement")
                rule("blockStatement")
                rule("functionStatement")
                rule("expressionStatement")
            }
        } transformBy { it.children[0] }

        ruleDef("returnStatement") {
            terminal(LE.RETURN.name)
            rule("expression")
            terminal(LE.SEMICOLON.name)
        } transformBy lambda@{
            val term = it.children[1]
            term.id = ""
            return@lambda Return(term)
        }

        ruleDef("ifStatement") {
            terminal(LE.IF.name)
            terminal(LE.LEFT_PAREN.name)
            rule("expression", "conditional")
            terminal(LE.RIGHT_PAREN.name)
            rule("statement", "consequence")
            terminal(LE.ELSE.name)
            rule("statement", "alternative")
        } transformBy lambda@{
            val conditional = it.getChildrenById("conditional")[0]
            val consequence = it.getChildrenById("consequence")[0]
            val alternative = it.getChildrenById("alternatice")[0]
            return@lambda If(conditional, consequence, alternative)
        }

        ruleDef("whileStatement") {
            terminal(LE.WHILE.name)
            terminal(LE.LEFT_PAREN.name)
            rule("expression", "conditional")
            terminal(LE.RIGHT_PAREN.name)
            rule("statement", "body")
        } transformBy lambda@{
            val conditional = it.getChildrenById("conditional")[0]
            val body = it.getChildrenById("body")[0]
            return@lambda While(conditional, body)
        }

        ruleDef("varStatement") {
            terminal(LE.VAR.name)
            terminal(LE.ID.name, "name")
            terminal(LE.ASSIGN.name)
            rule("expression", "value")
            terminal(LE.SEMICOLON.name)
        } transformBy lambda@{
            val name = it.getChildrenById("name")[0].attrs["name"]!!
            val value = it.getChildrenById("value")[0]
            return@lambda Var(name, value)
        }

        ruleDef("assignmentStatement") {
            terminal(LE.ID.name, "name")
            terminal(LE.ASSIGN.name)
            rule("expression", "value")
            terminal(LE.SEMICOLON.name)
        } transformBy lambda@{
            val name = it.getChildrenById("name")[0].attrs["name"]!!
            val value = it.getChildrenById("value")[0]
            return@lambda Assign(name, value)
        }

        ruleDef("blockStatement") {
            terminal(LE.LEFT_BRACE.name)
            many { rule("statement", "stmt") }
            terminal(LE.RIGHT_BRACE.name)
        } transformBy lambda@{
            val statements = it.getChildrenById("stmt")
            statements.forEach { it.id = "" }
            return@lambda Block(statements)
        }

        ruleDef("functionStatement") {
            terminal(LE.FUNCTION.name)
            terminal(LE.ID.name, "name")
            terminal(LE.LEFT_PAREN.name)
            rule("parameters", "parameters")
            terminal(LE.RIGHT_PAREN.name)
            rule("blockStatement", "body")
        } transformBy lambda@{
            val name = it.getChildrenById("name")[0].attrs["name"]!!

            val params = mutableListOf<Identifier>()
            val parameters = it.getChildrenById("parameters")[0]
            for (parameter in parameters.getChildrenById("param")) {
                parameter.id = ""
                params.add((parameter as Identifier))
            }

            val body = it.getChildrenById("body")[0] as Block
            body.id = ""

            return@lambda Function(name, params, body)
        }

        ruleDef("parameters") {
            optional {
                terminal(LE.ID.name, "param")
                many {
                    terminal(LE.COMMA.name)
                    terminal(LE.ID.name, "param")
                }
            }
        }

        ruleDef("expressionStatement") {
            rule("expression")
            terminal(LE.SEMICOLON.name)
        } transformBy { it.children[0] }

        ruleDef("expression") {
            rule("comparison")
        } transformBy { it.children[0] }

        ruleDef("comparison") {
            rule("sum")
            many {
                oneOf {terminal(LE.EQUAL.name); terminal(LE.NOT_EQUAL.name) }
                rule("sum")
            }
        } transformBy {
            transformBinOp(it.children)
        }

        ruleDef("sum") {
            rule("product")
            many {
                oneOf {terminal(LE.PLUS.name); terminal(LE.MINUS.name) }
                rule("product")
            }
        } transformBy {
            transformBinOp(it.children)
        }

        ruleDef("product") {
            rule("unary")
            many {
                oneOf {terminal(LE.STAR.name); terminal(LE.SLASH.name) }
                rule("unary")
            }
        } transformBy {
            transformBinOp(it.children)
        }

        ruleDef("unary") {
            optional { terminal(LE.NOT.name) }
            rule("atom")
        } transformBy {
            if (it.children.size == 1) it.children[0] else Not(it.children[0])
        }

        ruleDef("atom") {
            oneOf {
                rule("call")
                terminal(LE.ID.name)
                terminal(LE.NUMBER.name)
                sequence {
                    terminal(LE.LEFT_PAREN.name)
                    rule("expression")
                    terminal(LE.RIGHT_PAREN.name)
                }
            }
        } transformBy {
            if (it.children.size == 1)
                it.children[0]
            else
                it.getChildrenById("expression")[0]
        }

        ruleDef("call") {
            terminal(LE.ID.name)
            terminal(LE.LEFT_PAREN.name)
            rule("args")
            terminal(LE.RIGHT_PAREN.name)
        } transformBy lambda@{
            val callee = it.children[0].attrs["name"]!!
            val args = it.children[2].getChildrenById("arg")

            return@lambda Call(callee, args)
        }

        ruleDef("args") {
            optional {
                rule("expression", "arg")
                many {
                    terminal(LE.COMMA.name)
                    rule("expression", "arg")
                }
            }
        }

    }

    fun parse(code: String): Result<Ast> {
        val tokenStream = Scanner().scan(code)
        val parser = SyntaxParser(grammar)
        return parser.parse(tokenStream)
    }

    private fun transformBinOp(operandsAndOps: List<Ast>): Ast {
        var idx = 0
        var ret = operandsAndOps[idx]

        while (idx < operandsAndOps.size - 1) {
            val op = operandsAndOps[idx + 1]
            val right = operandsAndOps[idx + 2]
            ret = makeBinOp(op.name, ret, right)
            idx += 2
        }

        return ret
    }

}