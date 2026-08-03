package proj.src.types;

import proj.src.env.*;

public class ASTTUnit extends ASTType {

    public ASTTUnit() {
        lin = false;
    }

    public String toString() {
        return "()";
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe);
        return o instanceof ASTTUnit;
    }
}