package proj.src.types;

import proj.src.env.*;

public class ASTTString extends ASTType {

    public ASTTString() {
        lin = false;
    }

    public String toString() {
        return "string";
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), sigma, phi, alpha);
        return o instanceof ASTTString;
    }
}
