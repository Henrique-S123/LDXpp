package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.env.EnvSet.ENV;
import proj.src.env.PureEnvSet.PENV;
import proj.src.errors.*;

public class ASTSplit extends ASTNode {
    private final ASTNode pair, body;
	private final String id1, id2;
	private final boolean linpair;

	public ASTSplit(ASTNode p, String i1, String i2, ASTNode b, boolean l) {
		pair = p;
		id1 = i1;
		id2 = i2;
		body = b;
		linpair = l;
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

	public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		ASTType tt = pair.typecheck(e, null);
		tt = e.unfold(tt);
		this.setSig(e.getSigma());
		if (!(tt instanceof ASTTPair tpair && tpair.isLinear() == linpair))
			throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("split", tt));

		ASTType t1 = e.unfold(tpair.getFirst());
		Binder<ASTType> b1 = new Binder<ASTType>(t1);

		ASTTPair instttensor = (tpair.getBid() != null) ? tpair.inst(tpair.getBid(), new ASTId(id1, b1.getId())) : tpair;
		ASTType t2 = e.unfold(instttensor.getSecond());
		Binder<ASTType> b2 = new Binder<ASTType>(t2);

		boolean lin1 = t1.isLinear(), lin2 = t2.isLinear();
		if (lin1 || lin2) e.openEnvScope(ENV.DELTA);
		if (!lin1 || !lin2) e.openEnvScope(ENV.GAMMA);
		e.openEnvScope(ENV.SIGMA);
        e.bindToEnv(lin1 ? ENV.DELTA : ENV.GAMMA, id1, b1);
        e.bindToEnv(lin2 ? ENV.DELTA : ENV.GAMMA, id2, b2);
		e.bindToEnv(ENV.SIGMA, id1, b1);
		e.bindToEnv(ENV.SIGMA, id2, b2);
		e.bindToEnv(ENV.SIGMA, e.getFreshId(), new ASTTEq(new ASTPair(new ASTId(id1, b1.getId()), new ASTId(id2, b2.getId()), tpair.isLinear()), pair, instttensor));

		ASTType rt = body.typecheck(e, target);
		if (!e.getUnusedScopeLinears().isEmpty()) throw new TypeCheckError(ErrorMessages.unusedLinearValues(e.getUnusedLinears()));

		if (lin1 || lin2) e.closeEnvScope(ENV.DELTA);
		if (!lin1 || !lin2) e.closeEnvScope(ENV.GAMMA);
		e.closeEnvScope(ENV.SIGMA);
		return rt;
	}

	public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
		ASTType tt = pair.puretypecheck(pe, null);
		tt = pe.unfold(tt);
		this.setSig(pe.getSigma());
		if (!(tt instanceof ASTTPair tpair && tpair.isLinear() == linpair))
			throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("split", tt));

		ASTType t1 = pe.unfold(tpair.getFirst());
		ASTType t2 = pe.unfold(tpair.getSecond());

		pe.openEnvScope(PENV.SIGMA);
		pe.bindToEnv(PENV.SIGMA, id1, t1);
		pe.bindToEnv(PENV.SIGMA, id2, t2);
		ASTTEq newterm = new ASTTEq(new ASTPair(new ASTId(id1), new ASTId(id2), linpair), pair, tt);
		pe.bindToEnv(PENV.SIGMA, pe.getFreshId(), newterm);
		return body.puretypecheck(pe, target);
    }

	public ASTNode weaknorm(Env<ASTNode> sub) {
		ASTNode pn = pair.weaknorm(sub);
		ASTNode f, s;
		if (pn instanceof ASTPair t && t.isLinear()) { f = t.getFirst(); s = t.getSecond(); }
		else return new ASTSplit(pn, id1, id2, body.weaknorm(sub), linpair);

		ASTNode fn = f.weaknorm(sub), sn = s.weaknorm(sub);
		Env<ASTNode> env = sub.beginScope();
		env.assoc(id1, fn);
		env.assoc(id2, sn);
        return body.weaknorm(env);
    }

	public ASTSplit solve(Env<ASTType> sigma) {
        ASTNode npair = pair.solve(sigma);
		return (npair == null) ? null : new ASTSplit(npair, id1, id2, body, linpair);
    }

	public ASTSplit subs(String subsId, ASTNode node) {
		return new ASTSplit(pair.subs(subsId, node), id1, id2, body.subs(subsId, node), linpair);
	}

	@Override
	public String toString() {
		return String.format("split (%s|%s) = %s; %s", id1, id2, pair, body);
	}
}
