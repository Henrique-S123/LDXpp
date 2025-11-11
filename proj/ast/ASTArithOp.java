package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTArithOp implements ASTNode {

    ASTNode lhs, rhs;
	String op;

	public ASTArithOp(ASTNode l, ASTNode r, String o) {
		lhs = l;
		rhs = r;
		op = o;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue v1 = lhs.eval(e);
		IValue v2 = rhs.eval(e);
		if (v1 instanceof VInt && v2 instanceof VInt) {
			int i1 = ((VInt) v1).getval();
			int i2 = ((VInt) v2).getval();
			int res = switch (op) {
				case "+" -> i1 + i2;
				case "-" -> i1 - i2;
				case "*" -> i1 * i2;
				case "/" -> i1 / i2;
				case "-u" -> -i2;
				default -> throw new InterpreterError("unknown operation");
			};
			boolean lin = (((VInt) v1).islin() || ((VInt) v2).islin());
			return new VInt(res, lin);
		} else if ((v1 instanceof VString || v1 instanceof VInt) && (v2 instanceof VInt || v2 instanceof VString) && op == "+") {
			String s1 = v1 instanceof VString ? ((VString) v1).getval() : v1.toStr();
			String s2 = v2 instanceof VString ? ((VString) v2).getval() : v2.toStr();
			return new VString(s1 + s2);
		} else {
			String types = (op == "-u" ? "" : (v1 + " and ")) + v2.toStr();
			if (op == "-u")
				throw new InterpreterError("- unary operator: integer expected, found " + types);
			else {
				if (op == "+")
					throw new InterpreterError(op + "operator: integers or strings expected, found " + types);
				else
					throw new InterpreterError(op + "operator: integers expected, found " + types);
			}
		}
    }

	public ASTType typecheck(Environment<ASTType> te) throws TypeCheckError, InterpreterError {
		ASTType tl = lhs.typecheck(te);
		ASTType tr = rhs.typecheck(te);
		if (tl instanceof ASTTInt && tr instanceof ASTTInt) {
			return new ASTTInt();
		} else if ((tl instanceof ASTTInt || tl instanceof ASTTLInt) && (tr instanceof ASTTInt || tr instanceof ASTTLInt)) {
			return new ASTTLInt();
		} else if ((tl instanceof ASTTInt || tl instanceof ASTTString) && (tr instanceof ASTTInt || tr instanceof ASTTString) && op == "+") {
			return new ASTTString();
		} else {
			String types = (op == "-u" ? "" : (tl.toStr() + " and ")) + tr.toStr();
			if (op == "-u")
				throw new TypeCheckError("illegal type to - unary operator: " + types);
			else
				throw new TypeCheckError("illegal types to " + op + " operator: " + types);
		}
	}
}
