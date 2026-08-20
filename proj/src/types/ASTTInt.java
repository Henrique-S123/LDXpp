package proj.src.types;

import proj.src.env.*;

public class ASTTInt extends ASTType {

    public ASTTInt(boolean l) {
        lin = l;
    }
    
    public String toString() {
        return String.format("%sint", lin ? "lin" : "");
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe, alpha);
        return (o instanceof ASTTInt ot && (!lin || ot.isLinear()));
    }
}


