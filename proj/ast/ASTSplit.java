package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTSplit implements ASTNode {
    ASTNode pair, body;
	String id1, id2;

	public ASTSplit(ASTNode p, String i1, String i2, ASTNode b) {
		pair = p;
		id1 = i1;
		id2 = i2;
		body = b;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue v = pair.eval(e);
		if (v instanceof VPair) {
			Environment<IValue> en = e.beginScope();
			en.assoc(id1, ((VPair) v).getFirst());
			en.assoc(id2, ((VPair) v).getSecond());
			return body.eval(en);
		} else {
			throw new InterpreterError("split: linear pair expected, found " + v);
		}
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		if (id1.equals(id2)) throw new TypeCheckError("ids for split must be different");
		ASTType tt = pair.typecheck(e);
		tt = e.unfold(tt);
		if (!(tt instanceof ASTTTensor))
			throw new TypeCheckError("illegal type to split: " + tt.toStr());
		ASTType t1 = e.unfold(((ASTTTensor) tt).getFirst());
		ENV env = (t1 instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
        e.openEnvScope(env);
        e.bindToEnv(env, id1, t1);
		ASTType t2 = e.unfold(((ASTTTensor) tt).getSecond());
		boolean t1Lin = (t1 instanceof ASTLinType), t2Lin = (t2 instanceof ASTLinType);
		if (t2Lin) {
			if (!t1Lin) e.openEnvScope(ENV.DELTA);
			e.assocDelta(id2, t2);
		} else {
			if (t1Lin) e.openEnvScope(ENV.GAMMA);
			e.assocGamma(id2, t2);
		}
		ASTType rt = body.typecheck(e);
		if (!(e.getEnv(ENV.DELTA).isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + e.getEnv(ENV.DELTA).toStr());
		if (t1Lin || t2Lin) e.closeEnvScope(ENV.DELTA);
		else if (!t1Lin || !t2Lin) e.closeEnvScope(ENV.GAMMA);
		return rt;
	}

	public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

	public boolean defequals(ASTNode o) {
        // TODO
        return false;
    }
}
