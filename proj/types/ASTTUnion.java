package proj.types;

import proj.env.*;
import proj.errors.*;

import java.util.HashMap;

public class ASTTUnion implements ASTType {

    private TypeBindList ll;

    public ASTTUnion(TypeBindList llp) {
        ll = llp;
    }

    public String toStr() {
        String res = "union {";

        for (String k : ll.getMap().keySet()) {
            res += k + " = " + ll.getMap().get(k).toStr() + "; ";
        }

        if (ll.getMap().size() > 0) {
            res = res.substring(0, res.length()-2);
        }

        return res + "}";
    }

    public TypeBindList getList() {
        return ll;
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) throws InterpreterError {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        } else if (o instanceof ASTTUnion || o instanceof ASTTLUnion) {
            HashMap<String, ASTType> mb = o instanceof ASTTUnion ? ((ASTTUnion) o).getList().getMap() : ((ASTTLUnion) o).getList().getMap();
            HashMap<String, ASTType> ma = ll.getMap();
            for (String s : ma.keySet()) {
                if (!(mb.containsKey(s) && ma.get(s).isSubtypeOf(mb.get(s), e))) return false;
            }
            return true;
        }
        return false;
    }

    public boolean equals(Object o) {
        if (o instanceof ASTTUnion) {
            HashMap<String, ASTType> own = ll.getMap();
            HashMap<String, ASTType> other = ((ASTTUnion) o).getList().getMap();
            if (own.size() != other.size()) return false;
            for (String label : own.keySet()) {
                if (!(other.containsKey(label) && own.get(label).equals(other.get(label)))) return false;
            }
            return true;
        }
        return false;
    }
}