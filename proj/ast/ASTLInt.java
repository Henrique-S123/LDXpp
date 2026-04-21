package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTLInt implements ASTNode  {
    int v;

    public ASTLInt(int v0) {
        v = v0;
    }

    public int getVal() {
        return v;
    }

    public IValue eval(Environment<IValue> e) {
        return new VInt(v, true);
    }

    public ASTType typecheck(EnvSet e) {;
		return new ASTTLInt();
	}

    public ASTType typecheck(EnvSet e, ASTType t) {
        return typecheck(e);
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTLInt && ((ASTLInt) o).getVal() == v;
    }

    @Override
    public String toString() {
        return String.valueOf(v) + "l";
    }
}
