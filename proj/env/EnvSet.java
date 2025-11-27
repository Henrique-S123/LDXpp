package proj.env;

import proj.errors.InterpreterError;
import proj.types.*;

import java.util.ArrayList;

public class EnvSet {
    Environment<ASTType> gamma, delta, phi;
    ArrayList<String> declaredIds, usedLinears;

    public EnvSet() {
        gamma = new Environment<ASTType>();
        delta = new Environment<ASTType>();
        phi = new Environment<ASTType>();
        declaredIds = new ArrayList<String>();
        usedLinears = new ArrayList<String>();
    }

    public EnvSet(EnvSet o) {
        gamma = o.gamma.copy(true);
        delta = o.delta.copy(true);
        phi = o.phi.copy(true);
        declaredIds = new ArrayList<String>(o.getDeclaredIds());
        usedLinears = new ArrayList<String>(o.getUsedLinears());
    }

    public Environment<ASTType> getGamma() {
        return gamma;
    }

    public Environment<ASTType> getDelta() {
        return delta;
    }

    public Environment<ASTType> getPhi() {
        return phi;
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

    public void newGammaScope() {
        this.gamma = this.gamma.beginScope();
    }

    public void newDeltaScope() {
        this.delta = this.delta.beginScope();
    }

    public void newPhiScope() {
        this.phi = this.phi.beginScope();
    }

    public void closeGammaScope() {
        this.gamma = this.gamma.endScope();
    }

    public void closeDeltaScope() {
        this.delta = this.delta.endScope();
    }

    public void closePhiScope() {
        this.phi = this.phi.endScope();
    }

    public void assocGamma(String id, ASTType t) throws InterpreterError {
        if (declaredIds.contains(id))
            throw new InterpreterError("Identifier " + id + " already declared!");
        this.gamma.assoc(id, t);
    }

    public void assocDelta(String id, ASTType t) throws InterpreterError {
        if (declaredIds.contains(id))
            throw new InterpreterError("Identifier " + id + " already declared!");
        if (usedLinears.contains(id)) usedLinears.remove(id);
        this.delta.assoc(id, t);
    }

    public void assocPhi(String id, ASTType t) throws InterpreterError {
        if (declaredIds.contains(id))
            throw new InterpreterError("Identifier " + id + " already declared!");
        this.phi.assoc(id, t);
    }
    
    public void assocVar(String id, ASTType t) throws InterpreterError {
        if (declaredIds.contains(id))
            throw new InterpreterError("Identifier " + id + " already declared!");
        if (t instanceof ASTLinType) {
            this.newDeltaScope();
            this.assocDelta(id, t);
        } else {
            this.newGammaScope();
            this.assocGamma(id, t);
        }
    }

    public ASTType findVar(String id) throws InterpreterError {
        ASTType ret = gamma.find(id, false);
        if (ret != null) return ret;
        ret = delta.find(id, true);
        if (ret != null) {
            usedLinears.add(id);
            return ret;
        } else {
            if (usedLinears.contains(id))
                throw new InterpreterError("Linear value of '" + id + "' has already been consumed and cannot be used again.");
            else throw new InterpreterError("Undeclared identifier " + id + ".");
        }
    }

    public String toStr() {
        return "Gamma: " + gamma.toStr() + "; Delta: " + delta.toStr() + "; Phi: " + phi.toStr();
    }
}
