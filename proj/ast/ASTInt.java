package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTInt implements ASTNode  {
    int v;

    public ASTInt(int v0) {
        v = v0;
    }

    public int getVal() {
        return v;
    }

    public IValue eval(Env<IValue> e) {
        return new VInt(v, false);                
    }

    public ASTType typecheck(EnvSet e) {
		return new ASTTInt();
	}

    public ASTType typecheck(EnvSet e, ASTType t) {
        return typecheck(e);
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        return this;
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTInt oi && oi.getVal() == v;
    }

    @Override
    public String toString() {
        return String.valueOf(v);
    }
}
