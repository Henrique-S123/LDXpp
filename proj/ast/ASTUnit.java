package proj.ast;

import proj.env.*;
import proj.types.*;

public class ASTUnit extends ASTNode  {

    public ASTUnit() {}

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTUnit();
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) {
        return new ASTTUnit();
    }

    @Override
    public String toString() {
        return "()";
    }
}
