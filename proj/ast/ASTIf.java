package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.HashSet;

public class ASTIf extends ASTNode {
    private final ASTNode test, conseq, alt;

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

	public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		ASTType tt = test.typecheck(e, null);
		if (!(tt instanceof ASTTBool || tt instanceof ASTTLBool))
			throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("if", tt));

		EnvSet e2 = new EnvSet(e);
		ASTType tconseq = conseq.typecheck(e, target);
		ASTType talt = alt.typecheck(e2, target);
		HashSet<String> conseqLs = new HashSet<String>(e.getUsedLinears());
		HashSet<String> altLs = new HashSet<String>(e2.getUsedLinears());

		if (!conseqLs.equals(altLs))
			throw new TypeCheckError(ErrorMessages.branchesDifferentLinears(conseqLs, altLs));

		if (target != null) {
			if (!tconseq.isSubtypeOf(target, e.getSigma(), e.getPhi(), e.getAlpha()))
				throw new TypeCheckError(ErrorMessages.branchesDifferentTypes(tconseq, target));
			if (!talt.isSubtypeOf(target, e.getSigma(), e.getPhi(), e.getAlpha()))
				throw new TypeCheckError(ErrorMessages.branchesDifferentTypes(talt, target));
			return target;
		} else {
			if (tconseq.isSubtypeOf(talt, e.getSigma(), e.getPhi(), e.getAlpha())) return talt;
			else if (talt.isSubtypeOf(tconseq, e.getSigma(), e.getPhi(), e.getAlpha())) return tconseq;
			else throw new TypeCheckError(ErrorMessages.branchesDifferentTypes(tconseq, talt));
		}
	}

	public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
		ASTType tt = test.puretypecheck(sigma, phi, alpha, null);
		if (!(tt instanceof ASTTBool || tt instanceof ASTTLBool))
			throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("if", tt));

		ASTType tconseq = conseq.puretypecheck(sigma, phi, alpha, target);
		ASTType talt = alt.puretypecheck(sigma, phi, alpha, target);

		if (target != null) {
			if (!tconseq.isSubtypeOf(target, sigma, phi, alpha))
				throw new TypeCheckError(ErrorMessages.branchesDifferentTypes(tconseq, target));
			if (!talt.isSubtypeOf(target, sigma, phi, alpha))
				throw new TypeCheckError(ErrorMessages.branchesDifferentTypes(talt, target));
			return target;
		} else {
			if (tconseq.isSubtypeOf(talt, sigma, phi, alpha)) return talt;
			else if (talt.isSubtypeOf(tconseq, sigma, phi, alpha)) return tconseq;
			else throw new TypeCheckError(ErrorMessages.branchesDifferentTypes(tconseq, talt));
		}
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		ASTNode tn = test.weaknorm(sub);
		boolean val;
		if (tn instanceof ASTBool b) val = b.getVal();
		else return new ASTIf(tn, conseq.weaknorm(sub), alt.weaknorm(sub));

		if (val) return conseq.weaknorm(sub);
		else return alt.weaknorm(sub);
    }

	public ASTIf solve(Env<ASTType> sigma) {
		ASTNode ntest = test.solve(sigma);
		return ntest == null ? null : new ASTIf(ntest, conseq, alt);
    }

	public ASTIf subs(String subsId, ASTNode node) {
        return new ASTIf(test.subs(subsId, node), conseq.subs(subsId, node), alt.subs(subsId, node));
    }

	@Override
	public String toString() {
		return String.format("if(%s, %s, %s)", test, conseq, alt);
	}
}
