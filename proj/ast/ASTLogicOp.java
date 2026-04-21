package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTLogicOp implements ASTNode {
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

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue v1 = lhs.eval(e);
		IValue v2 = rhs.eval(e);
		if (v1 instanceof VBool && v2 instanceof VBool) {
			boolean i1 = ((VBool) v1).getval();
			boolean i2 = ((VBool) v2).getval();
			boolean res = switch (op) {
				case "&&" -> i1 && i2;
				case "||" -> i1 || i2;
				case "~" -> !i2;
				default -> throw new InterpreterError("unknown operation");
			};
			boolean lin = (((VBool) v1).islin() || ((VBool) v2).islin());
			return new VBool(res, lin);
		} else {
			String types = (op == "~" ? "" : (v1.toStr() + " and ")) + v2.toStr();
			if (op == "~")
				throw new InterpreterError("~ unary operator: boolean expected, found " + types);
			else
				throw new InterpreterError(op + "operator: booleans expected, found " + types);
		}
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		ASTType tl = lhs.typecheck(e);
		ASTType tr = rhs.typecheck(e);
		if (tl instanceof ASTTBool && tr instanceof ASTTBool) {
			return new ASTTBool();
		} else if ((tl instanceof ASTTBool || tl instanceof ASTTLBool) && (tr instanceof ASTTBool || tr instanceof ASTTLBool)) {
			return new ASTTLBool();
		} else {
			String types = (op == "~" ? "" : (tl.toStr() + " and ")) + tr.toStr();
			if (op == "~")
				throw new TypeCheckError("illegal type to ~ unary operator: " + types);
			else
				throw new TypeCheckError("illegal types to " + op + " operator: " + types);
		}
	}

	public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

	public ASTNode normalize(Environment<ASTType> sigma) {
		return new ASTLogicOp(lhs.normalize(sigma), rhs.normalize(sigma), op);
    }

	public boolean defequals(ASTNode o) {
		return o instanceof ASTLogicOp && ((ASTLogicOp) o).getLhs().defequals(lhs)
			&& ((ASTLogicOp) o).getRhs().defequals(rhs) && ((ASTLogicOp) o).getOp().equals(op);
	}

	public String toString() {
		return String.format("%s %s %s", lhs.toString(), op, rhs.toString());
	}
}
