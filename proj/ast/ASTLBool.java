package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTLBool extends ASTNode  {
    boolean b;

    public ASTLBool(boolean b0) {
        b = b0;
    }

    public boolean getVal() {
      return this.b;
    }

    public IValue eval(Env<IValue> e) {
        return new VBool(b, true);                
    }

    public ASTType typecheck(EnvSet e) {
		return new ASTTLBool();
	}

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTLBool olb && b == olb.getVal();
    }

    @Override
    public String toString() {
        return String.valueOf(b) + "l";
    }
}
