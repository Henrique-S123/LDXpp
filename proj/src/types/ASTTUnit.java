package proj.src.types;

import proj.src.env.*;

public class ASTTUnit extends ASTType {

    public ASTTUnit() {
        lin = false;
    }

    public String toString() {
        return "()";
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe, alpha);
        return o instanceof ASTTUnit;
    }
}