package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.debug.Debug;
import proj.src.env.*;
import proj.src.env.EnvSet.ENV;
import proj.src.env.PureEnvSet.PENV;
import proj.src.errors.*;

import java.util.*;

public class ASTMatch extends ASTNode {
	record MatchCase(String id, ASTNode exp) {
		@Override
		public String toString() {
			return String.format("%s -> %s", id, exp);
		}
	}

	private final ASTNode test;
	private final Map<String, MatchCase> cases;

	public ASTMatch(ASTNode t) {
		test = t; cases = new HashMap<String, MatchCase>();
	}

	public ASTMatch(ASTNode t, Map<String, MatchCase> cs) {
		test = t; cases = cs;
    }

	public ASTNode getTest() { return test; }

	public Set<String> getLabels() { return cases.keySet(); }

	public ASTNode getCaseExp(String label) { return cases.get(label).exp(); }

	public String getCaseId(String label) { return cases.get(label).id(); }

	public void addCase(String label, String id, ASTNode exp) { cases.put(label, new MatchCase(id, exp)); }

    public IValue eval(Env<IValue> e) throws InterpreterError {
		IValue vt = test.eval(e);
		if (vt instanceof VUnion vtu) {
			String testlabel = vtu.getLabel();
			MatchCase c = cases.get(testlabel);
			if (c == null) {
				throw new InterpreterError(ErrorMessages.missingMatchCase(testlabel));
			} else {
				Env<IValue> en = e.beginScope();
				en.assoc(c.id(), ((VUnion) vt).getValue());
				return c.exp().eval(en);
			}
		} else throw new InterpreterError(ErrorMessages.wrongValueToUnary("match", vt));
    }

