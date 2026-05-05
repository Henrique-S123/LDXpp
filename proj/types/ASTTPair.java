package proj.types;

import proj.ast.ASTNode;
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

    public boolean defequals(ASTType o, Env<ASTType> sigma, Env<ASTNode> alphaL, Env<ASTNode> alphaR) {
        return o instanceof ASTTPair opair && first.defequals(opair.getFirst(), sigma, alphaL, alphaR)
            && second.defequals(opair.getSecond(), sigma, alphaL, alphaR);
    }
}
