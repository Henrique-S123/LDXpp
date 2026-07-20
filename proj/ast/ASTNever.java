package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.defeq.*;
import proj.env.*;
import proj.errors.*;

import java.util.*;

public class ASTNever extends ASTNode  {
    private Env<ASTType> searchEnv;
    private String label;
    private ASTNode test;

    public ASTNever() {}

    public IValue eval(Env<IValue> e) throws InterpreterError {
        throw new InterpreterError(ErrorMessages.unreachableCode());
    }

    public void setFields(Env<ASTType> env, String l, ASTNode t) {
        searchEnv = env;
        label = l;
        test = t;
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        if (isInconsistent(e.getPhi(), e.getAlpha())) return target;
        throw new TypeCheckError(ErrorMessages.contextNotInconsistent());
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
        if (isInconsistent(phi, alpha)) return target;
        throw new TypeCheckError(ErrorMessages.contextNotInconsistent());
    }

    public boolean isInconsistent(Env<ASTType> phi, AlphaEnv alpha) throws TypeCheckError {
        Set<ASTNode> s = new HashSet<ASTNode>();
        Env<ASTType> curr = searchEnv;
        while (curr != null) {
            for (Binder<ASTType> b : curr.getBindings().values()) {
                DefEq eq = new DefEq(searchEnv);
                if (b.getVal() instanceof ASTTEq teq && eq.termdefeq(test, teq.getTerm1(), searchEnv, phi, alpha))
                    s.add(teq.getTerm2());
                else if (b.getVal() instanceof ASTTEq teq && eq.termdefeq(test, teq.getTerm2(), searchEnv, phi, alpha))
                    s.add(teq.getTerm1());
            }
            curr = curr.endScope();
        }
        for (ASTNode eq : s) {
            if (eq instanceof ASTUnion u && !u.getLabel().equals(label)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "never";
    }
}
