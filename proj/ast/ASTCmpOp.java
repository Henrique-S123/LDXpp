package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTCmpOp implements ASTNode {
    ASTNode lhs, rhs;
	String op;

	public ASTCmpOp(ASTNode l, ASTNode r, String o) {
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
			boolean res = switch (op) {
				case "==" -> i1 == i2;
				case "~=" -> i1 != i2;
				case ">" -> i1 > i2;
				case ">=" -> i1 >= i2;
				case "<" -> i1 < i2;
				case "<=" -> i1 <= i2;
				default -> throw new InterpreterError("unknown operation");
			};
			boolean lin = (((VInt) v1).islin() || ((VInt) v2).islin());
			return new VBool(res, lin);
		} else {
			throw new InterpreterError(op + " operator: integers expected, found " + v1 + " and " + v2);
		}
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		ASTType tl = lhs.typecheck(e);
		ASTType tr = rhs.typecheck(e);
		if (tl instanceof ASTTInt && tr instanceof ASTTInt) {
			return new ASTTBool();
		} else if ((tl instanceof ASTTInt || tl instanceof ASTTLInt) && (tr instanceof ASTTInt || tr instanceof ASTTLInt)) {
			return new ASTTLBool();
		} else {
			throw new TypeCheckError("illegal types to " + op + " operator: " + tl + " and " + tr);
		}
	}

	public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

	public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
		ASTNode ln = lhs.normalize(sigma, sub);
		ASTNode rn = rhs.normalize(sigma, sub);
		if ((ln instanceof ASTInt || ln instanceof ASTLInt) || (rn instanceof ASTInt || rn instanceof ASTLInt)) {
			int i1 = (ln instanceof ASTInt) ? ((ASTInt) ln).getVal() : ((ASTLInt) ln).getVal();
			int i2 = (rn instanceof ASTInt) ? ((ASTInt) rn).getVal() : ((ASTLInt) rn).getVal();
			boolean res = switch (op) {
				case "==" -> i1 == i2;
				case "~=" -> i1 != i2;
				case ">" -> i1 > i2;
				case ">=" -> i1 >= i2;
				case "<" -> i1 < i2;
				case "<=" -> i1 <= i2;
				default -> false; // unreachable code
			};
			return (ln instanceof ASTInt && rn instanceof ASTInt) ? new ASTBool(res) : new ASTLBool(res);
		}
		return new ASTCmpOp(lhs.normalize(sigma, sub), rhs.normalize(sigma, sub), op);
    }

	public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
		return o instanceof ASTCmpOp oop && oop.getLhs().defequals(lhs, sigma, alpha)
			&& oop.getRhs().defequals(rhs, sigma, alpha) && oop.getOp().equals(op);
	}

	@Override
	public String toString() {
		return String.format("%s %s %s", lhs, op, rhs);
	}
}
