package proj.types;

import proj.env.*;

public class ASTTTensor implements ASTLinType {
    ASTType first, second;
    String id;

    public ASTTTensor(ASTType f, ASTType s, String i) {
        first = f;
        second = s;
        id = i;
    }

    public ASTType getFirst() {
        return first;
    }
    
    public ASTType getSecond() {
        return second;
    }

    public String getId() {
        return id;
    }

    public String toStr() {
        return "(" + first.toStr() + " | " + second.toStr() + ")";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        } else if (o instanceof ASTTTensor) {
            return first.isSubtypeOf(((ASTTTensor) o).getFirst(), e)
                && second.isSubtypeOf(((ASTTTensor) o).getSecond(), e);
        }
        return false;
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma) {
        return o instanceof ASTTTensor && ((ASTTTensor) o).getFirst().defequals(first, sigma)
            && ((ASTTTensor) o).getSecond().defequals(second, sigma);
    }
}
