package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTLogicOp extends ASTNode {
    ASTNode lhs, rhs;
	String op;

	public ASTLogicOp(ASTNode l, ASTNode r, String o) {
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
		if (v1 instanceof VBool vb1 && v2 instanceof VBool vb2) {
			boolean i1 = vb1.getval();
			boolean i2 = vb2.getval();
			boolean res = switch (op) {
				case "&&" -> i1 && i2;
				case "||" -> i1 || i2;
				case "~" -> !i2;
				default -> throw new InterpreterError(ErrorMessages.unexpectedOperation(op));
			};
			boolean lin = (((VBool) v1).islin() || ((VBool) v2).islin());
			return new VBool(res, lin);
		} else {
			if (op == "~") throw new InterpreterError(ErrorMessages.wrongValueToUnary("~", v2));
			else throw new InterpreterError(ErrorMessages.wrongValueToBinary(op, v1, v2));
		}
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		ASTType tl = lhs.typecheck(e);
		ASTType tr = rhs.typecheck(e);
		if (tl instanceof ASTTBool && tr instanceof ASTTBool) return new ASTTBool();
		else if ((tl instanceof ASTTBool || tl instanceof ASTTLBool) && (tr instanceof ASTTBool || tr instanceof ASTTLBool))
			return new ASTTLBool();
		else {
			if (op == "~") throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("unary ~", tr));
			else throw new TypeCheckError(ErrorMessages.illegalTypeToBinary(op, tl, tr));
		}
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		ASTNode ln = lhs.weaknorm(sub);
		ASTNode rn = rhs.weaknorm(sub);
		if ((ln instanceof ASTBool || ln instanceof ASTLBool) && (rn instanceof ASTBool || rn instanceof ASTLBool)) {
			boolean i1 = (ln instanceof ASTBool) ? ((ASTBool) ln).getVal() : ((ASTLBool) ln).getVal();
			boolean i2 = (rn instanceof ASTBool) ? ((ASTBool) rn).getVal() : ((ASTLBool) rn).getVal();
			boolean res = switch (op) {
				case "&&" -> i1 && i2;
				case "||" -> i1 || i2;
				case "~" -> !i2;
				default -> false; // unreachable code
			};
			return (ln instanceof ASTBool && rn instanceof ASTBool) ? new ASTBool(res) : new ASTLBool(res);
		}
		return new ASTLogicOp(lhs.weaknorm(sub), rhs.weaknorm(sub), op);
    }

	public ASTNode solve(Env<ASTType> sigma) {
        ASTNode nlhs = lhs.solve(sigma);
        if (nlhs != null) return new ASTLogicOp(nlhs, rhs, op);
        ASTNode nrhs = rhs.solve(sigma);
        if (nrhs != null) return new ASTLogicOp(lhs, nrhs, op);
        return null;
    }

	public ASTNode subs(String subsId, ASTNode node) {
        return new ASTLogicOp(lhs.subs(subsId, node), rhs.subs(subsId, node), op);
    }

	public boolean defequals(Env<ASTType> sl, ASTNode o, Env<ASTType> sr, AlphaEnv alpha) {
		return o instanceof ASTLogicOp oop && op.equals(oop.getOp())
			&& lhs.defequals(sl, oop.getLhs(), sr, alpha) && rhs.defequals(sl, oop.getRhs(), sr, alpha);
	}

	@Override
	public String toString() {
		if (op == "~") return String.format("~%s", rhs);
		return String.format("%s %s %s", lhs, op, rhs);
	}
}
