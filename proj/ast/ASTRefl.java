package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTRefl extends ASTNode  {

    public ASTRefl() {}

    public IValue eval(Env<IValue> e) throws InterpreterError {
        return new VRefl();                
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        throw new TypeCheckError("refl: expected type to check against");
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        if (!(t instanceof ASTTEq tt))
            throw new TypeCheckError("refl: expected equality type");

        Env<ASTType> sigma = e.getEnv(ENV.SIGMA);
        ASTNode ln = tt.getTerm1(), rn = tt.getTerm2();
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

            ln = ln.normalize(sigma, new Env<ASTNode>());
            rn = rn.normalize(sigma, new Env<ASTNode>());
            if (ln.defequals(rn, sigma, new AlphaEnv())) return t;

            ASTNode newln = ln.solve(lnSigma);
            if (newln != null) { ln = newln; continue; }
            ASTNode newrn = rn.solve(rnSigma);
            if (newrn != null) { rn = newrn; continue; }
            break;
        }
        throw new TypeCheckError(String.format("refl: terms %s and %s are not definitionally equal", tt.getTerm1(), tt.getTerm2()));
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTRefl;
    }

    @Override
    public String toString() {
        return "refl";
    }
}
