package proj.types;

import proj.env.*;

public interface ASTType  {
    boolean isSubtypeOf(ASTType o, EnvSet e);

    boolean defequals(ASTType o, Env<ASTType> sigma, AlphaEnv alpha);
}
