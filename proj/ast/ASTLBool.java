package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTLBool implements ASTNode  {
    boolean b;

    public ASTLBool(boolean b0) {
        b = b0;
    }

    public boolean getVal() {
      return this.b;
    }

    public IValue eval(Environment<IValue> e) {
        return new VBool(b, true);                
    }

    public ASTType typecheck(EnvSet e) {
		return new ASTTLBool();
	}

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    public boolean defequals(ASTNode o) {
        return o instanceof ASTLBool && ((ASTLBool) o).getVal() == b;
    }

    @Override
    public String toString() {
        return String.valueOf(b) + "l";
    }
}
