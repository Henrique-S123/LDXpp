package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTCons implements ASTNode {

    ASTNode first, second;

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue v1 = first.eval(e);
		IValue v2 = second.eval(e);
		if (v2 instanceof VCons || v2 instanceof VNil) {
			return new VCons(v1, v2);
		} else {
			throw new InterpreterError("cons operator: cons or nil expected for second value, found " + v2);
		}
    }

    public ASTCons(ASTNode f, ASTNode s) {
		first = f;
		second = s;
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
		ASTType t1 = first.typecheck(e);
		ASTType t2 = second.typecheck(e);
		if (t2 instanceof ASTTList) {
			if (t1.isSubtypeOf(((ASTTList) t2).getType(), e)) {
				return t2;
			} else if (((ASTTList) t2).getType() instanceof ASTTUnit) {
				((ASTTList) t2).setType(t1);
				return t2;
			} else {
				throw new TypeCheckError("types for cons first and second values are not subtypes: " + t1.toStr() + " and " + t2.toStr());
			}
		} else {
			throw new TypeCheckError("illegal type for cons operator second value: " + t2.toStr());
		}
	}

}
