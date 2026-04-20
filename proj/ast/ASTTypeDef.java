package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.HashMap;

public class ASTTypeDef implements ASTNode {
    HashMap<String,ASTType> ltd;
    ASTNode body;

    public ASTTypeDef(HashMap<String,ASTType>  ltdp, ASTNode b) {
        ltd = ltdp;
        body = b;
    }
    
    public IValue eval(Environment<IValue> env) throws InterpreterError {
        return body.eval(env);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        e.newPhiScope();
        for (String s : ltd.keySet()) {
            e.assocPhi(s, ltd.get(s));
        }
        ASTType ret = this.body.typecheck(e);
        e.closePhiScope();
        return ret;
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    public boolean defequals(ASTNode o) {
        // TODO
        return false;
    }
}
