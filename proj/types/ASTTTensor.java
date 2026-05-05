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

    public String toString() {
        return String.format("(%s%s | %s)", id != null ? id+":" : "", first, second);
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        } else if (o instanceof ASTTTensor otensor) {
            return first.isSubtypeOf(otensor.getFirst(), e)
                && second.isSubtypeOf(otensor.getSecond(), e);
        }
        return false;
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTTensor otensor && first.defequals(otensor.getFirst(), sigma, alpha)
            && second.defequals(otensor.getSecond(), sigma, alpha.extend(id, otensor.getId()));
    }
}
