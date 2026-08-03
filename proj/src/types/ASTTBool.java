package proj.src.types;

import proj.src.env.*;

public class ASTTBool extends ASTType {

    public ASTTBool(boolean l) {
        lin = l;
    }

    public String toString() {
        return String.format("%sbool", lin ? "lin" : "");
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe);
        return (o instanceof ASTTBool ot && (!lin || ot.isLinear()));
    }
}