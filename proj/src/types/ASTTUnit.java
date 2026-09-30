package proj.src.types;

import proj.src.env.*;
import proj.src.defeq.IdPair;

import java.util.Set;

public class ASTTUnit extends ASTType {
    public ASTTUnit() {
        lin = false;
    }

    public String toString() {
        return "()";
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe, AlphaEnv alpha, Set<IdPair> seen) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe, alpha, seen);
        return o instanceof ASTTUnit;
    }

    public boolean structEq(ASTType o) {
        return o instanceof ASTTUnit;
    }
}