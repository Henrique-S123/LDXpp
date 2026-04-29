package proj.env;

import proj.errors.EnvironmentError;
import proj.types.*;

import java.util.*;

public class EnvSet {
    Environment<ASTType> gamma, delta, phi, sigma;
    ArrayList<String> declaredIds, usedLinears;

    public EnvSet() {
        gamma = new Environment<ASTType>();
        delta = new Environment<ASTType>();
        phi = new Environment<ASTType>();
        sigma = new Environment<ASTType>();
        declaredIds = new ArrayList<String>();
        usedLinears = new ArrayList<String>();
    }

    public EnvSet(EnvSet o) {
        gamma = o.gamma.copy(true);
        delta = o.delta.copy(true);
        phi = o.phi.copy(true);
        sigma = o.sigma.copy(true);
        declaredIds = new ArrayList<String>(o.getDeclaredIds());
        usedLinears = new ArrayList<String>(o.getUsedLinears());
    }

    public static enum ENV { GAMMA, DELTA, PHI, SIGMA }

    /* Getters and Setters */
    public Environment<ASTType> getEnv(ENV env) {
        return switch (env) {
            case GAMMA -> gamma;
            case DELTA -> delta;
            case PHI -> phi;
            case SIGMA -> sigma;
        };
    }

    public Environment<ASTType> popDelta() {
        Environment<ASTType> tmp = this.delta;
        this.delta = new Environment<ASTType>();
        return tmp;
    }

    public ArrayList<String> getDeclaredIds() {
        return declaredIds;
    }

    public ArrayList<String> getUsedLinears() {
        return usedLinears;
    }

    public void setEnv(ENV env, Environment<ASTType> e) {
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
    private void checkAlreadyDeclared(String id) throws EnvironmentError {
        if (declaredIds.contains(id))
            throw new EnvironmentError("Identifier " + id + " already declared!");
    }

    public void bindToEnv(ENV env, String id, ASTType t) throws EnvironmentError {
        checkAlreadyDeclared(id);
        switch (env) {
            case GAMMA -> gamma.assoc(id, t);
            case DELTA -> { if (usedLinears.contains(id)) usedLinears.remove(id); delta.assoc(id, t); }
            case PHI -> phi.assoc(id, t);
            case SIGMA -> sigma.assoc(id, t);
        }
    }

    public void addEq(ASTTEq t) {
        this.sigma.addEq(t);
    }

    /* Find binds */
    public ASTType findVar(String id) throws EnvironmentError {
        ASTType ret = gamma.find(id, false);
        if (ret != null) return ret;
        ret = delta.find(id, true);
        if (ret != null) {
            usedLinears.add(id);
            return ret;
        } else {
            if (usedLinears.contains(id))
                throw new EnvironmentError("Linear value of '" + id + "' has already been consumed and cannot be used again.");
            else throw new EnvironmentError("Undeclared identifier " + id + ".");
        }
    }

    /* Unfold operation */
    public ASTType unfold(ASTType t) {
        if (t instanceof ASTTId tid) {
            return unfold((ASTType) phi.find(tid.getId(), false));
        } else {
            return t;
        }
    }

    public String toStr() {
        return "Γ: " + gamma.toStr() + "; Δ: " + delta.toStr() + "; Φ: " + phi.toStr() + "; Σ: " + sigma.toStr();
    }
}
