package de.tbollmeier.bolang.frontend

import de.tbollmeier.grammarous.Ast
import de.tbollmeier.bolang.frontend.LanguageElements as LE

open class BinOp(name: String, left: Ast, right: Ast): Ast(name) {
    init {
        addChild(left)
        addChild(right)
    }
}

class Equal(left: Ast, right: Ast): BinOp("Equal", left, right)
class NotEqual(left: Ast, right: Ast): BinOp("NotEqual", left, right)
class Add(left: Ast, right: Ast): BinOp("Add", left, right)
class Subtract(left: Ast, right: Ast): BinOp("Subtract", left, right)
class Multiply(left: Ast, right: Ast): BinOp("Multiply", left, right)
class Divide(left: Ast, right: Ast): BinOp("Divide", left, right)

fun makeBinOp(operator: String, left: Ast, right: Ast):Ast {
    return when (operator) {
        LE.EQUAL.name -> Equal(left, right)
        LE.NOT_EQUAL.name -> NotEqual(left, right)
        LE.PLUS.name -> Add(left, right)
        LE.MINUS.name -> Subtract(left, right)
        LE.STAR.name -> Multiply(left, right)
        LE.SLASH.name -> Divide(left, right)
        else -> BinOp("UnknownOp", left, right)
    }
}

class Not(term: Ast): Ast("Not") {
    init {
        addChild(term)
    }
}