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

    public boolean isSubtypeOf(ASTType o, EnvSet e, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e, alpha);
        ASTType ofirst, osecond;
        String oid;
        if (o instanceof ASTTPair opair) { ofirst = opair.getFirst(); osecond = opair.getSecond(); oid = opair.getId(); }
        else if (o instanceof ASTTTensor otensor) { ofirst = otensor.getFirst(); osecond = otensor.getSecond(); oid = otensor.getId(); }
        else return false;
        return first.isSubtypeOf(ofirst, e, alpha) && second.isSubtypeOf(osecond, e, alpha.extend(id, oid));
    }

    public ASTType inst(String instId, ASTNode n) {
        return new ASTTPair(first.inst(instId, n), second.inst(instId, n), id);
    }

    public ASTType check(Env<ASTType> sigma, Env<ASTType> phi) throws TypeCheckError, EnvironmentError {
        first.check(sigma, phi);
        Env<ASTType> env = sigma.beginScope();
        env.assoc(id, first);
        second.check(sigma, phi);
        return this;
    }
}
