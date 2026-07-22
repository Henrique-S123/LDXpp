package proj.src.types;

import proj.src.ast.ASTNode;
import proj.src.env.*;
import proj.src.errors.*;

import java.util.Map;

public class ASTTUnion extends ASTType {

    Map<String, ASTType> ll;

    public ASTTUnion(Map<String, ASTType> llp, boolean l) {
        ll = llp; lin = l;
    }

    public Map<String, ASTType> getMap() { return ll; }

    public void setSig(Env<ASTType> s) {
        sig = s;
        for (String id : ll.keySet())
            ll.get(id).setSig(s);
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), sigma, phi, alpha);
        Map<String, ASTType> mb;
        if (o instanceof ASTTUnion ot && (!lin || ot.isLinear())) mb = ot.getMap();
        else return false;

        for (String s : ll.keySet())
            if (!(mb.containsKey(s) && ll.get(s).isSubtypeOf(mb.get(s), sigma, phi, alpha))) return false;
        return true;
    }

    public String toString() {
        String fill = "";
        for (String k : ll.keySet()) fill += String.format("%s: %s; ", k, ll.get(k));
        if (ll.size() > 0) fill = fill.substring(0, fill.length()-2);

        return String.format("%sunion {%s}", lin ? "lin" : "", fill);
    }

    public ASTTUnion inst(String instId, ASTNode n) {
        ll.forEach((id, type) -> ll.put(id, type.inst(instId, n)));
        return this;
    }

    public ASTType check(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) throws TypeCheckError {
        for (ASTType t : ll.values()) t.check(sigma, phi, alpha);
        return this;
    }
}