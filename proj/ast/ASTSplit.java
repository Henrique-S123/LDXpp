package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.defeq.TermClosure;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTSplit extends ASTNode {
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

    public IValue eval(Env<IValue> e) throws InterpreterError {
		IValue v = pair.eval(e);
		if (v instanceof VPair vp) {
			Env<IValue> en = e.beginScope();
			en.assoc(id1, vp.getFirst());
			en.assoc(id2, vp.getSecond());
			return body.eval(en);
		} else throw new InterpreterError(ErrorMessages.wrongValueToUnary("split", v));
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        return typecheck(e, null);
    }

	public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError, EnvironmentError {
		if (id1.equals(id2)) throw new TypeCheckError(ErrorMessages.splitIdsMustBeDifferent());
		ASTType tt = pair.typecheck(e);
		tt = e.unfold(tt);
		if (!(tt instanceof ASTTTensor ttensor))
			throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("split", tt));

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

		ASTType rt = (target == null) ? body.typecheck(e) : body.typecheck(e, target);
		if (!e.getUnusedScopeLinears().isEmpty()) throw new TypeCheckError(ErrorMessages.unusedLinearValues(e.getUnusedLinears()));

		if (lin1 || lin2) e.closeEnvScope(ENV.DELTA);
		if (!lin1 || !lin2) e.closeEnvScope(ENV.GAMMA);
		e.closeEnvScope(ENV.SIGMA);
		return rt;
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		ASTNode pn = pair.weaknorm(sub);
		ASTNode f, s;
		if (pn instanceof ASTTensor t) { f = t.getFirst(); s = t.getSecond(); }
		else return new ASTSplit(pn, id2, id1, body.weaknorm(sub));

		ASTNode fn = f.weaknorm(sub), sn = s.weaknorm(sub);
		Env<ASTNode> env = sub.beginScope();
		env.assoc(id1, fn);
		env.assoc(id2, sn);
        return body.weaknorm(env);
    }

	public TermClosure solve(Env<ASTType> sigma) {
        TermClosure npair = pair.solve(sigma);
        if (npair == null) return null;
        Env<ASTType> sig = sigma;
        if (npair.term() instanceof ASTTensor t) sig = t.getSig();
        return new TermClosure(new ASTSplit(npair.term(), id1, id2, body), sig);
    }

	public ASTNode subs(String subsId, ASTNode node) {
		return new ASTSplit(pair.subs(subsId, node), id1, id2, body.subs(subsId, node));
	}

	@Override
	public String toString() {
		return String.format("split %s {%s|%s -> %s}", pair, id1, id2, body);
	}
}
