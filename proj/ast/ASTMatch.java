package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

import java.util.*;

public class ASTMatch extends ASTNode {
	ASTNode test;
	Map<String, MatchCase> cases;

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

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		ASTType tt = test.typecheck(e), rettype = null, tcase;
		HashSet<String> matchUsedLinears = null;
		tt = e.unfold(tt);
		if (!(tt instanceof ASTTUnion || tt instanceof ASTTLUnion))
			throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("match", tt));
		EnvSet en = new EnvSet(e), env;
		Set<Map.Entry<String, ASTType>> entries = tt instanceof ASTTUnion ?
			((ASTTUnion) tt).getMap().entrySet() :
			((ASTTLUnion) tt).getMap().entrySet();
		for (Map.Entry<String, ASTType> entry : entries) {
			MatchCase c = cases.get(entry.getKey());
			if (c == null)
				throw new TypeCheckError(ErrorMessages.missingMatchCase(entry.getKey()));

			env = (matchUsedLinears == null ? e : new EnvSet(en));
			ASTType tlabel = e.unfold(entry.getValue());

			ENV envChoice = (tlabel instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
			env.openEnvScope(envChoice);
			env.openEnvScope(ENV.SIGMA);
			env.bindToEnv(envChoice, c.getId(), tlabel);
			env.bindToEnv(ENV.SIGMA, c.getId(), tlabel);
			tcase = c.getExp().typecheck(env);
			env.closeEnvScope(envChoice);
			env.closeEnvScope(ENV.SIGMA);

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
			if ((tcase.isSubtypeOf(rettype, env) && rettype.isSubtypeOf(tcase, env)) || rettype == null)
				rettype = tcase;
			else throw new TypeCheckError(ErrorMessages.branchesDifferentTypes(tcase, rettype));
		}
		return rettype;
	}

	public ASTNode weaknorm(Env<ASTType> sigma, Env<ASTNode> sub) {
		ASTNode exp, tn = test.weaknorm(sigma, sub);
		String label;
		if (tn instanceof ASTUnion un) { exp = un.getExpr(); label = un.getLabel(); }
		else if (tn instanceof ASTLUnion lun) { exp = lun.getExpr(); label = lun.getLabel(); }
		else return new ASTMatch(tn, cases);

		MatchCase c = cases.get(label);
		String id = c.getId();
		ASTNode body = c.getExp(), expn = exp.weaknorm(sigma, sub);

		Env<ASTNode> env = sub.beginScope();
		env.assoc(id, expn);
		return body.weaknorm(sigma, env);
    }

	public ASTNode solve(Env<ASTType> sigma) {
		ASTNode ntest = test.solve(sigma);
		if (ntest != null) return new ASTMatch(ntest, cases);
		for (String label : cases.keySet()) {
			MatchCase c = cases.get(label);
			ASTNode nexp = c.getExp().solve(sigma);
			if (nexp != null) { cases.put(label, new MatchCase(c.getId(), nexp)); return new ASTMatch(test, cases); }
		}
        return null;
    }

	public ASTNode subs(String subsId, ASTNode node) {
		for (String label : cases.keySet()) {
			MatchCase c = cases.get(label);
			ASTNode exps = c.getExp().subs(subsId, node);
			cases.put(label, new MatchCase(c.getId(), exps));
		}
		return new ASTMatch(test.subs(subsId, node), cases);
	}

	public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
		if (o instanceof ASTMatch omatch && test.defequals(omatch.getTest(), sigma, alpha)) {
			Map<String, MatchCase> other = omatch.getCases();
			if (cases.size() != other.size()) return false;
			for (String label : cases.keySet()) {
				MatchCase ownCase = cases.get(label);
				MatchCase otherCase = other.get(label);
				if (otherCase == null || !ownCase.getExp().defequals(otherCase.getExp(), sigma, alpha.extend(ownCase.getId(), otherCase.getId()))) return false;
			}
			return true;
		}
		return false;
    }

	@Override
	public String toString() {
		return String.format("match(%s, %s)", test, cases);
	}
}
