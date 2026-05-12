package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTCheck implements ASTNode {
    ASTNode left, right;

    public ASTCheck(ASTNode l, ASTNode r) {
        left = l;
        right = r;
    }
    
    public IValue eval(Env<IValue> e) throws InterpreterError {
        return new VBool(true, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType t = left.typecheck(e);
        ASTType t2 = right.typecheck(e);
        if (!t.isSubtypeOf(t2, e) || !t2.isSubtypeOf(t, e))
            throw new TypeCheckError(String.format("terms %s and %s do not have the same type", left, right));

        Env<ASTType> sigma = e.getEnv(ENV.SIGMA);
        ASTNode ln = left, rn = right;
        while (true) {
            Env<ASTType> lnClosSigma = sigma;
            if (ln instanceof ASTApp a) {
                if (a.getFunc() instanceof ASTFunc f) lnClosSigma = f.getNormSigma();
                else if (a.getFunc() instanceof ASTLFunc lf) lnClosSigma = lf.getNormSigma();
            }
            Env<ASTType> rnClosSigma = sigma;
            if (rn instanceof ASTApp a) {
                if (a.getFunc() instanceof ASTFunc f) rnClosSigma = f.getNormSigma();
                else if (a.getFunc() instanceof ASTLFunc lf) rnClosSigma = lf.getNormSigma();
            }

            ln = ln.normalize(sigma, new Env<ASTNode>());
            rn = rn.normalize(sigma, new Env<ASTNode>());
            if (ln.defequals(rn, sigma, new AlphaEnv())) return new ASTTEq(left, right, t);

            ASTNode newln = ln.solve(lnClosSigma);
            if (newln != null) { ln = newln; continue; }
            ASTNode newrn = rn.solve(rnClosSigma);
            if (newrn != null) { rn = newrn; continue; }
            break;
        }
        throw new TypeCheckError(String.format("terms %s and %s are not definitionally equal", left, right));
    }

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        return this;
    }

    public ASTNode solve(Env<ASTType> sigma) {
        return null;
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s ? %s", left, right);
    }
}
