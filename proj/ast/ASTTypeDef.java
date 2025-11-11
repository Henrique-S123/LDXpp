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

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
        Environment<ASTType> en = e.beginScope();

        for (String s : ltd.keySet()) {
            en.assoc(s, ltd.get(s));
        }

        return this.body.typecheck(en);
    }
}
