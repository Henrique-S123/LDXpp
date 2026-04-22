package proj.types;

import proj.env.*;

import java.util.Map;

public class ASTTUnion implements ASTType {

    Map<String, ASTType> ll;

    public ASTTUnion(Map<String, ASTType> llp) {
        ll = llp;
    }

    public Map<String, ASTType> getMap() {
        return ll;
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        } else if (o instanceof ASTTUnion || o instanceof ASTTLUnion) {
            Map<String, ASTType> mb = o instanceof ASTTUnion ? ((ASTTUnion) o).getMap() : ((ASTTLUnion) o).getMap();
            for (String s : ll.keySet())
                if (!(mb.containsKey(s) && ll.get(s).isSubtypeOf(mb.get(s), e))) return false;
            return true;
        }
        return false;
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma) {
        if (o instanceof ASTTUnion) {
            Map<String, ASTType> other = ((ASTTUnion) o).getMap();
            if (ll.size() != other.size()) return false;
            for (String label : ll.keySet()) {
                if (!(other.containsKey(label) && ll.get(label).defequals(other.get(label), sigma))) return false;
            }
            return true;
        }
        return false;
    }

    public String toStr() {
        String res = "union {";

        for (String k : ll.keySet()) {
            res += k + " = " + ll.get(k).toStr() + "; ";
        }

        if (ll.size() > 0) {
            res = res.substring(0, res.length()-2);
        }

        return res + "}";
    }
}