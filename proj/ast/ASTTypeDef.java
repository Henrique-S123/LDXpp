package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

import java.util.HashMap;

public class ASTTypeDef extends ASTNode {
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
    
    public IValue eval(Env<IValue> env) throws InterpreterError {
        return body.eval(env);
    }

    public ASTType typeinfer(EnvSet e) throws TypeCheckError, EnvironmentError {
        e.openEnvScope(ENV.PHI);
        for (String s : ltd.keySet()) {
            ASTType t = ltd.get(s);
            t.setSig(e.getSigma());
            e.bindToEnv(ENV.PHI, s, t);
        }
        ASTType ret = this.body.typeinfer(e);
        e.closeEnvScope(ENV.PHI);
        return ret;
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return new ASTTypeDef(ltd, body.weaknorm(sub));
    }

    public ASTNode subs(String subsId, ASTNode node) {
		return new ASTTypeDef(ltd, body.subs(subsId, node));
	}

    @Override
	public String toString() {
		return String.format("typdef(%s, %s)", ltd, body);
	}
}
