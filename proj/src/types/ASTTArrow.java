package proj.src.types;

import proj.src.ast.*;
import proj.src.env.*;
import proj.src.env.PureEnvSet.PENV;
import proj.src.errors.*;

public class ASTTArrow extends ASTType {
    private final ASTType dom, codom;
    private final ASTId bind;

    public ASTTArrow(ASTType d, ASTType co, ASTId b, boolean l) {
        dom = d; codom = co; bind = b; lin = l;
    }

    public ASTType getDom() { return dom; }

    public ASTType getCodom() { return codom; }

    public String getId() { return bind.getId(); }

    public String getBid() { return bind.getBid(); }

    public String toString() {
        String domStr = (bind.getId() == null) ? ""+dom : String.format("(%s:%s)", bind.getId(), dom);
        return String.format("%s-%s>%s", domStr, lin ? "o" : "", codom);
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe, alpha);
        if (o instanceof ASTTArrow ot && (!lin || ot.isLinear())) {
            if (!ot.getDom().isSubtypeOf(dom, pe, alpha)) return false;
            if (bind.getId() != null && ot.getId() != null) alpha.extend(bind.getId(), ot.getId());
            return codom.isSubtypeOf(ot.getCodom(), pe, alpha);
        }
        return false;
    }

    public ASTTArrow inst(String instId, ASTNode n) {
        return new ASTTArrow(dom.inst(instId, n), codom.inst(instId, n), bind, lin);
    }

    public ASTType check(PureEnvSet pe) throws TypeCheckError {
        dom.check(pe);
        if (bind.getId() != null) {
            pe.openEnvScope(PENV.SIGMA);
            bind.setBid(pe.bindToEnv(PENV.SIGMA, bind.getId(), dom).getId());;
        }
        codom.check(pe);
        if (bind.getId() != null) pe.closeEnvScope(PENV.SIGMA);
        return this;
    }
}

