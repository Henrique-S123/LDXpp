package proj.src.ast;

import proj.src.env.*;
import proj.src.types.*;

public class ASTUnit extends ASTNode  {
    public ASTUnit() {}

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTUnit();
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) {
        return new ASTTUnit();
    }

    public boolean structEq(ASTNode o) {
        return o instanceof ASTUnit;
    }

    @Override
    public String toString() {
        return "()";
    }
}
