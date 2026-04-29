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
        }
        Map<String, ASTType> mb;
        if (o instanceof ASTTUnion ounion) mb = ounion.getMap();
        else if (o instanceof ASTTLUnion olunion) mb = olunion.getMap();
        else return false;

        for (String s : ll.keySet())
            if (!(mb.containsKey(s) && ll.get(s).isSubtypeOf(mb.get(s), e))) return false;
        return true;
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma) {
        if (o instanceof ASTTUnion ounion) {
            Map<String, ASTType> other = ounion.getMap();
            if (ll.size() != other.size()) return false;
            for (String label : ll.keySet()) {
                ASTType otherType = other.get(label);
                if (otherType == null || !ll.get(label).defequals(otherType, sigma)) return false;
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