	public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		Env<ASTType> prevEnv = e.getSigma();
		ASTType tt = test.typecheck(e, null), rettype = null, tcase;
		HashSet<String> matchUsedLinears = null;
		tt = e.unfold(tt);
		if (!(tt instanceof ASTTUnion))
			throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("match", tt));
		EnvSet en = new EnvSet(e), env;
		Set<Map.Entry<String, ASTType>> entries = ((ASTTUnion) tt).getMap().entrySet();
		for (Map.Entry<String, ASTType> entry : entries) {
			Debug.log("##################################");
			Debug.log("TYPECHECKING BRANCH " + entry.getKey());
			Debug.log("##################################");
			MatchCase c = cases.get(entry.getKey());
			if (c == null)
				throw new TypeCheckError(ErrorMessages.missingMatchCase(entry.getKey()));
			env = (matchUsedLinears == null ? e : new EnvSet(en));
			ASTType tlabel = e.unfold(entry.getValue());

			ENV envChoice = tlabel.isLinear() ? ENV.DELTA : ENV.GAMMA;
			env.openEnvScope(envChoice);
			Binder<ASTType> b = env.bindToEnv(envChoice, c.id(), tlabel);
			env.openEnvScope(ENV.SIGMA);
			env.bindToEnv(ENV.SIGMA, c.id(), b);

			ASTUnion eqterm = new ASTUnion(entry.getKey(), new ASTId(c.id(), b.getId()), tt.isLinear());
			env.bindToEnv(ENV.SIGMA, env.getFreshId(), new ASTTEq(test, eqterm, tt));

			if (c.exp() instanceof ASTNever never) never.setFields(prevEnv, entry.getKey(), test);
			tcase = c.exp().typecheck(env, target);

			if (matchUsedLinears == null) {
				matchUsedLinears = new HashSet<String>(e.getUsedLinears());
				matchUsedLinears.remove(c.id());
			}

			HashSet<String> caseUsedLineares = new HashSet<String>(env.getUsedLinears());
			if (entry.getValue().isLinear() && !caseUsedLineares.contains(c.id()))
				throw new TypeCheckError(ErrorMessages.unusedLinearValues(c.id()));
			caseUsedLineares.remove(c.id());
			if (!caseUsedLineares.equals(matchUsedLinears))
				throw new TypeCheckError(ErrorMessages.branchesDifferentLinears(caseUsedLineares, matchUsedLinears));
			
			if (target == null) {
				if (rettype == null || rettype.isSubtypeOf(tcase, new PureEnvSet(env)))
					rettype = tcase;
				else if (!tcase.isSubtypeOf(rettype, new PureEnvSet(env)))
					throw new TypeCheckError(ErrorMessages.branchesDifferentTypes(tcase, rettype));
			} else {
				rettype = target;
				if (!tcase.isSubtypeOf(target, new PureEnvSet(env)))
					throw new TypeCheckError(ErrorMessages.notSubtype(tcase, target));
			}

			env.closeEnvScope(envChoice);
			env.closeEnvScope(ENV.SIGMA);
		}
		return rettype;
	}

	public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
		ASTType tt = test.puretypecheck(pe, null), rettype = null, tcase;
		tt = pe.unfold(tt);
		if (!(tt instanceof ASTTUnion))
			throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("match", tt));
		Set<Map.Entry<String, ASTType>> entries = ((ASTTUnion) tt).getMap().entrySet();
		for (Map.Entry<String, ASTType> entry : entries) {
			MatchCase c = cases.get(entry.getKey());
			if (c == null)
				throw new TypeCheckError(ErrorMessages.missingMatchCase(entry.getKey()));
			ASTType tlabel = pe.unfold(entry.getValue());

			pe.openEnvScope(PENV.SIGMA);
			Binder<ASTType> b = pe.bindToEnv(PENV.SIGMA, c.id(), tlabel);

			ASTUnion eqterm = new ASTUnion(entry.getKey(), new ASTId(c.id(), b.getId()), tt.isLinear());
			pe.bindToEnv(PENV.SIGMA, pe.getFreshId(), new ASTTEq(test, eqterm, tt));

			if (c.exp() instanceof ASTNever never) never.setFields(pe.getSigma(), entry.getKey(), test);
			tcase = c.exp().puretypecheck(pe, target);
			
			if (target == null) {
				if (rettype == null || rettype.isSubtypeOf(tcase, pe))
					rettype = tcase;
				else if (!tcase.isSubtypeOf(rettype,pe))
					throw new TypeCheckError(ErrorMessages.branchesDifferentTypes(tcase, rettype));
			} else {
				rettype = target;
				if (!tcase.isSubtypeOf(target, pe))
					throw new TypeCheckError(ErrorMessages.notSubtype(tcase, target));
			}

			pe.closeEnvScope(PENV.SIGMA);
		}
		return rettype;
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		ASTNode tn = test.weaknorm(sub);
		if (tn instanceof ASTUnion un) {
			MatchCase c = cases.get(un.getLabel());
			ASTNode body = c.exp(), expn = un.getExpr().weaknorm(sub);
			Env<ASTNode> env = sub.beginScope();
			env.assoc(c.id(), expn);
			return body.weaknorm(env);
		} else {
			Map<String, MatchCase> newcases = new HashMap<String, MatchCase>();
			cases.forEach((label, c) -> newcases.put(label, new MatchCase(c.id(), c.exp().weaknorm(sub))));
			return new ASTMatch(tn, newcases);
		}
    }

	public ASTMatch solve(Env<ASTType> sigma) {
		ASTNode ntest = test.solve(sigma);
		return ntest == null ? null : new ASTMatch(ntest, cases);
    }

	public ASTMatch subs(String subsId, ASTNode node) {
		for (String label : cases.keySet()) {
			MatchCase c = cases.get(label);
			ASTNode exps = c.exp().subs(subsId, node);
			cases.put(label, new MatchCase(c.id(), exps));
		}
		return new ASTMatch(test.subs(subsId, node), cases);
	}

	@Override
	public String toString() {
		return String.format("match(%s, %s)", test, cases);
	}
}
