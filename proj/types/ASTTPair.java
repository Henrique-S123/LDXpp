package proj.types;

import proj.env.*;

public class ASTTPair implements ASTType {
    ASTType first, second;
    String id;

    public ASTTPair(ASTType f, ASTType s, String i) {
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
        return String.format("(%s%s, %s)", id != null ? id+":" : "", first, second);
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e);
        ASTType ofirst, osecond;
        if (o instanceof ASTTPair opair) { ofirst = opair.getFirst(); osecond = opair.getSecond(); }
        else if (o instanceof ASTTTensor otensor) { ofirst = otensor.getFirst(); osecond = otensor.getSecond(); }
        else return false;
        return first.isSubtypeOf(ofirst, e) && second.isSubtypeOf(osecond, e);
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTPair opair && first.defequals(opair.getFirst(), sigma, alpha)
            && second.defequals(opair.getSecond(), sigma, alpha.extend(id, opair.getId()));
    }
}
