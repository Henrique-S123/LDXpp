package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTString implements ASTNode  {
    String s;

    public ASTString(String s0) {
        s = s0;
    }
    public String getVal() {
      return this.s;
    }

    public IValue eval(Environment<IValue> e) {
        return new VString(s);                
    }

    public ASTType typecheck(EnvSet e) {
		return new ASTTString();
	}

    public ASTType typecheck(EnvSet e, ASTType t) {
        return typecheck(e);
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    public boolean defequals(ASTNode o) {
        return o instanceof ASTString && ((ASTString) o).getVal() == s;
    }

    @Override
    public String toString() {
        return s;
    }
}
