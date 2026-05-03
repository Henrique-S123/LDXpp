package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTLBool implements ASTNode  {
    boolean b;

    public ASTLBool(boolean b0) {
        b = b0;
    }

    public boolean getVal() {
      return this.b;
    }

    public IValue eval(Environment<IValue> e) {
        return new VBool(b, true);                
    }

    public ASTType typecheck(EnvSet e) {
		return new ASTTLBool();
	}

    public ASTNode normalize(Environment<ASTType> sigma, Environment<ASTNode> e) {
        return this;
    }

    public ASTType typecheck(EnvSet e, ASTType t) {
        return typecheck(e);
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTLBool olb && olb.getVal() == b;
    }

    @Override
    public String toString() {
        return String.valueOf(b) + "l";
    }
}
