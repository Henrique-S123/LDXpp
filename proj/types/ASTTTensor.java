package proj.types;

import proj.ast.ASTNode;
import proj.env.*;
import proj.errors.*;

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

    public void setSig(Env<ASTType> s) {
        sig = s;
        first.setSig(s);
        second.setSig(s);
    }

    public String toString() {
        return String.format("(%s%s | %s)", id != null ? id+":" : "", first, second);
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), sigma, phi, alpha);
        if (o instanceof ASTTTensor otensor) {
            return first.isSubtypeOf(otensor.getFirst(), sigma, phi, alpha)
                && second.isSubtypeOf(otensor.getSecond(), sigma, phi, alpha.extend(id, otensor.getId()));
        }
        return false;
    }

    public ASTType inst(String instId, ASTNode n) {
        return new ASTTTensor(first.inst(instId, n), second.inst(instId, n), id);
    }

    public ASTType check(Env<ASTType> sigma, Env<ASTType> phi) throws TypeCheckError, EnvironmentError {
        first.check(sigma, phi);
        Env<ASTType> env = sigma.beginScope();
        env.assoc(id, first);
        second.check(sigma, phi);
        return this;
    }
}
