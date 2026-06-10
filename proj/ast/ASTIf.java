package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.defeq.TermClosure;
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

	public ASTType typeinfer(EnvSet e) throws TypeCheckError, EnvironmentError {
        return typecheck(e, null);
    }

	public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError, EnvironmentError {
		ASTType tt = test.typeinfer(e);
		if (!(tt instanceof ASTTBool || tt instanceof ASTTLBool))
			throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("if", tt));

		EnvSet e2 = new EnvSet(e);
		ASTType tconseq = (target == null) ? conseq.typeinfer(e) : conseq.typecheck(e, target);
		ASTType talt = (target == null) ? alt.typeinfer(e2) : alt.typecheck(e2, target);
		HashSet<String> conseqLs = new HashSet<String>(e.getUsedLinears());
		HashSet<String> altLs = new HashSet<String>(e2.getUsedLinears());

		if (!conseqLs.equals(altLs))
			throw new TypeCheckError(ErrorMessages.branchesDifferentLinears(conseqLs, altLs));
		if (tconseq.isSubtypeOf(talt, e.getSigma(), e.getPhi(), new AlphaEnv())) return tconseq;
		else if (talt.isSubtypeOf(tconseq, e.getSigma(), e.getPhi(), new AlphaEnv())) return talt;
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

	public TermClosure solve(Env<ASTType> sigma) {
		TermClosure ntest = test.solve(sigma);
		return ntest == null ? null : new TermClosure(new ASTIf(ntest.term(), conseq, alt), sigma);
    }

	public ASTNode subs(String subsId, ASTNode node) {
        return new ASTIf(test.subs(subsId, node), conseq.subs(subsId, node), alt.subs(subsId, node));
    }

	@Override
	public String toString() {
		return String.format("if(%s, %s, %s)", test, conseq, alt);
	}
}
