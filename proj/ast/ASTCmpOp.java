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

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue v1 = lhs.eval(e);
		IValue v2 = rhs.eval(e);
		if (v1 instanceof VInt && v2 instanceof VInt) {
			int i1 = ((VInt) v1).getval();
			int i2 = ((VInt) v2).getval();
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

	public ASTType typecheck(Environment<ASTType> te) throws TypeCheckError, InterpreterError {
		ASTType tl = lhs.typecheck(te);
		ASTType tr = rhs.typecheck(te);
		if (tl instanceof ASTTInt && tr instanceof ASTTInt) {
			return new ASTTBool();
		} else if ((tl instanceof ASTTInt || tl instanceof ASTTLInt) && (tr instanceof ASTTInt || tr instanceof ASTTLInt)) {
			return new ASTTLBool();
		} else {
			throw new TypeCheckError("illegal types to " + op + " operator: " + tl.toStr() + " and " + tr.toStr());
		}
	}
}
