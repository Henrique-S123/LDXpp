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
		ASTNode ln = lhs.normalize(sigma, sub);
		ASTNode rn = rhs.normalize(sigma, sub);
		if ((ln instanceof ASTInt || ln instanceof ASTLInt) && (rn instanceof ASTInt || rn instanceof ASTLInt)) {
			int i1 = (ln instanceof ASTInt) ? ((ASTInt) ln).getVal() : ((ASTLInt) ln).getVal();
			int i2 = (rn instanceof ASTInt) ? ((ASTInt) rn).getVal() : ((ASTLInt) rn).getVal();
			int res = switch (op) {
				case "+" -> i1 + i2;
				case "-" -> i1 - i2;
				case "*" -> i1 * i2;
				case "/" -> i1 / i2;
				case "-u" -> -i2;
				default -> -1; // unreachable code
			};
			return (ln instanceof ASTInt && rn instanceof ASTInt) ? new ASTInt(res) : new ASTLInt(res);
		}
		else if (op == "+" && (ln instanceof ASTString || rn instanceof ASTString) &&
				(ln instanceof ASTInt || ln instanceof ASTLInt || ln instanceof ASTString) &&
				(rn instanceof ASTInt || rn instanceof ASTLInt || rn instanceof ASTString)) {
			String s1 = "";
			if (ln instanceof ASTInt n) s1 = "" + n.getVal();
			if (ln instanceof ASTLInt n) s1 = "" + n.getVal();
			if (ln instanceof ASTString n) s1 = n.getVal();
			String s2 = "";
			if (rn instanceof ASTInt n) s2 = "" + n.getVal();
			if (rn instanceof ASTLInt n) s2 = "" + n.getVal();
			if (rn instanceof ASTString n) s2 = n.getVal();
			return new ASTString(s1 + s2);
		}
		// TODO: add string concat
		return new ASTArithOp(lhs.normalize(sigma, sub), rhs.normalize(sigma, sub), op);
    }

	public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
		return o instanceof ASTArithOp oop && oop.getLhs().defequals(lhs, sigma, alpha)
			&& oop.getRhs().defequals(rhs, sigma, alpha) && oop.getOp().equals(op);
	}

	@Override
	public String toString() {
		if (op == "-u") return String.format("-%s", rhs);
		return String.format("%s %s %s", lhs, op, rhs);
	}
}
