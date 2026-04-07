package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTBool implements ASTNode  {
    boolean b;

    public ASTBool(boolean b0) {
        b = b0;
    }

    public boolean getVal() {
      return this.b;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VBool(b, false);                
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError {
		return new ASTTBool();
	}

    public ASTNode normalize() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ASTBool && ((ASTBool) o).getVal() == b;
    }

    @Override
    public String toString() {
        return String.valueOf(b);
    }
}
