package proj.ast;

import proj.values.*;
import proj.types.ASTType;
import proj.env.*;
import proj.errors.*;

public interface ASTNode {
    public IValue eval(Environment<IValue> e) throws InterpreterError;
	
    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError;

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError;

    public ASTNode normalize(Environment<ASTType> sigma, Environment<ASTNode> sub);

    public boolean defequals(ASTNode o, Environment<ASTType> sigma);
}

