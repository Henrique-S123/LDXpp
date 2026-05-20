package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTLInt extends ASTNode  {
    int v;

    public ASTLInt(int v0) {
        v = v0;
    }

    public int getVal() {
        return v;
    }

    public IValue eval(Env<IValue> e) {
        return new VInt(v, true);
    }

    public ASTType typecheck(EnvSet e) {;
		return new ASTTLInt();
	}

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTLInt oli && v == oli.getVal();
    }

    @Override
    public String toString() {
        return String.valueOf(v) + "l";
    }
}
