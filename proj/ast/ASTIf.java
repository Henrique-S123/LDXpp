package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.HashSet;

public class ASTIf implements ASTNode {
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

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue vt = test.eval(e);
		if (!(vt instanceof VBool vtb)) {
			throw new InterpreterError("if: bool condition expected, found " + vt);
		} else {
			boolean val = vtb.getval();
			return val ? conseq.eval(e) : alt.eval(e);
		}
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		ASTType tt = test.typecheck(e);
		if (tt instanceof ASTTBool || tt instanceof ASTTLBool) {
			EnvSet e2 = new EnvSet(e);
			ASTType vconseq = conseq.typecheck(e);
			ASTType valt = alt.typecheck(e2);
			if (!new HashSet<String>(e.getUsedLinears()).equals(new HashSet<String>(e2.getUsedLinears())))
				throw new TypeCheckError("if conseq and alt branches must use the same linear values");
			if (vconseq.isSubtypeOf(valt, e)) {
				return vconseq;
			} else if (valt.isSubtypeOf(vconseq, e)) {
				return valt;
			} else {
				throw new TypeCheckError("if conseq and alt branches do not have compatible types: " + vconseq + " and " + valt);
			}
		} else {
			throw new TypeCheckError("illegal type to if test: " + tt);
		}
	}

	public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

	public ASTNode normalize(Environment<ASTType> sigma, Environment<ASTNode> sub) {
		ASTNode tn = test.normalize(sigma, sub);
		boolean val;
		if (tn instanceof ASTBool b) val = b.getVal();
		else if (tn instanceof ASTLBool lb) val = lb.getVal();
		else return new ASTIf(tn, conseq.normalize(sigma, sub), alt.normalize(sigma, sub));

		if (val) return conseq.normalize(sigma, sub);
		else return alt.normalize(sigma, sub);
    }

	public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
		return o instanceof ASTIf oif && oif.getTest().defequals(test, sigma)
			&& oif.getConseq().defequals(conseq, sigma) && oif.getAlt().defequals(alt, sigma);
	}

	@Override
	public String toString() {
		return String.format("if(%s, %s, %s)", test, conseq, alt);
	}
}
