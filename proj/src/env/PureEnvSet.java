package proj.src.env;

import proj.src.errors.*;
import proj.src.types.*;

import java.util.*;

public class PureEnvSet {
    Env<ASTType> phi, sigma;
    AlphaEnv alpha;

    public PureEnvSet() {
        phi = new Env<ASTType>();
        sigma = new Env<ASTType>();
        alpha = new AlphaEnv();
    }

    public PureEnvSet(PureEnvSet o) {
        phi = o.phi.copy();
        sigma = o.sigma.copy();
        alpha = o.alpha.copy();
    }

    public PureEnvSet(EnvSet o) {
        phi = o.phi.copy();
        sigma = o.sigma.copy();
        alpha = o.alpha.copy();
    }

    public static enum PENV { PHI, SIGMA }

    /* Getters and Setters */
    public Env<ASTType> getSigma() {
        return sigma;
    }

    public Env<ASTType> getPhi() {
        return phi;
    }

    public AlphaEnv getAlpha() {
        return alpha;
    }

    /* Open scopes */
    public void openEnvScope(PENV env) {
        switch (env) {
            case PHI -> this.phi = this.phi.beginScope();
            case SIGMA -> this.sigma = this.sigma.beginScope();
        }
    }

    /* Close scopes */
    public void closeEnvScope(PENV env) {
        switch (env) {
            case PHI -> this.phi = this.phi.endScope();
            case SIGMA -> this.sigma = this.sigma.endScope();
        }
    }

    /* Populate environments */
    private void checkAlreadyDeclared(PENV env, String id) throws TypeCheckError {
        if (
            switch (env) {
                case PHI -> phi.find(id) != null;
                case SIGMA -> sigma.find(id) != null;
            }
        ) throw new TypeCheckError(ErrorMessages.alreadyDeclaredVariable(id));
    }

    public String bindToEnv(PENV env, String id, ASTType t) throws TypeCheckError {
        if (id == null) throw new TypeCheckError(ErrorMessages.nullId());
        Binder<ASTType> b = new Binder<ASTType>(t);
        return bindToEnv(env, id, b);
    }

    public String bindToEnv(PENV env, String id, Binder<ASTType> b) throws TypeCheckError {
        if (id == null) throw new TypeCheckError(ErrorMessages.nullId());
        switch (env) {
            case PHI -> { checkAlreadyDeclared(PENV.PHI, id); phi.assoc(id, b); }
            case SIGMA -> sigma.assoc(id, b);
        }
        return b.getId();
    }

    public String getFreshId() {
        return UUID.randomUUID().toString();
    }

    public void extendAlpha(String id1, String id2) {
        alpha.extend(id1, id2);
    }

    /* Find binds */
    public ASTType findVar(String id) throws TypeCheckError {
        ASTType ret = sigma.find(id);
        if (ret != null) return ret;
        throw new TypeCheckError(ErrorMessages.idNotFound(id));
    }

    public String findBinderId(String id) {
        String ret = sigma.findBinderId(id);
        return ret;
    }

    public ASTType findAlias(String id) {
        return phi.find(id);
    }

    /* Unfold operation */
    public ASTType unfold(ASTType t) {
        return (t instanceof ASTTId tid) ? unfold(phi.find(tid.getId())) : t;
    }

    public String toString() {
        return "Φ: " + phi + "; Σ: " + sigma;
    }
}
