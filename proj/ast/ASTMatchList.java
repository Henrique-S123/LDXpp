package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTMatchList implements ASTNode {

    ASTNode test, nilcase, conscase;
	String id1, id2;

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue vt = test.eval(e);
		if (vt instanceof VNil) { return nilcase.eval(e);

		} else if (vt instanceof VCons || (vt instanceof VLcons &&
				((VLcons) vt).getFirstValue() != null &&
				((VLcons) vt).getSecondValue() != null)) {
			IValue v1, v2;
			if (vt instanceof VCons) {
				v1 = ((VCons) vt).getFirst();
				v2 = ((VCons) vt).getSecond();
			} else {
				v1 = ((VLcons) vt).getFirstValue();
				v2 = ((VLcons) vt).getSecondValue();
			}
			Environment<IValue> env = e.beginScope();
			env.assoc(id1, v1);
			env.assoc(id2, v2);
			return conscase.eval(env);

		} else if (vt instanceof VLcons) {
			IValue vfirst = ((VLcons) vt).getFirst().eval(((VLcons) vt).getEnv());
			IValue vsecond = ((VLcons) vt).getSecond().eval(((VLcons) vt).getEnv());
			((VLcons) vt).setFirst(vfirst);
			((VLcons) vt).setSecond(vsecond);
			Environment<IValue> env = e.beginScope();
			env.assoc(id1, vfirst);
			env.assoc(id2, vsecond);
			return conscase.eval(env);

		} else
			throw new InterpreterError("match: nil, cons or lcons expected, found " + vt);
    }

    public ASTMatchList(ASTNode t, ASTNode nc, String i1, String i2, ASTNode cc) {
		test = t;
		nilcase = nc;
		id1 = i1;
		id2 = i2;
		conscase = cc;
    }

	public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
		if (id1.equals(id2)) throw new TypeCheckError("ids for cons case must be different");
		ASTType tt = test.typecheck(e);
		tt = e.unfold(tt);
		if (tt instanceof ASTTList) {
			ASTType tlist = ((ASTTList) tt).getType();
			ASTType tnil = nilcase.typecheck(e);
			Environment<ASTType> en = e.beginScope();
			en.assoc(id1, e.unfold(tlist));
			en.assoc(id2, e.unfold(tt));
			ASTType tcons = conscase.typecheck(en);
			if (tnil.isSubtypeOf(tcons, e) && tcons.isSubtypeOf(tnil, e)) {
				return tnil;
			} else {
				throw new TypeCheckError("different types to match nil and cons case: " + tnil.toStr() + " and " + tcons.toStr());
			}
		} else {
			throw new TypeCheckError("illegal type to match test: " + tt.toStr());
		}
	}
}
