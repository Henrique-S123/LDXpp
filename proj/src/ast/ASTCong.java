package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.errors.*;

public class ASTCong extends ASTNode {
    private final String var;

    public ASTCong(String v) {
        var = v;
    }

    public IValue eval(Env<IValue> e) {
        return new VRefl();
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        return expandVar(e.getSigma());
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        return expandVar(pe.getSigma());
    }

    private ASTTEq expandVar(Env<ASTType> sigma) throws TypeCheckError {
        ASTType type = sigma.find(var);
        String bid = sigma.findBinderId(var);
        if (type == null) throw new TypeCheckError(ErrorMessages.idNotFound(var));
        ASTNode t1 = new ASTId(var, bid);
        ASTNode t2 = null;

        if (type instanceof ASTTUnit) t2 = new ASTUnit();
        else if (type instanceof ASTTPair t && !t.isLinear())
            t2 = new ASTPair(new ASTChoice(t1, true), new ASTChoice(t1, false), false);

        return new ASTTEq(t1, t2, type);
    }

    @Override
    public String toString() {
        return String.format("cong %s", var);
    }
}