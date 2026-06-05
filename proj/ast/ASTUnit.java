package proj.ast;

import proj.env.*;
import proj.types.*;

public class ASTUnit extends ASTNode  {

    public ASTUnit() {}

    public ASTType typecheck(EnvSet e) {
        return new ASTTUnit();
    }

    @Override
    public String toString() {
        return "()";
    }
}
