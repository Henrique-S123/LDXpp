package proj.types;

import proj.env.*;
import proj.errors.*;

import java.util.HashMap;

public class ASTTStruct implements ASTType {

    private TypeBindList ll;

    public ASTTStruct(TypeBindList llp) {
        ll = llp;
    }

    public String toStr() {
        String res = "struct {";

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

    public boolean isSubtypeOf(ASTType o, Environment<ASTType> e) throws InterpreterError {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        } else if (o instanceof ASTTStruct) {
            HashMap<String, ASTType> mb = ((ASTTStruct) o).getList().getMap();
            HashMap<String, ASTType> ma = ll.getMap();
            for (String s : mb.keySet()) {
                if (!(ma.containsKey(s) && ma.get(s).isSubtypeOf(mb.get(s), e)))
                    return false;
            }
            return true;
        }
        return false;
    }
}