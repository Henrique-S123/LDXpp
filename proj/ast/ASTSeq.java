package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTSeq extends ASTNode {
    ASTNode first, second;

    public ASTSeq(ASTNode f, ASTNode s) {
		first = f;
		second = s;
    }

    public ASTNode getFirst() {
        return first;
    }

    public ASTNode getSecond() {
        return second;
    }

    public IValue eval(Env<IValue> e) throws InterpreterError {
		first.eval(e);
		return second.eval(e);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType tf = first.typecheck(e);
        if (tf instanceof ASTTUnit) {
            return second.typecheck(e);
        } else {
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("seq", tf));
        }
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
        return new ASTSeq(first.weaknorm(sub), second.weaknorm(sub));
    }

    public TermClosure solve(Env<ASTType> sigma) {
        TermClosure nfirst = first.solve(sigma);
        return nfirst == null ? null : new TermClosure(new ASTSeq(nfirst.term(), second), sigma);
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTSeq(first.subs(subsId, node), second.subs(subsId, node));
    }

    @Override
	public String toString() {
		return String.format("%s; %s", first, second);
	}
}
