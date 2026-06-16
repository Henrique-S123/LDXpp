package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.defeq.TermClosure;
import proj.env.*;
import proj.errors.*;

public class ASTCmpOp extends ASTNode {
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
				default -> throw new InterpreterError(ErrorMessages.unexpectedOperation(op));
			};
			boolean lin = (((VInt) v1).islin() || ((VInt) v2).islin());
			return new VBool(res, lin);
		} else throw new InterpreterError(ErrorMessages.wrongValueToBinary(op, v1, v2));
    }

	public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		ASTType tl = lhs.typecheck(e, null);
		ASTType tr = rhs.typecheck(e, null);
		if (tl instanceof ASTTInt && tr instanceof ASTTInt) return new ASTTBool();
		else if ((tl instanceof ASTTInt || tl instanceof ASTTLInt) && (tr instanceof ASTTInt || tr instanceof ASTTLInt))
			return new ASTTLBool();
		else throw new TypeCheckError(ErrorMessages.illegalTypeToBinary(op, tl, tr));
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		ASTNode ln = lhs.weaknorm(sub);
		ASTNode rn = rhs.weaknorm(sub);
		if ((ln instanceof ASTInt || ln instanceof ASTLInt) && (rn instanceof ASTInt || rn instanceof ASTLInt)) {
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
		return new ASTCmpOp(lhs.weaknorm(sub), rhs.weaknorm(sub), op);
    }

	public TermClosure solve(Env<ASTType> sigma) {
		TermClosure nlhs = lhs.solve(sigma);
        if (nlhs != null) return new TermClosure(new ASTCmpOp(nlhs.term(), rhs, op), sigma);
        TermClosure nrhs = rhs.solve(sigma);
        if (nrhs != null) return new TermClosure(new ASTCmpOp(lhs, nrhs.term(), op), sigma);
        return null;
    }

	public ASTNode subs(String subsId, ASTNode node) {
        return new ASTCmpOp(lhs.subs(subsId, node), rhs.subs(subsId, node), op);
    }

	@Override
	public String toString() {
		return String.format("%s %s %s", lhs, op, rhs);
	}
}
