package proj.src.types;

import proj.src.ast.ASTNode;
import proj.src.env.*;
import proj.src.env.PureEnvSet.PENV;
import proj.src.errors.*;

public class ASTTArrow extends ASTType {
    private final ASTType dom, codom;
    private final String id;
    private String bid;

    public ASTTArrow(ASTType d, ASTType co, String i, String bi, boolean l) {
        dom = d; codom = co; id = i; bid = bi; lin = l;
    }

    public ASTType getDom() { return dom; }

    public ASTType getCodom() { return codom; }

    public String getId() { return id; }

    public String getBid() { return bid; }

    public String toString() {
        String domStr = (id == null) ? ""+dom : String.format("(%s:%s)", id, dom);
        return String.format("%s-%s>%s", domStr, lin ? "o" : "", codom);
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe, alpha);
        if (o instanceof ASTTArrow ot && (!lin || ot.isLinear())) {
            if (!ot.getDom().isSubtypeOf(dom, pe, alpha)) return false;
            if (id != null && ot.getId() != null) alpha.extend(id, ot.getId());
            return codom.isSubtypeOf(ot.getCodom(), pe, alpha);
        }
        return false;
    }

    public ASTTArrow inst(String instId, ASTNode n) {
        return new ASTTArrow(dom.inst(instId, n), codom.inst(instId, n), id, bid, lin);
    }

    public ASTType check(PureEnvSet pe) throws TypeCheckError {
        dom.check(pe);
        if (id != null) {
            pe.openEnvScope(PENV.SIGMA);
            bid = pe.bindToEnv(PENV.SIGMA, id, dom).getId();
        }
        codom.check(pe);
        if (id != null) pe.closeEnvScope(PENV.SIGMA);
        return this;
    }
}

