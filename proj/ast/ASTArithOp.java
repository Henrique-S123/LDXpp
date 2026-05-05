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

	public ASTNode getLhs() {
		return lhs;
	}

	public ASTNode getRhs() {
		return rhs;
	}

	public String getOp() {
		return op;
	}

    public IValue eval(Env<IValue> e) throws InterpreterError {
		IValue v1 = lhs.eval(e);
		IValue v2 = rhs.eval(e);
		if (v1 instanceof VInt vi1 && v2 instanceof VInt vi2) {
			int i1 = vi1.getval();
			int i2 = vi2.getval();
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
			String s1 = v1 instanceof VString ? ((VString) v1).getval() : v1.toString();
			String s2 = v2 instanceof VString ? ((VString) v2).getval() : v2.toString();
			return new VString(s1 + s2);
		} else {
			String types = (op == "-u" ? "" : (v1 + " and ")) + v2;
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

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		ASTType tl = lhs.typecheck(e);
		ASTType tr = rhs.typecheck(e);
		if (tl instanceof ASTTInt && tr instanceof ASTTInt) {
			return new ASTTInt();
		} else if ((tl instanceof ASTTInt || tl instanceof ASTTLInt) && (tr instanceof ASTTInt || tr instanceof ASTTLInt)) {
			return new ASTTLInt();
		} else if ((tl instanceof ASTTInt || tl instanceof ASTTString) && (tr instanceof ASTTInt || tr instanceof ASTTString) && op == "+") {
			return new ASTTString();
		} else {
			String types = (op == "-u" ? "" : (tl + " and ")) + tr;
			if (op == "-u")
				throw new TypeCheckError("illegal type to - unary operator: " + types);
			else
				throw new TypeCheckError("illegal types to " + op + " operator: " + types);
		}
	}

	public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

	public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
		return new ASTArithOp(lhs.normalize(sigma, sub), rhs.normalize(sigma, sub), op);
    }

	public boolean defequals(ASTNode o, Env<ASTType> sigma) {
		return o instanceof ASTArithOp oop && oop.getLhs().defequals(lhs, sigma)
			&& oop.getRhs().defequals(rhs, sigma) && oop.getOp().equals(op);
	}

	@Override
	public String toString() {
		return String.format("%s %s %s", lhs, op, rhs);
	}
}
