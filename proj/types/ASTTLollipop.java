package proj.types;

import proj.ast.ASTNode;
import proj.env.*;
import proj.errors.*;

public class ASTTLollipop extends ASTLinType {
    ASTType dom, codom;
    String id;

    public ASTTLollipop(ASTType d, ASTType co, String i) {
        dom = d;
        codom = co;
        id = i;
    }

    public ASTType getDom() {
        return dom;
    }

    public ASTType getCodom() {
        return codom;
    }

    public String getId() {
        return id;
    }

    public void setSig(Env<ASTType> s) {
        sig = s;
        dom.setSig(s);
        codom.setSig(s);
    }

    public String toString() {
        String domStr = (id == null) ? ""+dom : String.format("(%s:%s)", id, dom);
        return String.format("%s-o>%s", domStr, codom);
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e, alpha);
        ASTType odom, ocodom;
        String oid;
        if (o instanceof ASTTLollipop lolli) { odom = lolli.getDom(); ocodom = lolli.getCodom(); oid = lolli.getId(); }
        else return false;
        return odom.isSubtypeOf(dom, e, alpha) && codom.isSubtypeOf(ocodom, e, alpha.extend(id, oid));
    }

    public ASTType inst(String instId, ASTNode n) {
        return new ASTTLollipop(dom.inst(instId, n), codom.inst(instId, n), id);
    }

    public ASTType check(Env<ASTType> sigma, Env<ASTType> phi) throws TypeCheckError, EnvironmentError {
        dom.check(sigma, phi);
        Env<ASTType> env = sigma.beginScope();
        env.assoc(id, dom);
        codom.check(sigma, phi);
        return this;
    }
}

