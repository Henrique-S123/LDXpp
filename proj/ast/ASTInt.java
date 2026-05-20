package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTInt extends ASTNode  {
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
    
    public boolean defequals(Env<ASTType> sl, ASTNode o, Env<ASTType> sr, AlphaEnv alpha) {
        return o instanceof ASTInt oi && v == oi.getVal();
    }

    @Override
    public String toString() {
        return String.valueOf(v);
    }
}
