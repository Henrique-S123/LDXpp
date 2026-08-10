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

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe);
        Map<String, ASTType> mb;
        if (o instanceof ASTTUnion ot && (!lin || ot.isLinear())) mb = ot.getMap();
        else return false;

        for (String s : ll.keySet())
            if (!(mb.containsKey(s) && ll.get(s).isSubtypeOf(mb.get(s), pe))) return false;
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

    public ASTType check(PureEnvSet pe) throws TypeCheckError {
        for (ASTType t : ll.values()) t.check(pe);
        return this;
    }
}