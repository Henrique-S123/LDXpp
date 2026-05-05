package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTUnit implements ASTNode  {

    public ASTUnit() {}

    public IValue eval(Env<IValue> e) {
        return new VUnit();                
    }

    public ASTType typecheck(EnvSet e) {
		return new ASTTUnit();
	}

    public ASTType typecheck(EnvSet e, ASTType t) {
        return typecheck(e);
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        return this;
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTUnit;
    }

    @Override
    public String toString() {
        return "()";
    }
}
