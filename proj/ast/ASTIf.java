package proj.ast;

import proj.values.*;
import proj.types.*;

import java.util.HashSet;

import proj.env.*;
import proj.errors.*;

public class ASTIf implements ASTNode {

    ASTNode test, conseq, alt;

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue vt = test.eval(e);
		if (!(vt instanceof VBool || vt instanceof VLBool)) {
			throw new InterpreterError("if: bool condition expected, found " + vt);
		} else {
			boolean val = (vt instanceof VBool ? ((VBool) vt).getval() : ((VLBool) vt).getval());
			if (val) {
				return conseq.eval(e);
			} else {
				return alt.eval(e);
			}
		}
    }

    public ASTIf(ASTNode t, ASTNode c, ASTNode a) {
		test = t;
		conseq = c;
		alt = a;
    }

	public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
		ASTType tt = test.typecheck(e);
		if (tt instanceof ASTTBool || tt instanceof ASTTLBool) {
			Environment<ASTType> e2 = e.copy();
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

}
