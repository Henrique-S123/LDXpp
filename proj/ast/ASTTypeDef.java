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

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        e.openEnvScope(ENV.PHI);
        for (String s : ltd.keySet())
            e.bindToEnv(ENV.PHI, s, ltd.get(s));
        ASTType ret = this.body.typecheck(e);
        e.closeEnvScope(ENV.PHI);
        return ret;
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return new ASTTypeDef(ltd, body.weaknorm(sub));
    }
    
    public ASTNode solve(Env<ASTType> sigma) {
        ASTNode nbody = body.solve(sigma);
        return (nbody == null) ? null : new ASTTypeDef(ltd, nbody);
    }

    public ASTNode subs(String subsId, ASTNode node) {
		return new ASTTypeDef(ltd, body.subs(subsId, node));
	}

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTypeDef otype && otype.getBody().defequals(body, sigma, alpha)
            && otype.getLtd().equals(ltd);
    }

    @Override
	public String toString() {
		return String.format("typdef(%s, %s)", ltd, body);
	}
}
