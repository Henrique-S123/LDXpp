package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.env.EnvSet.ENV;
import proj.src.env.PureEnvSet.PENV;
import proj.src.errors.*;

import java.util.HashMap;

public class ASTTypeDef extends ASTNode {
    private final HashMap<String, ASTType> ltmap;
    private final ASTNode body;

    public ASTTypeDef(HashMap<String, ASTType> l, ASTNode b) {
        ltmap = l; body = b;
    }

    public HashMap<String, ASTType> getLtd() { return ltmap; }

    public ASTNode getBody() { return body; }
    
    public IValue eval(Env<IValue> env) throws InterpreterError {
        return body.eval(env);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        e.openEnvScope(ENV.PHI);
        for (String s : ltmap.keySet()) e.bindToEnv(ENV.PHI, s, ltmap.get(s));
        for (String s : ltmap.keySet()) ltmap.get(s).check(new PureEnvSet(e));
        ASTType ret = this.body.typecheck(e, target);
        e.closeEnvScope(ENV.PHI);
        return ret;
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        pe.openEnvScope(PENV.PHI);
        for (String s : ltmap.keySet()) pe.bindToEnv(PENV.PHI, s, ltmap.get(s));
        for (String s : ltmap.keySet()) ltmap.get(s).check(pe);
        ASTType ret = this.body.puretypecheck(pe, target);
        pe.closeEnvScope(PENV.PHI);
        return ret;
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return body.weaknorm(sub);
    }

    public ASTNode subs(String subsId, ASTNode node) {
		return new ASTTypeDef(ltmap, body.subs(subsId, node));
	}

    @Override
	public String toString() {
		return String.format("typedef(%s, %s)", ltmap, body);
	}
}
