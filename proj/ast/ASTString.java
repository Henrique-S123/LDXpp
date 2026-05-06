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
      return s;
    }

    public IValue eval(Env<IValue> e) {
        return new VString(s);                
    }

    public ASTType typecheck(EnvSet e) {
		return new ASTTString();
	}

    public ASTType typecheck(EnvSet e, ASTType t) {
        return typecheck(e);
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        return this;
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTString ostr && s.equals(ostr.getVal());
    }

    @Override
    public String toString() {
        return s;
    }
}
