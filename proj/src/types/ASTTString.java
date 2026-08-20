package proj.src.types;

import proj.src.env.*;

public class ASTTString extends ASTType {
    public ASTTString() {
        lin = false;
    }

    public String toString() {
        return "string";
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe, alpha);
        return o instanceof ASTTString;
    }
}
