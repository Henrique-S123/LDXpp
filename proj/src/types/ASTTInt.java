package proj.src.types;

import proj.src.env.*;
import proj.src.defeq.IdPair;

import java.util.Set;

public class ASTTInt extends ASTType {
    public ASTTInt(boolean l) {
        lin = l;
    }
    
    public String toString() {
        return String.format("%sint", lin ? "lin" : "");
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe, AlphaEnv alpha, Set<IdPair> seen) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe, alpha, seen);
        return (o instanceof ASTTInt ot && (!lin || ot.isLinear()));
    }

    public boolean structEq(ASTType o) {
        return o instanceof ASTTInt ot && lin == ot.isLinear();
    }
}


