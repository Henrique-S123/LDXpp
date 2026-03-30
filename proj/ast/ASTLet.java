package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.List;

public class ASTLet implements ASTNode {
    List<Bind> decls;
    ASTNode body;

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        Environment<IValue> en = e.beginScope();
        for (Bind b : decls) {
            en.assoc(b.getId(), b.getExp().eval(en));
        }
        return body.eval(en);
    }

    public ASTLet(List<Bind> d, ASTNode b) {
        decls = d;
        body = b;
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, InterpreterError {
        boolean gammaExpanded = false, deltaExpanded = false;
        for (Bind b : decls) {
            ASTType tt = b.getType();
            if (tt != null) {
                // premptive type binding
                tt = e.unfold(tt);
                if (tt instanceof ASTLinType) {
                    if (!deltaExpanded) e.newDeltaScope();
                    e.assocDelta(b.getId(), tt);
                    deltaExpanded = true;
                } else {
                    if (!gammaExpanded) e.newGammaScope();
                    e.assocGamma(b.getId(), tt);
                    gammaExpanded = true;
                }
                ASTType valuetype = b.getExp().typecheck(e);
                if (!(valuetype.isSubtypeOf(tt, e))) {
                    throw new TypeCheckError("types to bind are not subtypes: " + valuetype.toStr() + " and " + tt.toStr());
                }
            } else {
                ASTType t = b.getExp().typecheck(e);
                t = e.unfold(t);
                if (t instanceof ASTLinType) {
                    if (!deltaExpanded) e.newDeltaScope();
                    e.assocDelta(b.getId(), t);
                    deltaExpanded = true;
                } else {
                    if (!gammaExpanded) e.newGammaScope();
                    e.assocGamma(b.getId(), t);
                    gammaExpanded = true;
                }
            }
        }
        ASTType rt = body.typecheck(e);
        if (!(e.getDelta().isEmpty()))
            throw new TypeCheckError("there are unused linear values: " + e.getDelta().toStr());
        if (gammaExpanded) e.closeGammaScope();
        if (deltaExpanded) e.closeDeltaScope();
        return rt;
	}

    public ASTNode normalize() {
        return this;
    }
}
