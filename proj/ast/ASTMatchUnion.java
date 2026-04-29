package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

import java.util.*;

public class ASTMatchUnion implements ASTNode {
	ASTNode test;
	Map<String, MatchCase> cases;

	public ASTMatchUnion(ASTNode t, Map<String, MatchCase> cs) {
		test = t;
		cases = cs;
    }

	public ASTNode getTest() {
		return test;
	}

	public Map<String, MatchCase> getCases() {
		return cases;
	}

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue vt = test.eval(e);
		if (vt instanceof VUnion vtu) {
			String testlabel = vtu.getLabel();
			MatchCase c = cases.get(testlabel);
			if (c == null) {
				throw new InterpreterError("match: missing case for label " + testlabel);
			} else {
				Environment<IValue> en = e.beginScope();
				en.assoc(c.getId(), ((VUnion) vt).getValue());
				return c.getExp().eval(en);
			}
		} else {
			throw new InterpreterError("match: union expected, found " + vt.toStr());
		}
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		ASTType tt = test.typecheck(e), rettype = null, tcase;
		HashSet<String> matchUsedLinears = null;
		tt = e.unfold(tt);
		if (tt instanceof ASTTUnion || tt instanceof ASTTLUnion) {
			EnvSet en = new EnvSet(e), env;
			Set<Map.Entry<String, ASTType>> entries = tt instanceof ASTTUnion ?
				((ASTTUnion) tt).getMap().entrySet() :
				((ASTTLUnion) tt).getMap().entrySet();
			for (Map.Entry<String, ASTType> entry : entries) {
				MatchCase c = cases.get(entry.getKey());
				if (c == null)
					throw new TypeCheckError("match missing label " + entry.getKey());

				env = (matchUsedLinears == null ? e : new EnvSet(en));
				ASTType tlabel = e.unfold(entry.getValue());

				ENV envChoice = (tlabel instanceof ASTLinType) ? ENV.DELTA : ENV.GAMMA;
        		env.openEnvScope(envChoice);
        		env.bindToEnv(envChoice, c.getId(), tlabel);
				tcase = c.getExp().typecheck(env);
				env.closeEnvScope(envChoice);

				if (matchUsedLinears == null) {
					matchUsedLinears = new HashSet<String>(e.getUsedLinears());
					matchUsedLinears.remove(c.getId());
				}

				HashSet<String> caseUsedLineares = new HashSet<String>(env.getUsedLinears());
				if ((entry.getValue() instanceof ASTLinType) && !caseUsedLineares.contains(c.getId()))
					throw new TypeCheckError("linear value " + c.getId() + " must be used");
				caseUsedLineares.remove(c.getId());
				if (!caseUsedLineares.equals(matchUsedLinears))
					throw new TypeCheckError("all match cases must use the same linear values");
				if ((tcase.isSubtypeOf(rettype, env) && rettype.isSubtypeOf(tcase, env)) || rettype == null) {
					rettype = tcase;
				} else {
					throw new TypeCheckError("different types for match cases: " + tcase.toStr() + " and " + rettype.toStr());
				}
			}
		} else {
			throw new TypeCheckError("illegal type to match test: " + tt.toStr());
		}
		return rettype;
	}

	public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

	public ASTNode normalize(Environment<ASTType> sigma) {
		ASTNode exp, tn = test.normalize(sigma);
		String label, id;
		if (tn instanceof ASTUnion un) { exp = un.getExpr(); label = un.getLabel(); }
		else if (tn instanceof ASTLUnion lun) { exp = lun.getExpr(); label = lun.getLabel(); }
		else return this;

		MatchCase c = cases.get(label);
		id = c.getId();
		ASTNode body = c.getExp();

		Environment<ASTType> env = sigma.beginScope();
		env.addEq(new ASTTEq(new ASTId(id), exp.normalize(sigma), sigma.find(id, false)));
		return body.normalize(env);
    }

	public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
		if (o instanceof ASTMatchUnion omatch && omatch.getTest().defequals(test, sigma)) {
			Map<String, MatchCase> other = omatch.getCases();
			if (cases.size() != other.size()) return false;
			for (String label : cases.keySet()) {
				MatchCase ownCase = cases.get(label);
				MatchCase otherCase = other.get(label);
				if (otherCase == null || !ownCase.getId().equals(otherCase.getId())
					|| !ownCase.getExp().defequals(otherCase.getExp(), sigma)) return false;
			}
			return true;
		}
		return false;
    }

	@Override
	public String toString() {
		return String.format("match(%s, %s)", test.toString(), cases.toString());
	}
}
