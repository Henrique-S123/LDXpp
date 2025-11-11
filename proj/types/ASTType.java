package proj.types;

import proj.env.*;
import proj.errors.*;

public interface ASTType  {
    String toStr();

    boolean isSubtypeOf(ASTType o, Environment<ASTType> e) throws InterpreterError;
}


