package proj.types;

import proj.ast.ASTNode;
import proj.env.*;
import proj.errors.*;

import java.util.Map;

public class ASTTUnion extends ASTType {

    Map<String, ASTType> ll;

    public ASTTUnion(Map<String, ASTType> llp) {
        ll = llp;
    }

    public Map<String, ASTType> getMap() {
        return ll;
    }

    public void setSig(Env<ASTType> s) {
        sig = s;
        for (String id : ll.keySet())
            ll.get(id).setSig(s);
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), sigma, phi, alpha);
        Map<String, ASTType> mb;
        if (o instanceof ASTTUnion ounion) mb = ounion.getMap();
        else if (o instanceof ASTTLUnion olunion) mb = olunion.getMap();
        else return false;

        for (String s : ll.keySet())
            if (!(mb.containsKey(s) && ll.get(s).isSubtypeOf(mb.get(s), sigma, phi, alpha))) return false;
        return true;
    }

    public String toString() {
        String fill = "";
        for (String k : ll.keySet()) fill += String.format("%s = %s; ", k, ll.get(k));
        if (ll.size() > 0) fill = fill.substring(0, fill.length()-2);

        return String.format("union {%s}", fill);
    }

    public ASTType inst(String instId, ASTNode n) {
        ll.forEach((id, type) -> ll.put(id, type.inst(instId, n)));
        return this;
    }

    public ASTType check(Env<ASTType> sigma, Env<ASTType> phi) throws TypeCheckError {
        for (ASTType t : ll.values()) t.check(sigma, phi);
        return this;
    }
}