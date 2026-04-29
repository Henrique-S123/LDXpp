package proj.types;

import proj.env.*;

public interface ASTType  {
    boolean isSubtypeOf(ASTType o, EnvSet e);

    boolean defequals(ASTType o, Environment<ASTType> sigma);
}


