package proj.types;

import proj.env.*;
import proj.ast.ASTNode;

public class ASTType  {
    public boolean isSubtypeOf(ASTType o, EnvSet e, AlphaEnv alpha) {
        return false;
    }

    public ASTType inst(String instId, ASTNode n) {
        return this;
    }
}
