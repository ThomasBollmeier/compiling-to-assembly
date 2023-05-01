package de.tbollmeier.bolang.frontend

import de.tbollmeier.grammarous.Ast
import de.tbollmeier.bolang.frontend.LanguageElements as LE

class Identifier(name: String) : Ast("Identifier") {
    init {
        attrs["name"] = name
    }
}

class Number(value: String) : Ast("Number") {
    init {
        attrs["value"] = value
    }
}

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

class Return(term: Ast): Ast("Return") {
    init {
        addChild(term)
    }
}

class Block(statements: List<Ast>) : Ast("Block") {
    init {
        for (stmt in statements) {
            stmt.id = ""
            addChild(stmt)
        }
    }
}

class Function(name: String, parameters: List<Identifier>, body: Block): Ast("Function") {
    init {
        attrs["name"] = name
        val params = Ast("Parameters")
        addChild(params)
        for (parameter in parameters) {
            params.addChild(parameter)
        }
        addChild(body)
    }
}

class If(conditional: Ast, consequence: Ast, alternative: Ast): Ast("If") {
    init {
        addChild(conditional)
        addChild(consequence)
        addChild(alternative)
        children.forEach { it.id = "" }
    }
}

class While(conditional: Ast, body: Ast): Ast("While") {
    init {
        addChild(conditional)
        addChild(body)
        children.forEach { it.id = "" }
    }
}

class Var(name: String, value: Ast): Ast("Var") {
    init {
        attrs["name"] = name
        value.id = ""
        addChild(value)
    }
}
class Assign(name: String, value: Ast): Ast("Assign") {
    init {
        attrs["name"] = name
        value.id = ""
        addChild(value)
    }
}

class Call(callee: String, args: List<Ast>): Ast("Call") {
    init {
        attrs["callee"] = callee
        for (arg in args) {
            arg.id = ""
            addChild(arg)
        }
    }
}