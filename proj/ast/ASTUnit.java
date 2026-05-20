package proj.ast;

import proj.types.*;
import proj.env.*;

public class ASTUnit extends ASTNode  {

    public ASTUnit() {}

    public boolean defequals(Env<ASTType> sl, ASTNode o, Env<ASTType> sr, AlphaEnv alpha) {
        return o instanceof ASTUnit;
    }

    @Override
    public String toString() {
        return "()";
    }
}
