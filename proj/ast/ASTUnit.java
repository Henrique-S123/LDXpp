package proj.ast;

import proj.env.*;
import proj.types.*;

public class ASTUnit extends ASTNode  {

    public ASTUnit() {}

    public ASTType typeinfer(EnvSet e) {
        return new ASTTUnit();
    }

    @Override
    public String toString() {
        return "()";
    }
}
