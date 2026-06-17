package proj.ast;

import proj.env.*;
import proj.errors.*;
import proj.types.*;

public class ASTUnit extends ASTNode  {

    public ASTUnit() {}

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTUnit();
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        if (target == null || target instanceof ASTTUnit) return new ASTTUnit();
        throw new TypeCheckError(ErrorMessages.typeMismatch("()", target));
    }

    @Override
    public String toString() {
        return "()";
    }
}
