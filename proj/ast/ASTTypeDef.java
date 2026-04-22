package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

import java.util.HashMap;

public class ASTTypeDef implements ASTNode {
    HashMap<String, ASTType> ltd;
    ASTNode body;

    public ASTTypeDef(HashMap<String, ASTType> ltdp, ASTNode b) {
        ltd = ltdp;
        body = b;
    }

    public HashMap<String, ASTType> getLtd() {
        return ltd;
    }

    public ASTNode getBody() {
        return body;
    }
    
    public IValue eval(Environment<IValue> env) throws InterpreterError {
        return body.eval(env);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        e.openEnvScope(ENV.PHI);
        for (String s : ltd.keySet())
            e.bindToEnv(ENV.PHI, s, ltd.get(s));
        ASTType ret = this.body.typecheck(e);
        e.closeEnvScope(ENV.PHI);
        return ret;
    }

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return new ASTTypeDef(ltd, body.normalize(sigma));
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTTypeDef && ((ASTTypeDef) o).getBody().defequals(body, sigma)
            && ((ASTTypeDef) o).getLtd().equals(ltd);
    }
}
