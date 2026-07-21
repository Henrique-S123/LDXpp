package proj.types;

import proj.ast.ASTNode;
import proj.env.*;
import proj.errors.*;

public class ASTTTensor extends ASTType {
    ASTType first, second;
    String id;

    public ASTTTensor(ASTType f, ASTType s, String i) {
        first = f;
        second = s;
        id = i;
        lin = true;
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
            if (!first.isSubtypeOf(otensor.getFirst(), sigma, phi, alpha)) return false;
            if (id != null && otensor.getId() != null) alpha.extend(id, otensor.getId());
            return second.isSubtypeOf(otensor.getSecond(), sigma, phi, alpha);
        }
        return false;
    }

    public ASTTTensor inst(String instId, ASTNode n) {
        return new ASTTTensor(first.inst(instId, n), second.inst(instId, n), id);
    }

    public ASTType check(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) throws TypeCheckError {
        first.check(sigma, phi, alpha);
        Env<ASTType> env = sigma;
        if (id != null) {
            env = env.beginScope();
            env.assoc(id, first);
        }
        second.check(env, phi, alpha);
        return this;
    }
}
