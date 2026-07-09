package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.debug.Debug;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

import java.util.*;

public class ASTMatch extends ASTNode {
	private final ASTNode test;
	private final Map<String, MatchCase> cases;

	public ASTMatch(ASTNode t, Map<String, MatchCase> cs) {
		test = t;
		cases = cs;
    }

	public ASTNode getTest() {
		return test;
	}

	public Map<String, MatchCase> getCases() {
		return cases;
	}

    public IValue eval(Env<IValue> e) throws InterpreterError {
		IValue vt = test.eval(e);
		if (vt instanceof VUnion vtu) {
			String testlabel = vtu.getLabel();
			MatchCase c = cases.get(testlabel);
			if (c == null) {
				throw new InterpreterError(ErrorMessages.missingMatchCase(testlabel));
			} else {
				Env<IValue> en = e.beginScope();
				en.assoc(c.getId(), ((VUnion) vt).getValue());
				return c.getExp().eval(en);
			}
		} else throw new InterpreterError(ErrorMessages.wrongValueToUnary("match", vt));
    }

	public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		ASTType tt = test.typecheck(e, null), rettype = null, tcase;
		HashSet<String> matchUsedLinears = null;
		tt = e.unfold(tt);
		if (!(tt instanceof ASTTUnion || tt instanceof ASTTLUnion))
			throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("match", tt));
		EnvSet en = new EnvSet(e), env;
		Set<Map.Entry<String, ASTType>> entries = tt instanceof ASTTUnion ?
			((ASTTUnion) tt).getMap().entrySet() :
			((ASTTLUnion) tt).getMap().entrySet();
		for (Map.Entry<String, ASTType> entry : entries) {
			Debug.log("TYPECHECKING BRANCH " + entry.getKey());
			MatchCase c = cases.get(entry.getKey());
			if (c == null)
				throw new TypeCheckError(ErrorMessages.missingMatchCase(entry.getKey()));

			env = (matchUsedLinears == null ? e : new EnvSet(en));
			ASTType tlabel = e.unfold(entry.getValue());

			ENV envChoice = (tlabel instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
			env.openEnvScope(envChoice);
			env.openEnvScope(ENV.SIGMA);
			Binder<ASTType> b = new Binder<ASTType>(tlabel);
			env.bindToEnv(envChoice, c.getId(), b);
			env.bindToEnv(ENV.SIGMA, c.getId(), b);
			env.addEq(new ASTTEq(test, new ASTUnion(entry.getKey(), new ASTId(c.getId(), b.getId())), tt));
			tcase = c.getExp().typecheck(env, target);

			if (matchUsedLinears == null) {
				matchUsedLinears = new HashSet<String>(e.getUsedLinears());
				matchUsedLinears.remove(c.getId());
			}

			HashSet<String> caseUsedLineares = new HashSet<String>(env.getUsedLinears());
			if ((entry.getValue() instanceof ASTLinType) && !caseUsedLineares.contains(c.getId()))
				throw new TypeCheckError(ErrorMessages.unusedLinearValues(c.getId()));
			caseUsedLineares.remove(c.getId());
			if (!caseUsedLineares.equals(matchUsedLinears))
				throw new TypeCheckError(ErrorMessages.branchesDifferentLinears(caseUsedLineares, matchUsedLinears));
			
			if (target == null) {
				if (rettype == null || rettype.isSubtypeOf(tcase, env.getSigma(), env.getPhi(), e.getAlpha()))
					rettype = tcase;
				else if (!tcase.isSubtypeOf(rettype, env.getSigma(), env.getPhi(), e.getAlpha()))
					throw new TypeCheckError(ErrorMessages.branchesDifferentTypes(tcase, rettype));
			} else {
				rettype = target;
				if (!tcase.isSubtypeOf(target, env.getSigma(), env.getPhi(), e.getAlpha()))
					throw new TypeCheckError(ErrorMessages.notSubtype(tcase, target));
			}

			env.closeEnvScope(envChoice);
			env.closeEnvScope(ENV.SIGMA);
		}
		return rettype;
	}

	public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
		ASTType tt = test.puretypecheck(sigma, phi, null), rettype = null, tcase;
		tt = phi.unfold(tt);
		if (!(tt instanceof ASTTUnion || tt instanceof ASTTLUnion))
			throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("match", tt));
		Set<Map.Entry<String, ASTType>> entries = tt instanceof ASTTUnion ?
			((ASTTUnion) tt).getMap().entrySet() :
			((ASTTLUnion) tt).getMap().entrySet();
		for (Map.Entry<String, ASTType> entry : entries) {
			MatchCase c = cases.get(entry.getKey());
			if (c == null)
				throw new TypeCheckError(ErrorMessages.missingMatchCase(entry.getKey()));

			ASTType tlabel = phi.unfold(entry.getValue());
			Env<ASTType> env = sigma.beginScope();
			env.assoc(c.getId(), tlabel);
			env.addEq(new ASTTEq(test, new ASTUnion(entry.getKey(), new ASTId(c.getId())), tt));
			tcase = c.getExp().puretypecheck(env, phi, target);
			
			if (target == null) {
				if (rettype == null || rettype.isSubtypeOf(tcase, env, phi, new AlphaEnv()))
					rettype = tcase;
				else if (!tcase.isSubtypeOf(rettype, env, phi, new AlphaEnv()))
					throw new TypeCheckError(ErrorMessages.branchesDifferentTypes(tcase, rettype));
			} else {
				rettype = target;
				if (!tcase.isSubtypeOf(target, env, phi, new AlphaEnv()))
					throw new TypeCheckError(ErrorMessages.notSubtype(tcase, target));
			}
		}
		return rettype;
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		ASTNode exp, tn = test.weaknorm(sub);
		Map<String, MatchCase> newcases = new HashMap<String, MatchCase>();
		cases.forEach((label, c) -> newcases.put(label, new MatchCase(c.getId(), c.getExp().weaknorm(sub))));
		String label;
		if (tn instanceof ASTUnion un) { exp = un.getExpr(); label = un.getLabel(); }
		else if (tn instanceof ASTLUnion lun) { exp = lun.getExpr(); label = lun.getLabel(); }
		else return new ASTMatch(tn, newcases);

		MatchCase c = cases.get(label);
		String id = c.getId();
		ASTNode body = c.getExp(), expn = exp.weaknorm(sub);

		Env<ASTNode> env = sub.beginScope();
		env.assoc(id, expn);
		return body.weaknorm(env);
    }

	public ASTNode solve(Env<ASTType> sigma) {
		ASTNode ntest = test.solve(sigma);
		return ntest == null ? null : new ASTMatch(ntest, cases);
    }

	public ASTNode subs(String subsId, ASTNode node) {
		for (String label : cases.keySet()) {
			MatchCase c = cases.get(label);
			ASTNode exps = c.getExp().subs(subsId, node);
			cases.put(label, new MatchCase(c.getId(), exps));
		}
		return new ASTMatch(test.subs(subsId, node), cases);
	}

	@Override
	public String toString() {
		return String.format("match(%s, %s)", test, cases);
	}
}
