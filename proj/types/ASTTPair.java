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
        return "(" + (id != null ? id + ":" : "") + first.toString() + ", " + second.toString() + ")";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        ASTType ofirst, osecond;
        if (o instanceof ASTTPair opair) { ofirst = opair.getFirst(); osecond = opair.getSecond(); }
        else if (o instanceof ASTTTensor otensor) { ofirst = otensor.getFirst(); osecond = otensor.getSecond(); }
        else return false;
        return first.isSubtypeOf(ofirst, e) && second.isSubtypeOf(osecond, e);
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma) {
        return o instanceof ASTTPair opair && opair.getFirst().defequals(first, sigma)
            && opair.getSecond().defequals(second, sigma);
    }
}
