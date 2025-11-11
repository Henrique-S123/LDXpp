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

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue v1 = lhs.eval(e);
		IValue v2 = rhs.eval(e);
		if ((v1 instanceof VBool || v1 instanceof VLBool) && (v2 instanceof VBool || v2 instanceof VLBool)) {
			boolean i1 = v1 instanceof VLBool ? ((VLBool) v1).getval() : ((VBool) v1).getval();
			boolean i2 = v2 instanceof VLBool ? ((VLBool) v2).getval() : ((VBool) v2).getval();
			boolean res = switch (op) {
				case "&&" -> i1 && i2;
				case "||" -> i1 || i2;
				case "~" -> !i2;
				default -> throw new InterpreterError("unknown operation");
			};
			return (v1 instanceof VBool && v2 instanceof VBool) ? new VBool(res) : new VLBool(res);
		} else {
			String types = (op == "~" ? "" : (v1 + " and ")) + v2.toStr();
			if (op == "~")
				throw new InterpreterError("~ unary operator: boolean expected, found " + types);
			else
				throw new InterpreterError(op + "operator: booleans expected, found " + types);
		}
    }

	public ASTType typecheck(Environment<ASTType> te) throws TypeCheckError, InterpreterError {
		ASTType tl = lhs.typecheck(te);
		ASTType tr = rhs.typecheck(te);
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
}
