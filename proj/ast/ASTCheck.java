package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTCheck extends ASTNode {
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
            Env<ASTType> lnSigma = sigma;
            if (ln instanceof ASTApp a) {
                if (a.getFunc() instanceof ASTFunc f) lnSigma = f.getNormSigma();
                else if (a.getFunc() instanceof ASTLFunc lf) lnSigma = lf.getNormSigma();
            }
            Env<ASTType> rnSigma = sigma;
            if (rn instanceof ASTApp a) {
                if (a.getFunc() instanceof ASTFunc f) rnSigma = f.getNormSigma();
                else if (a.getFunc() instanceof ASTLFunc lf) rnSigma = lf.getNormSigma();
            }

            ln = ln.weaknorm(sigma, new Env<ASTNode>());
            rn = rn.weaknorm(sigma, new Env<ASTNode>());
            if (ln.defequals(rn, sigma, new AlphaEnv())) return new ASTTEq(left, right, t);

            ASTNode newln = ln.solve(lnSigma);
            if (newln != null) { ln = newln; continue; }
            ASTNode newrn = rn.solve(rnSigma);
            if (newrn != null) { rn = newrn; continue; }
            break;
        }
        throw new TypeCheckError(String.format("terms %s and %s are not definitionally equal", left, right));
    }

    @Override
    public String toString() {
        return String.format("%s ? %s", left, right);
    }
}
