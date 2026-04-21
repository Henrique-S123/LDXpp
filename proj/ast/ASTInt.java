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

    public IValue eval(Environment<IValue> e) {
        return new VInt(v, false);                
    }

    public ASTType typecheck(EnvSet e) {
		return new ASTTInt();
	}

    public ASTType typecheck(EnvSet e, ASTType t) {
        return typecheck(e);
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    public boolean defequals(ASTNode o) {
        return o instanceof ASTInt && ((ASTInt) o).getVal() == v;
    }

    @Override
    public String toString() {
        return String.valueOf(v);
    }
}
