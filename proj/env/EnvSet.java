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

    public void clearDelta() {
        this.delta = new Environment<ASTType>();
    }

    public void assocGamma(String id, ASTType t) throws InterpreterError {
        Environment<ASTType> en = this.gamma.beginScope();
        if (declaredIds.contains(id))
            throw new InterpreterError("Identifier " + id + " already declared!");
        en.assoc(id, t);
        this.gamma = en;
    }

    public void assocDelta(String id, ASTType t) throws InterpreterError {
        Environment<ASTType> en = this.delta.beginScope();
        if (declaredIds.contains(id))
            throw new InterpreterError("Identifier " + id + " already declared!");
        if (usedLinears.contains(id)) usedLinears.remove(id);
        en.assoc(id, t);
        this.delta = en;
    }

    public void assocPhi(String id, ASTType t) throws InterpreterError {
        Environment<ASTType> en = this.phi.beginScope();
        if (declaredIds.contains(id))
            throw new InterpreterError("Identifier " + id + " already declared!");
        en.assoc(id, t);
        this.phi = en;
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
