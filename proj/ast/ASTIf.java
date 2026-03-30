package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.HashSet;

public class ASTIf implements ASTNode {
    ASTNode test, conseq, alt;

	public ASTIf(ASTNode t, ASTNode c, ASTNode a) {
		test = t;
		conseq = c;
		alt = a;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue vt = test.eval(e);
		if (!(vt instanceof VBool)) {
			throw new InterpreterError("if: bool condition expected, found " + vt);
		} else {
			boolean val = ((VBool) vt).getval();
			return val ? conseq.eval(e) : alt.eval(e);
		}
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, InterpreterError {
		ASTType tt = test.typecheck(e);
		if (tt instanceof ASTTBool || tt instanceof ASTTLBool) {
			EnvSet e2 = new EnvSet(e);
			ASTType vconseq = conseq.typecheck(e);
			ASTType valt = alt.typecheck(e2);
			if (!new HashSet<String>(e.getUsedLinears()).equals(new HashSet<String>(e2.getUsedLinears())))
				throw new TypeCheckError("if conseq and alt branches must use the same linear values");
			if (vconseq.isSubtypeOf(valt, e)) {
				return vconseq;
			} else if (valt.isSubtypeOf(vconseq, e)) {
				return valt;
			} else {
				throw new TypeCheckError("if conseq and alt branches do not have compatible types: " + vconseq.toStr() + " and " + valt.toStr());
			}
		} else {
			throw new TypeCheckError("illegal type to if test: " + tt.toStr());
		}
	}

	public ASTNode normalize() {
        return this;
    }
}
