package proj.types;

import proj.env.*;
import proj.ast.ASTNode;

public interface ASTType  {
    boolean isSubtypeOf(ASTType o, EnvSet e, AlphaEnv alpha);

    boolean defequals(Env<ASTType> sl, ASTType o, Env<ASTType> sr, AlphaEnv alpha);

    ASTType inst(String instId, ASTNode n);
}
