package proj.src.types;

import proj.src.env.*;

public class ASTTBool extends ASTType {

    public ASTTBool(boolean l) {
        lin = l;
    }

    public String toString() {
        return String.format("%sbool", lin ? "lin" : "");
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), sigma, phi, alpha);
        return (o instanceof ASTTBool ot && (!lin || ot.isLinear()));
    }
}