package proj.types;

import proj.ast.ASTNode;
import proj.env.*;
import proj.errors.*;

public class ASTTPair extends ASTType {
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

    public void setSig(Env<ASTType> s) {
        sig = s;
        first.setSig(s);
        second.setSig(s);
    }

    public String toString() {
        return String.format("(%s%s, %s)", id != null ? id+":" : "", first, second);
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), sigma, phi, alpha);
        ASTType ofirst, osecond;
        String oid;
        if (o instanceof ASTTPair opair) { ofirst = opair.getFirst(); osecond = opair.getSecond(); oid = opair.getId(); }
        else if (o instanceof ASTTTensor otensor) { ofirst = otensor.getFirst(); osecond = otensor.getSecond(); oid = otensor.getId(); }
        else return false;

        if (!first.isSubtypeOf(ofirst, sigma, phi, alpha)) return false;
        if (id != null && oid != null) alpha.extend(id, oid);
        return second.isSubtypeOf(osecond, sigma, phi, alpha);
    }

    public ASTType inst(String instId, ASTNode n) {
        return new ASTTPair(first.inst(instId, n), second.inst(instId, n), id);
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
