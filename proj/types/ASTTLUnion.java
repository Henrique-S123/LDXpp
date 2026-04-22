package proj.types;

import proj.env.*;

import java.util.Map;

public class ASTTLUnion implements ASTLinType {

    Map<String, ASTType> ll;

    public ASTTLUnion(Map<String, ASTType> llp) {
        ll = llp;
    }

    public Map<String, ASTType> getMap() {
        return ll;
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        } else if (o instanceof ASTTLUnion) {
            Map<String, ASTType> mb = ((ASTTLUnion) o).getMap();
            for (String s : ll.keySet())
                if (!(mb.containsKey(s) && ll.get(s).isSubtypeOf(mb.get(s), e))) return false;
            return true;
        }
        return false;
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma) {
        if (o instanceof ASTTLUnion) {
            Map<String, ASTType> other = ((ASTTLUnion) o).getMap();
            if (ll.size() != other.size()) return false;
            for (String label : ll.keySet()) {
                if (!(other.containsKey(label) && ll.get(label).defequals(other.get(label), sigma))) return false;
            }
            return true;
        }
        return false;
    }

    public String toStr() {
        String res = "linear union {";

        for (String k : ll.keySet()) {
            res += k + " = " + ll.get(k).toStr() + "; ";
        }

        if (ll.size() > 0) {
            res = res.substring(0, res.length()-2);
        }

        return res + "}";
    }
}