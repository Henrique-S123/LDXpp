package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTUnit implements ASTNode  {

    public ASTUnit() {}

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VUnit(false);                
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError {
		return new ASTTUnit();
	}

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ASTUnit;
    }

    @Override
    public String toString() {
        return "()";
    }
}
