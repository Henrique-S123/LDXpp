package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTString extends ASTNode  {
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

    public boolean defequals(Env<ASTType> sl, ASTNode o, Env<ASTType> sr, AlphaEnv alpha) {
        return o instanceof ASTString ostr && s.equals(ostr.getVal());
    }

    @Override
    public String toString() {
        return s;
    }
}
