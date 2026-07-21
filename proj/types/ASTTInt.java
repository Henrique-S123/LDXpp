package proj.types;

import proj.env.*;

public class ASTTInt extends ASTType {

    public ASTTInt(boolean l) {
        lin = l;
    }
    
    public String toString() {
        return String.format("%sint", lin ? "lin" : "");
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), sigma, phi, alpha);
        return (o instanceof ASTTInt ot && (!lin || ot.isLinear()));
    }
}


