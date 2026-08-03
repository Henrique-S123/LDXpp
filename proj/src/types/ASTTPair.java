package proj.src.types;

import proj.src.ast.ASTNode;
import proj.src.env.*;
import proj.src.env.PureEnvSet.PENV;
import proj.src.errors.*;

public class ASTTPair extends ASTType {
    ASTType first, second;
    String id;

    public ASTTPair(ASTType f, ASTType s, String i, boolean l) {
        first = f; second = s; id = i; lin = l;
    }

    public ASTType getFirst() { return first; }
    
    public ASTType getSecond() { return second; }

    public String getId() { return id; }

    public void setSig(Env<ASTType> s) {
        sig = s;
        first.setSig(s);
        second.setSig(s);
    }

    public String toString() {
        return String.format("(%s%s%s %s)", id != null ? id+":" : "", first, lin ? " |" : ",", second);
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe);
        ASTType ofirst, osecond;
        String oid;
        if (o instanceof ASTTPair ot && (!lin || ot.isLinear())) { ofirst = ot.getFirst(); osecond = ot.getSecond(); oid = ot.getId(); }
        else return false;

        if (!first.isSubtypeOf(ofirst, pe)) return false;
        if (id != null && oid != null) pe.extendAlpha(id, oid);
        return second.isSubtypeOf(osecond, pe);
    }

    public ASTTPair inst(String instId, ASTNode n) {
        return new ASTTPair(first.inst(instId, n), second.inst(instId, n), id, lin);
    }

    public ASTType check(PureEnvSet pe) throws TypeCheckError {
        first.check(pe);
        if (id != null) {
            pe.openEnvScope(PENV.SIGMA);
            pe.bindToEnv(PENV.SIGMA, id, first);
        }
        second.check(pe);
        return this;
    }
}
