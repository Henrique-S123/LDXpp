package proj.env;

import proj.errors.*;
import proj.types.*;

import java.util.*;

public class EnvSet {
    Env<ASTType> gamma, phi, sigma;
    Env<LinearBinding> delta;

    public EnvSet() {
        gamma = new Env<ASTType>();
        delta = new Env<LinearBinding>();
        phi = new Env<ASTType>();
        sigma = new Env<ASTType>();
    }

    public EnvSet(EnvSet o) {
        gamma = o.gamma.copy();
        delta = o.delta.copy(lb -> new LinearBinding(lb.getType(), lb.isAvailable()));
        phi = o.phi.copy();
        sigma = o.sigma.copy();
    }

    public static enum ENV { GAMMA, DELTA, PHI, SIGMA }

    /* Getters and Setters */
    public Env<ASTType> getSigma() {
        return sigma;
    }

    public Env<ASTType> getPhi() {
        return phi;
    }

    public Env<LinearBinding> popDelta() {
        Env<LinearBinding> tmp = this.delta;
        this.delta = new Env<LinearBinding>();
        return tmp;
    }

    public void pushDelta(Env<LinearBinding> d) {
        delta = d;
    }

    public Set<String> getUsedLinears() {
        return delta.filterIds(null, id -> !id.isAvailable());
    }

    public Set<String> getUnusedLinears() {
        return delta.filterIds(null, id -> id.isAvailable());
    }

    public Set<String> getUnusedScopeLinears() {
        return delta.filterIds(delta.anc, id -> id.isAvailable());
    }

    /* Open scopes */
    public void openEnvScope(ENV env) {
        switch (env) {
            case GAMMA -> this.gamma = this.gamma.beginScope();
            case DELTA -> this.delta = this.delta.beginScope();
            case PHI -> this.phi = this.phi.beginScope();
            case SIGMA -> this.sigma = this.sigma.beginScope();
        }
    }

    /* Close scopes */
    public void closeEnvScope(ENV env) {
        switch (env) {
            case GAMMA -> this.gamma = this.gamma.endScope();
            case DELTA -> this.delta = this.delta.endScope();
            case PHI -> this.phi = this.phi.endScope();
            case SIGMA -> this.sigma = this.sigma.endScope();
        }
    }

    /* Populate environments */
    private void checkAlreadyDeclared(ENV env, String id) throws TypeCheckError {
        if (
            switch (env) {
                case GAMMA -> gamma.find(id) != null;
                case DELTA -> delta.find(id) != null;
                case PHI -> phi.find(id) != null;
                case SIGMA -> sigma.find(id) != null;
            }
        ) throw new TypeCheckError(ErrorMessages.alreadyDeclaredVariable(id));
    }

    public void bindToEnv(ENV env, String id, ASTType t) throws TypeCheckError {
        switch (env) {
            case GAMMA -> { checkAlreadyDeclared(ENV.DELTA, id); gamma.assoc(id, t); }
            case DELTA -> { checkAlreadyDeclared(ENV.GAMMA, id); delta.assoc(id, new LinearBinding(t, true)); }
            case PHI -> { checkAlreadyDeclared(ENV.PHI, id); phi.assoc(id, t); }
            case SIGMA -> sigma.assoc(id, t);
        }
    }

    public void addEq(ASTTEq t) {
        String e = UUID.randomUUID().toString();
        this.sigma.assoc(e, t);
    }

    /* Find binds */
    public ASTType findVar(String id) throws TypeCheckError {
        ASTType ret = gamma.find(id);
        if (ret != null) return ret;
        LinearBinding b = delta.find(id);
        if (b != null) {
            if (b.isAvailable()) { b.use(); return b.getType(); }
            else throw new TypeCheckError(ErrorMessages.alreadyUsedLinear(id));
        }
        throw new TypeCheckError(ErrorMessages.idNotFound(id));
    }

    /* Unfold operation */
    public ASTType unfold(ASTType t) {
        return (t instanceof ASTTId tid) ? unfold(phi.find(tid.getId())) : t;
    }

    public String toString() {
        return "Γ: " + gamma + "; Δ: " + delta + "; Φ: " + phi + "; Σ: " + sigma;
    }
}
