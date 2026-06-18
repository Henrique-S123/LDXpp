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

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        e.openEnvScope(ENV.PHI);
        for (String s : ltd.keySet()) {
            ASTType t = ltd.get(s);
            t.setSig(e.getSigma());
            e.bindToEnv(ENV.PHI, s, t);
        }
        ASTType ret = this.body.typecheck(e, target);
        e.closeEnvScope(ENV.PHI);
        return ret;
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        Env<ASTType> env = phi.beginScope();
        for (String s : ltd.keySet()) {
            ASTType t = ltd.get(s);
            t.setSig(sigma);
            env.assoc(s, t);
        }
        return body.puretypecheck(sigma, env, target);
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
