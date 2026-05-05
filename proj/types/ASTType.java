package proj.types;

import proj.ast.ASTNode;
import proj.env.*;

public interface ASTType  {
    boolean isSubtypeOf(ASTType o, EnvSet e);

    boolean defequals(ASTType o, Env<ASTType> sigma, Env<ASTNode> alphaL, Env<ASTNode> alphaR);
}
