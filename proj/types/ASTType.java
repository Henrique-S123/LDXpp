package proj.types;

import proj.env.*;
import proj.errors.*;

public interface ASTType  {
    String toStr();

    boolean isSubtypeOf(ASTType o, EnvSet e) throws InterpreterError;

    boolean defequals(ASTType o, Environment<ASTType> sigma);
}


