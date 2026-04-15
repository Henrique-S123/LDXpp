package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTRefl implements ASTNode  {
    ASTNode term;

    public ASTRefl(ASTNode t) {
        term = t;
    }

    public ASTNode getTerm() {
        return term;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VUnit();                
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        return new ASTTEq(term, term, term.typecheck(e));
	}

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ASTRefl && ((ASTRefl) o).getTerm().equals(term);
    }

    @Override
    public String toString() {
        return String.format("refl(%s)", term.toString());
    }
}
