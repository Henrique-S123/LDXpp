package proj.env;

import proj.errors.*;
import proj.types.*;

import java.util.*;

public class EnvSet {
    Env<ASTType> gamma, phi, sigma;
    ResourceManager<ASTType> delta;
    AlphaEnv alpha;

    public EnvSet() {
        gamma = new Env<ASTType>();
        delta = new ResourceManager<ASTType>();
        phi = new Env<ASTType>();
        sigma = new Env<ASTType>();
        alpha = new AlphaEnv();
    }

    public EnvSet(EnvSet o) {
        gamma = o.gamma.copy();
        delta = o.delta.copy();
        phi = o.phi.copy();
        sigma = o.sigma.copy();
        alpha = o.alpha.copy();
    }

    public static enum ENV { GAMMA, DELTA, PHI, SIGMA }

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

    public ResourceManager<ASTType> popDelta() {
        ResourceManager<ASTType> tmp = this.delta;
        this.delta = new ResourceManager<ASTType>();
        return tmp;
    }

    public void pushDelta(ResourceManager<ASTType> d) {
        delta = d;
    }

    public Set<String> getUsedLinears() {
        return delta.getUsedLinears();
    }

    public Set<String> getUnusedLinears() {
        return delta.getUnusedScopeLinears();
    }

    public Set<String> getUnusedScopeLinears() {
        return delta.getUnusedScopeLinears();
    }

    /* Open scopes */
    public void openEnvScope(ENV env) {
        switch (env) {
            case GAMMA -> this.gamma = this.gamma.beginScope();
            case DELTA -> this.delta.openScope();
            case PHI -> this.phi = this.phi.beginScope();
            case SIGMA -> this.sigma = this.sigma.beginScope();
        }
    }

    /* Close scopes */
    public void closeEnvScope(ENV env) {
        switch (env) {
            case GAMMA -> this.gamma = this.gamma.endScope();
            case DELTA -> this.delta.closeScope();
            case PHI -> this.phi = this.phi.endScope();
            case SIGMA -> this.sigma = this.sigma.endScope();
        }
    }

    /* Populate environments */
    private void checkAlreadyDeclared(ENV env, String id) throws TypeCheckError {
        if (
            switch (env) {
                case GAMMA -> gamma.find(id) != null;
                case DELTA -> delta.contains(id);
                case PHI -> phi.find(id) != null;
                case SIGMA -> sigma.find(id) != null;
            }
        ) throw new TypeCheckError(ErrorMessages.alreadyDeclaredVariable(id));
    }

    public void bindToEnv(ENV env, String id, ASTType t) throws TypeCheckError {
        switch (env) {
            case GAMMA -> { checkAlreadyDeclared(ENV.DELTA, id); gamma.assoc(id, t); }
            case DELTA -> { checkAlreadyDeclared(ENV.GAMMA, id); delta.register(id, t); }
            case PHI -> { checkAlreadyDeclared(ENV.PHI, id); phi.assoc(id, t); }
            case SIGMA -> sigma.assoc(id, t);
        }
    }

    public void bindToEnv(ENV env, String id, Binder<ASTType> b) throws TypeCheckError {
        switch (env) {
            case GAMMA -> { checkAlreadyDeclared(ENV.DELTA, id); gamma.assoc(id, b); }
            case DELTA -> { checkAlreadyDeclared(ENV.GAMMA, id); delta.register(id, b); }
            case PHI -> { checkAlreadyDeclared(ENV.PHI, id); phi.assoc(id, b); }
            case SIGMA -> sigma.assoc(id, b);
        }
    }

    public void addEq(ASTTEq t) {
        this.sigma.addEq(t);
    }

    public void extendAlpha(String id1, String id2) {
        alpha.extend(id1, id2);
    }

    /* Find binds */
    public ASTType findVar(String id) throws TypeCheckError {
        ASTType ret = gamma.find(id);
        if (ret != null) return ret;
        ret = delta.consume(id);
        if (ret != null) return ret;
        throw new TypeCheckError(ErrorMessages.idNotFound(id));
    }

    public String findBinderId(String id) {
        String ret = gamma.findBinderId(id);
        if (ret != null) return ret;
        ret = delta.findBinderId(id);
        return ret;
    }

    /* Unfold operation */
    public ASTType unfold(ASTType t) {
        return (t instanceof ASTTId tid) ? unfold(phi.find(tid.getId())) : t;
    }

    public String toString() {
        return "Γ: " + gamma + "; Δ: " + delta + "; Φ: " + phi + "; Σ: " + sigma;
    }
}
