package proj.src.types;

import proj.src.ast.ASTNode;
import proj.src.env.*;
import proj.src.env.PureEnvSet.PENV;
import proj.src.errors.*;

public class ASTTPair extends ASTType {
    ASTType first, second;
    String id, bid;

    public ASTTPair(ASTType f, ASTType s, String i, String bi, boolean l) {
        first = f; second = s; id = i; bid = bi; lin = l;
    }

    public ASTType getFirst() { return first; }
    
    public ASTType getSecond() { return second; }

    public String getId() { return id; }

    public String getBid() { return bid; }

    public String toString() {
        return String.format("(%s%s%s %s)", id != null ? id+":" : "", first, lin ? " |" : ",", second);
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe);
        if (o instanceof ASTTPair ot && (!lin || ot.isLinear())) {
            if (!first.isSubtypeOf(ot.getFirst(), pe)) return false;
            if (id != null && ot.getId() != null) pe.extendAlpha(id, ot.getId());
            return second.isSubtypeOf(ot.getSecond(), pe);
        }
        return false;
    }

    public ASTTPair inst(String instId, ASTNode n) {
        return new ASTTPair(first.inst(instId, n), second.inst(instId, n), id, bid, lin);
    }

    public ASTType check(PureEnvSet pe) throws TypeCheckError {
        first.check(pe);
        if (id != null) {
            pe.openEnvScope(PENV.SIGMA);
            bid = pe.bindToEnv(PENV.SIGMA, id, first);
        }
        second.check(pe);
        return this;
    }
}
