package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTBool implements ASTNode  {
    boolean b;

    public ASTBool(boolean b0) {
        b = b0;
    }

    public boolean getVal() {
      return this.b;
    }

    public IValue eval(Environment<IValue> e) {
        return new VBool(b, false);                
    }

    public ASTType typecheck(EnvSet e) {
		return new ASTTBool();
	}

    public ASTType typecheck(EnvSet e, ASTType t) {
        return typecheck(e);
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTBool && ((ASTBool) o).getVal() == b;
    }

    @Override
    public String toString() {
        return String.valueOf(b);
    }
}
