package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTLInt implements ASTNode  {
    int v;

    public ASTLInt(int v0) {
        v = v0;
    }

    public int getVal() {
        return v;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VInt(v, true);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError {;
		return new ASTTLInt();
	}

    public ASTNode normalize() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ASTLInt && ((ASTLInt) o).getVal() == v;
    }

    @Override
    public String toString() {
        return String.valueOf(v) + "l";
    }
}
