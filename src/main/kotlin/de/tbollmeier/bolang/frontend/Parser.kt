package de.tbollmeier.bolang.frontend

import de.tbollmeier.grammarous.Ast
import de.tbollmeier.grammarous.Result
import de.tbollmeier.grammarous.SyntaxParser
import de.tbollmeier.grammarous.grammar

import de.tbollmeier.bolang.frontend.LanguageElements as LE

class Parser {

    private val grammar = grammar {

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
        }

        ruleDef("ifStatement") {
            terminal(LE.IF.name)
            terminal(LE.LEFT_PAREN.name)
            rule("expression", "conditional")
            terminal(LE.RIGHT_PAREN.name)
            rule("statement", "consequent")
            terminal(LE.ELSE.name)
            rule("statement", "alternative")
        }

        ruleDef("whileStatement") {
            terminal(LE.WHILE.name)
            terminal(LE.LEFT_PAREN.name)
            rule("expression", "conditional")
            terminal(LE.RIGHT_PAREN.name)
            rule("statement", "body")
        }

        ruleDef("varStatement") {
            terminal(LE.VAR.name)
            terminal(LE.ID.name, "name")
            terminal(LE.ASSIGN.name)
            rule("expression", "value")
            terminal(LE.SEMICOLON.name)
        }

        ruleDef("assignmentStatement") {
            terminal(LE.ID.name, "name")
            terminal(LE.ASSIGN.name)
            rule("expression", "value")
            terminal(LE.SEMICOLON.name)
        }

        ruleDef("blockStatement") {
            terminal(LE.LEFT_BRACE.name)
            many { rule("statement", "stmt") }
            terminal(LE.RIGHT_BRACE.name)
        }

        ruleDef("functionStatement") {
            terminal(LE.FUNCTION.name)
            terminal(LE.ID.name, "name")
            terminal(LE.LEFT_PAREN.name)
            rule("parameters", "parameters")
            terminal(LE.RIGHT_PAREN.name)
            rule("blockStatement", "body")
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
        }

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
        }

        ruleDef("args") {
            optional {
                rule("expression")
                many {
                    terminal(LE.COMMA.name)
                    rule("expression")
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