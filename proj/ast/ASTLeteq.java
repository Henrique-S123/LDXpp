package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTLeteq implements ASTNode {
    Bind bind;
    ASTNode body;

    public ASTLeteq(Bind bi, ASTNode b) {
        bind = bi;
        body = b;
    }

    public Bind getBind() {
        return bind;
    }

    public ASTNode getBody() {
        return body;
    }

    public IValue eval(Env<IValue> e) throws InterpreterError {
        Env<IValue> en = e.beginScope();
        en.assoc(bind.getId(), bind.getExp().eval(en));
        return body.eval(en);
    }
	
    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        String id = bind.getId();
        ASTNode expr = bind.getExp();
        ASTType t = expr.typecheck(e);
        t = e.unfold(t);

        if (!(t instanceof ASTTEq)) throw new TypeCheckError("leteq: expected equality type");
        e.openEnvScope(ENV.SIGMA);
        e.bindToEnv(ENV.SIGMA, id, t);

        ASTType rt = body.typecheck(e);
        if (!(e.getEnv(ENV.DELTA).isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + e.getEnv(ENV.DELTA));
        e.closeEnvScope(ENV.SIGMA);
        return rt;
    }

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        ASTNode normExp = bind.getExp().normalize(sigma, sub);
        Env<ASTNode> esub = sub.beginScope();
        esub.assoc(bind.getId(), normExp);
        return body.normalize(sigma, esub);
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTLeteq oleteq && bind.getExp().defequals(oleteq.getBind().getExp(), sigma, alpha)
            && bind.getType().defequals(oleteq.getBind().getType(), sigma, alpha)
            && body.defequals(oleteq.getBody(), sigma, alpha.extend(bind.getId(), oleteq.getBind().getId()));
    }
}
