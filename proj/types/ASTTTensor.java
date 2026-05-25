package proj.types;

import proj.ast.ASTNode;
import proj.env.*;

public class ASTTTensor extends ASTLinType {
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

    public boolean isSubtypeOf(ASTType o, EnvSet e, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e, alpha);
        if (o instanceof ASTTTensor otensor) {
            return first.isSubtypeOf(otensor.getFirst(), e, alpha)
                && second.isSubtypeOf(otensor.getSecond(), e, alpha.extend(id, otensor.getId()));
        }
        return false;
    }

    public boolean defequals(Env<ASTType> sl, ASTType o, Env<ASTType> sr, AlphaEnv alpha) {
        return o instanceof ASTTTensor otensor && first.defequals(sl, otensor.getFirst(), sr, alpha)
            && second.defequals(sl, otensor.getSecond(), sr, alpha.extend(id, otensor.getId()));
    }

    public ASTType inst(String instId, ASTNode n) {
        return new ASTTTensor(first.inst(instId, n), second.inst(instId, n), id);
    }
}
