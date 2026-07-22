package proj.src.types;

import proj.src.env.*;

public class ASTTUnit extends ASTType {

    public ASTTUnit() {
        lin = false;
    }

    public String toString() {
        return "()";
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), sigma, phi, alpha);
        return o instanceof ASTTUnit;
    }
}