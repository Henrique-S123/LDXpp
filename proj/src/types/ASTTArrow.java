package proj.src.types;

import proj.src.ast.ASTNode;
import proj.src.env.*;
import proj.src.errors.*;

public class ASTTArrow extends ASTType {
    ASTType dom, codom;
    String id;

    public ASTTArrow(ASTType d, ASTType co, String i, boolean l) {
        dom = d; codom = co; id = i; lin = l;
    }

    public ASTType getDom() { return dom; }

    public ASTType getCodom() { return codom; }

    public String getId() { return id; }

    public void setSig(Env<ASTType> s) {
        sig = s;
        dom.setSig(s);
        codom.setSig(s);
    }

    public String toString() {
        String domStr = (id == null) ? ""+dom : String.format("(%s:%s)", id, dom);
        return String.format("%s-%s>%s", domStr, lin ? "o" : "", codom);
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), sigma, phi, alpha);
        ASTType odom, ocodom;
        String oid;
        if (o instanceof ASTTArrow ot && (!lin || ot.isLinear())) { odom = ot.getDom(); ocodom = ot.getCodom(); oid = ot.getId(); }
        else return false;

        if (!odom.isSubtypeOf(dom, sigma, phi, alpha)) return false;
        if (id != null && oid != null) alpha.extend(id, oid);
        return codom.isSubtypeOf(ocodom, sigma, phi, alpha);
    }

    public ASTTArrow inst(String instId, ASTNode n) {
        return new ASTTArrow(dom.inst(instId, n), codom.inst(instId, n), id, lin);
    }

    public ASTType check(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) throws TypeCheckError {
        dom.check(sigma, phi, alpha);
        Env<ASTType> env = sigma;
        if (id != null) {
            env = env.beginScope();
            env.assoc(id, dom);
        }
        codom.check(env, phi, alpha);
        return this;
    }
}

