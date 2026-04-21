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

    public Environment<ASTType> getGamma() {
        return gamma;
    }

    public Environment<ASTType> getDelta() {
        return delta;
    }

    public Environment<ASTType> getPhi() {
        return phi;
    }

    public Environment<ASTType> getSigma() {
        return sigma;
    }

    public ArrayList<String> getDeclaredIds() {
        return declaredIds;
    }

    public ArrayList<String> getUsedLinears() {
        return usedLinears;
    }

    public Environment<ASTType> popDelta() {
        Environment<ASTType> tmp = this.delta;
        this.delta = new Environment<ASTType>();
        return tmp;
    }

    public void setDelta(Environment<ASTType> d) {
        this.delta = d;
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

    public void closeGammaScope() {
        this.gamma = this.gamma.endScope();
    }

    public void closeDeltaScope() {
        this.delta = this.delta.endScope();
    }

    public void closePhiScope() {
        this.phi = this.phi.endScope();
    }

    public void closeSigmaScope() {
        this.sigma = this.sigma.endScope();
    }

    public void closeEnvScope(ENV env) {
        switch (env) {
            case GAMMA -> this.gamma = this.gamma.endScope();
            case DELTA -> this.delta = this.delta.endScope();
            case PHI -> this.phi = this.phi.endScope();
            case SIGMA -> this.sigma = this.sigma.endScope();
        }
    }

    /* Bind new ids */

    private void checkAlreadyDeclared(String id) throws EnvironmentError {
        if (declaredIds.contains(id))
            throw new EnvironmentError("Identifier " + id + " already declared!");
    }

    public void assocGamma(String id, ASTType t) throws EnvironmentError {
        checkAlreadyDeclared(id);
        this.gamma.assoc(id, t);
    }

    public void assocDelta(String id, ASTType t) throws EnvironmentError {
        checkAlreadyDeclared(id);
        if (usedLinears.contains(id)) usedLinears.remove(id);
        this.delta.assoc(id, t);
    }

    public void assocPhi(String id, ASTType t) throws EnvironmentError {
        checkAlreadyDeclared(id);
        this.phi.assoc(id, t);
    }
    
    public void assocVar(String id, ASTType t) throws EnvironmentError {
        checkAlreadyDeclared(id);
        if (t instanceof ASTLinType) {
            this.openEnvScope(ENV.DELTA);
            this.assocDelta(id, t);
        } else {
            this.openEnvScope(ENV.GAMMA);
            this.assocGamma(id, t);
        }
    }

    public void bindVar(String id, ASTType t) throws EnvironmentError {
        checkAlreadyDeclared(id);
        if (t instanceof ASTLinType) {
            this.assocDelta(id, t);
        } else {
            this.assocGamma(id, t);
        }
    }

    public void assocSigma(String id, ASTType t) throws EnvironmentError {
        checkAlreadyDeclared(id);
        this.sigma.assoc(id, t);
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

    public String toStr() {
        return "Gamma: " + gamma.toStr() + "; Delta: " + delta.toStr() + "; Phi: " + phi.toStr();
    }

    public ASTType unfold(ASTType t) {
        if (t instanceof ASTTId) {
            return unfold((ASTType) phi.find(((ASTTId) t).getId(), false));
        } else {
            return t;
        }
    }
}
