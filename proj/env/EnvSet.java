package proj.env;

import proj.errors.EnvironmentError;
import proj.errors.ErrorMessages;
import proj.types.*;

import java.util.*;

public class EnvSet {
    Env<ASTType> gamma, delta, phi, sigma;
    ArrayList<String> usedLinears;

    public EnvSet() {
        gamma = new Env<ASTType>();
        delta = new Env<ASTType>();
        phi = new Env<ASTType>();
        sigma = new Env<ASTType>();
        usedLinears = new ArrayList<String>();
    }

    public EnvSet(EnvSet o) {
        gamma = o.gamma.copy(true);
        delta = o.delta.copy(true);
        phi = o.phi.copy(true);
        sigma = o.sigma.copy(true);
        usedLinears = new ArrayList<String>(o.getUsedLinears());
    }

    public static enum ENV { GAMMA, DELTA, PHI, SIGMA }

    /* Getters and Setters */
    public Env<ASTType> getEnv(ENV env) {
        return switch (env) {
            case GAMMA -> gamma;
            case DELTA -> delta;
            case PHI -> phi;
            case SIGMA -> sigma;
        };
    }

    public Env<ASTType> popDelta() {
        Env<ASTType> tmp = this.delta;
        this.delta = new Env<ASTType>();
        return tmp;
    }

    public ArrayList<String> getUsedLinears() {
        return usedLinears;
    }

    public void setEnv(ENV env, Env<ASTType> e) {
        switch (env) {
            case GAMMA -> this.gamma = e;
            case DELTA -> this.delta = e;
            case PHI -> this.phi = e;
            case SIGMA -> this.sigma = e;
        }
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
    private void checkAlreadyDeclared(String id, Env<ASTType> env) throws EnvironmentError {
        if (env.getScopeIds().contains(id))
            throw new EnvironmentError(ErrorMessages.alreadyDeclaredVariable(id));
    }

    public void bindToEnv(ENV env, String id, ASTType t) throws EnvironmentError {
        switch (env) {
            case GAMMA -> { checkAlreadyDeclared(id, gamma); gamma.assoc(id, t); }
            case DELTA -> { checkAlreadyDeclared(id, delta); if (usedLinears.contains(id)) usedLinears.remove(id); delta.assoc(id, t); }
            case PHI -> { checkAlreadyDeclared(id, phi); phi.assoc(id, t); }
            case SIGMA -> sigma.assoc(id, t);
        }
    }

    public void addEq(ASTTEq t) {
        String e = UUID.randomUUID().toString();
        this.sigma.assoc(e, t);
    }

    /* Find binds */
    public ASTType findVar(String id) throws EnvironmentError {
        ASTType ret = gamma.find(id);
        if (ret != null) return ret;
        ret = delta.remove(id);
        if (ret != null) {
            usedLinears.add(id);
            return ret;
        } else {
            if (usedLinears.contains(id))
                throw new EnvironmentError(ErrorMessages.alreadyUsedLinear(id));
            else throw new EnvironmentError(ErrorMessages.idNotFound(id));
        }
    }

    /* Unfold operation */
    public ASTType unfold(ASTType t) {
        if (t instanceof ASTTId tid) {
            return unfold((ASTType) phi.find(tid.getId()));
        } else {
            return t;
        }
    }

    public String toString() {
        return "Γ: " + gamma + "; Δ: " + delta + "; Φ: " + phi + "; Σ: " + sigma;
    }
}
