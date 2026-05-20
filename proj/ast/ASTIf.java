package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.HashSet;

public class ASTIf extends ASTNode {
    ASTNode test, conseq, alt;

	public ASTIf(ASTNode t, ASTNode c, ASTNode a) {
		test = t;
		conseq = c;
		alt = a;
    }

	public ASTNode getTest() {
		return test;
	}

	public ASTNode getConseq() {
		return conseq;
	}

	public ASTNode getAlt() {
		return alt;
	}

    public IValue eval(Env<IValue> e) throws InterpreterError {
		IValue vt = test.eval(e);
		if (!(vt instanceof VBool vtb)) {
			throw new InterpreterError(ErrorMessages.wrongValueToUnary("if", vt));
		} else {
			boolean val = vtb.getval();
			return val ? conseq.eval(e) : alt.eval(e);
		}
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		ASTType tt = test.typecheck(e);
		if (!(tt instanceof ASTTBool || tt instanceof ASTTLBool))
			throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("if", tt));
		EnvSet e2 = new EnvSet(e);
		ASTType tconseq = conseq.typecheck(e);
		ASTType talt = alt.typecheck(e2);
		HashSet<String> conseqLs = new HashSet<String>(e.getUsedLinears());
		HashSet<String> altLs = new HashSet<String>(e2.getUsedLinears());
		if (!conseqLs.equals(altLs))
			throw new TypeCheckError(ErrorMessages.branchesDifferentLinears(conseqLs, altLs));
		if (tconseq.isSubtypeOf(talt, e, new AlphaEnv())) return tconseq;
		else if (talt.isSubtypeOf(tconseq, e, new AlphaEnv())) return talt;
		else throw new TypeCheckError(ErrorMessages.branchesDifferentTypes(tconseq, talt));
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		ASTNode tn = test.weaknorm(sub);
		boolean val;
		if (tn instanceof ASTBool b) val = b.getVal();
		else if (tn instanceof ASTLBool lb) val = lb.getVal();
		else return new ASTIf(tn, conseq.weaknorm(sub), alt.weaknorm(sub));

		if (val) return conseq.weaknorm(sub);
		else return alt.weaknorm(sub);
    }

	public ASTNode solve(Env<ASTType> sigma) {
		ASTNode ntest = test.solve(sigma);
		if (ntest != null) return new ASTIf(ntest, conseq, alt);
		ASTNode nconseq = conseq.solve(sigma);
		if (nconseq != null) return new ASTIf(test, nconseq, alt);
		ASTNode nalt = alt.solve(sigma);
		if (nalt != null) return new ASTIf(test, conseq, nalt);
        return null;
    }

	public ASTNode subs(String subsId, ASTNode node) {
        return new ASTIf(test.subs(subsId, node), conseq.subs(subsId, node), alt.subs(subsId, node));
    }

	public boolean defequals(Env<ASTType> sl, ASTNode o, Env<ASTType> sr, AlphaEnv alpha) {
		return o instanceof ASTIf oif && test.defequals(sl, oif.getTest(), sr, alpha)
			&& conseq.defequals(sl, oif.getConseq(), sr, alpha) && alt.defequals(sl, oif.getAlt(), sr, alpha);
	}

	@Override
	public String toString() {
		return String.format("if(%s, %s, %s)", test, conseq, alt);
	}
}
