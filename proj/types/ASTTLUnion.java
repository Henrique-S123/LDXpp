package proj.types;

import proj.ast.ASTNode;
import proj.env.*;

import java.util.Map;

public class ASTTLUnion extends ASTLinType {

    Map<String, ASTType> ll;

    public ASTTLUnion(Map<String, ASTType> llp) {
        ll = llp;
    }

    public Map<String, ASTType> getMap() {
        return ll;
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e, alpha);
        if (o instanceof ASTTLUnion olunion) {
            Map<String, ASTType> mb = olunion.getMap();
            for (String s : ll.keySet())
                if (!(mb.containsKey(s) && ll.get(s).isSubtypeOf(mb.get(s), e, alpha))) return false;
            return true;
        }
        return false;
    }

    public boolean defequals(Env<ASTType> sl, ASTType o, Env<ASTType> sr, AlphaEnv alpha) {
        if (o instanceof ASTTLUnion olunion) {
            Map<String, ASTType> other = olunion.getMap();
            if (ll.size() != other.size()) return false;
            for (String label : ll.keySet()) {
                ASTType otherType = other.get(label);
                if (otherType == null || !ll.get(label).defequals(sl, otherType, sr, alpha)) return false;
            }
            return true;
        }
        return false;
    }

    public String toString() {
        String fill = "";
        for (String k : ll.keySet()) fill += String.format("%s = %s; ", k, ll.get(k));
        if (ll.size() > 0) fill = fill.substring(0, fill.length()-2);

        return String.format("linear union {%s}", fill);
    }

    public ASTType inst(String instId, ASTNode n) {
        ll.forEach((id, type) -> ll.put(id, type.inst(instId, n)));
        return this;
    }
}