package proj.ast;

import proj.values.*;
import proj.types.ASTType;
import proj.env.*;
import proj.errors.*;

public interface ASTNode {
    public IValue eval(Env<IValue> e) throws InterpreterError;
	
    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError;

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError;

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub);

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha);
}

