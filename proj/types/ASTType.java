package proj.types;

import proj.ast.ASTNode;
import proj.env.*;

public interface ASTType  {
    boolean isSubtypeOf(ASTType o, EnvSet e);

    boolean defequals(ASTType o, Environment<ASTType> sigma, Environment<ASTNode> alphaL, Environment<ASTNode> alphaR);
}
