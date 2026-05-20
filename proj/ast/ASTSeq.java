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

    public ASTNode solve(Env<ASTType> sigma) {
        ASTNode nfirst = first.solve(sigma);
        if (nfirst != null) return new ASTSeq(nfirst, second);
        ASTNode nsecond = second.solve(sigma);
        if (nsecond != null) return new ASTSeq(first, nsecond);
        return null;
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTSeq(first.subs(subsId, node), second.subs(subsId, node));
    }

    public boolean defequals(Env<ASTType> sl, ASTNode o, Env<ASTType> sr, AlphaEnv alpha) {
        return o instanceof ASTSeq oseq && first.defequals(sl, oseq.getFirst(), sr, alpha)
            && second.defequals(sl, oseq.getSecond(), sr, alpha);
    }

    @Override
	public String toString() {
		return String.format("%s; %s", first, second);
	}
}
