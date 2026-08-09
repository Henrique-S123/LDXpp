package proj.src.types;

import proj.src.ast.ASTNode;
import proj.src.env.*;
import proj.src.env.PureEnvSet.PENV;
import proj.src.errors.*;

public class ASTTArrow extends ASTType {
    ASTType dom, codom;
    String id, bid;

    public ASTTArrow(ASTType d, ASTType co, String i, String bi, boolean l) {
        dom = d; codom = co; id = i; bid = bi; lin = l;
    }

    public ASTType getDom() { return dom; }

    public ASTType getCodom() { return codom; }

    public String getId() { return id; }

    public String getBid() { return bid; }

    public void setSig(Env<ASTType> s) {
        sig = s;
        dom.setSig(s);
        codom.setSig(s);
    }

    public String toString() {
        String domStr = (id == null) ? ""+dom : String.format("(%s:%s)", id, dom);
        return String.format("%s-%s>%s", domStr, lin ? "o" : "", codom);
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe);
        ASTType odom, ocodom;
        String oid;
        if (o instanceof ASTTArrow ot && (!lin || ot.isLinear())) { odom = ot.getDom(); ocodom = ot.getCodom(); oid = ot.getId(); }
        else return false;

        if (!odom.isSubtypeOf(dom, pe)) return false;
        if (id != null && oid != null) pe.extendAlpha(id, oid);
        return codom.isSubtypeOf(ocodom, pe);
    }

    public ASTTArrow inst(String instId, ASTNode n) {
        return new ASTTArrow(dom.inst(instId, n), codom.inst(instId, n), id, bid, lin);
    }

    public ASTType check(PureEnvSet pe) throws TypeCheckError {
        dom.check(pe);
        if (id != null) {
            Binder<ASTType> b = new Binder<ASTType>(dom);
            bid = b.getId();
            pe.openEnvScope(PENV.SIGMA);
            pe.bindToEnv(PENV.SIGMA, id, b);
        }
        codom.check(pe);
        return this;
    }
}

