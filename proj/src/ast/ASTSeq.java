package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.env.EnvSet.ENV;
import proj.src.env.PureEnvSet.PENV;
import proj.src.errors.*;

public class ASTSeq extends ASTNode {
    private final ASTNode first, second;

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

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        ASTType tf = first.typecheck(e, null);
        if (tf instanceof ASTTUnit) {
            e.openEnvScope(ENV.SIGMA);
            e.bindToEnv(ENV.SIGMA, e.getFreshId(), new ASTTEq(first, new ASTUnit(), tf));
            ASTType rt = second.typecheck(e, target);
            e.closeEnvScope(ENV.SIGMA);
            return rt;
        }
        else throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("seq", tf));
	}

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        ASTType tf = first.puretypecheck(pe, null);
        if (tf instanceof ASTTUnit) {
            pe.openEnvScope(PENV.SIGMA);
            pe.bindToEnv(PENV.SIGMA, pe.getFreshId(), new ASTTEq(first, new ASTUnit(), tf));
            ASTType rt = second.puretypecheck(pe, target);
            pe.closeEnvScope(PENV.SIGMA);
            return rt;
        }
        else throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("seq", tf));
    }

	public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode nfirst = first.weaknorm(sub);
        if (nfirst instanceof ASTUnit) return second.weaknorm(sub);
        return new ASTSeq(nfirst, second.weaknorm(sub));
    }

    public ASTSeq solve(Env<ASTType> sigma) {
        ASTNode nfirst = first.solve(sigma);
        return nfirst == null ? null : new ASTSeq(nfirst, second);
    }

    public ASTSeq subs(String subsId, ASTNode node) {
        return new ASTSeq(first.subs(subsId, node), second.subs(subsId, node));
    }

    @Override
	public String toString() {
		return String.format("%s; %s", first, second);
	}
}
