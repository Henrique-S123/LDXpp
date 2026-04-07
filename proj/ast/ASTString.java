package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTString implements ASTNode  {
    String s;

    public ASTString(String s0) {
        s = s0;
    }
    public String getVal() {
      return this.s;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VString(s);                
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError {
		return new ASTTString();
	}

    public ASTNode normalize() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ASTString && ((ASTString) o).getVal() == s;
    }

    @Override
    public String toString() {
        return s;
    }
}
