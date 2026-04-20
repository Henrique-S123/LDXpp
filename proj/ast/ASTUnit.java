package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTUnit implements ASTNode  {

    public ASTUnit() {}

    public IValue eval(Environment<IValue> e) {
        return new VUnit();                
    }

    public ASTType typecheck(EnvSet e) {
		return new ASTTUnit();
	}

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    public boolean defequals(ASTNode o) {
        return o instanceof ASTUnit;
    }

    @Override
    public String toString() {
        return "()";
    }
}
