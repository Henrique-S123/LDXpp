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

	public String getId1() {
		return id1;
	}

	public String getId2() {
		return id2;
	}

	public ASTNode getPair() {
		return pair;
	}

	public ASTNode getBody() {
		return body;
	}

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue v = pair.eval(e);
		if (v instanceof VPair vp) {
			Environment<IValue> en = e.beginScope();
			en.assoc(id1, vp.getFirst());
			en.assoc(id2, vp.getSecond());
			return body.eval(en);
		} else {
			throw new InterpreterError("split: linear pair expected, found " + v);
		}
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		if (id1.equals(id2)) throw new TypeCheckError("ids for split must be different");
		ASTType tt = pair.typecheck(e);
		tt = e.unfold(tt);
		if (!(tt instanceof ASTTTensor ttensor))
			throw new TypeCheckError("illegal type to split: " + tt);

		ASTType t1 = e.unfold(ttensor.getFirst());
		ASTType t2 = e.unfold(ttensor.getSecond());
		boolean lin1 = (t1 instanceof ASTLinType), lin2 = (t2 instanceof ASTLinType);

		if (lin1 || lin2) e.openEnvScope(ENV.DELTA);
		if (!lin1 || !lin2) e.openEnvScope(ENV.GAMMA);
		e.openEnvScope(ENV.SIGMA);

        e.bindToEnv(lin1 ? ENV.DELTA : ENV.GAMMA, id1, t1);
        e.bindToEnv(lin2 ? ENV.DELTA : ENV.GAMMA, id2, t2);
		e.bindToEnv(ENV.SIGMA, id1, t1);
		e.bindToEnv(ENV.SIGMA, id2, t2);
		e.addEq(new ASTTEq(new ASTTensor(new ASTId(id1), new ASTId(id2)), pair, tt));

		ASTType rt = body.typecheck(e);
		if (!(e.getEnv(ENV.DELTA).isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + e.getEnv(ENV.DELTA));

		if (lin1 || lin2) e.closeEnvScope(ENV.DELTA);
		if (!lin1 || !lin2) e.closeEnvScope(ENV.GAMMA);
		e.closeEnvScope(ENV.SIGMA);
		return rt;
	}

	public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

	public ASTNode normalize(Environment<ASTType> sigma, Environment<ASTNode> sub) {
		ASTNode pn = pair.normalize(sigma, sub);
		ASTNode f, s;
		if (pn instanceof ASTTensor t) { f = t.getFirst(); s = t.getSecond(); }
		else return new ASTSplit(pn, id2, id1, body.normalize(sigma, sub));

		ASTNode fn = f.normalize(sigma, sub), sn = s.normalize(sigma, sub);
		Environment<ASTNode> env = sub.beginScope();
		env.assoc(id1, fn);
		env.assoc(id2, sn);
        return body.normalize(sigma, env);
    }

	public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
		return o instanceof ASTSplit osplit && osplit.getId1().equals(id1)
			&& osplit.getId2().equals(id2) && osplit.getPair().defequals(pair, sigma)
			&& osplit.getBody().defequals(body, sigma);
    }

	@Override
	public String toString() {
		return String.format("split %s {%s|%s -> %s}", pair, id1, id2, body);
	}
}
