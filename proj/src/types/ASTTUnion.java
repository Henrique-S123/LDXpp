package proj.src.types;

import proj.src.ast.ASTNode;
import proj.src.env.*;
import proj.src.errors.*;

import java.util.HashMap;
import java.util.Map;

public class ASTTUnion extends ASTType {
    private final Map<String, ASTType> ll;

    public ASTTUnion(Map<String, ASTType> llp, boolean l) {
        ll = llp; lin = l;
    }

    public Map<String, ASTType> getMap() { return ll; }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe, alpha);
        if (o instanceof ASTTUnion ot && (!lin || ot.isLinear())) {
            for (String s : ll.keySet())
                if (!ot.getMap().containsKey(s) || !ll.get(s).isSubtypeOf(ot.getMap().get(s), pe, alpha)) return false;
            return true;
        }
        return false;
    }

    public String toString() {
        String fill = "";
        for (String k : ll.keySet()) fill += String.format("%s: %s; ", k, ll.get(k));
        if (ll.size() > 0) fill = fill.substring(0, fill.length()-2);

        return String.format("%sunion {%s}", lin ? "lin" : "", fill);
    }

    public ASTTUnion inst(String instId, ASTNode n) {
        Map<String, ASTType> newll = new HashMap<String, ASTType>();
        ll.forEach((id, type) -> newll.put(id, type.inst(instId, n)));
        return new ASTTUnion(newll, lin);
    }

    public ASTType check(PureEnvSet pe) throws TypeCheckError {
        for (ASTType t : ll.values()) t.check(pe);
        return this;
    }
}