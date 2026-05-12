package proj.ast;

import proj.types.*;
import proj.env.*;

public class ASTUnit extends ASTNode  {

    public ASTUnit() {}

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTUnit;
    }

    @Override
    public String toString() {
        return "()";
    }
}
