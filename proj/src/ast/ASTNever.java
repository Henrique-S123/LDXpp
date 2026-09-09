package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.defeq.*;
import proj.src.env.*;
import proj.src.errors.*;

import java.util.*;

public class ASTNever extends ASTNode  {
    private Env<ASTType> searchEnv;
    private String label;
    private ASTNode test;

    public ASTNever() {}

    public void setFields(Env<ASTType> env, String l, ASTNode t) {
        searchEnv = env; label = l; test = t;
    }

    public IValue eval(Env<IValue> e) throws InterpreterError {
        throw new InterpreterError(ErrorMessages.unreachableCode());
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        if (isInconsistent(new PureEnvSet(e))) return target;
        throw new TypeCheckError(ErrorMessages.contextNotInconsistent());
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        if (isInconsistent(pe)) return target;
        throw new TypeCheckError(ErrorMessages.contextNotInconsistent());
    }

    public boolean isInconsistent(PureEnvSet pe) throws TypeCheckError {
        Set<ASTNode> s = new HashSet<ASTNode>();
        Env<ASTType> curr = searchEnv;
        pe.setSigma(searchEnv);
        while (curr != null) {
            for (Binder<ASTType> b : curr.getBindings().values()) {
                if (b.getVal() instanceof ASTTEq teq && DefEq.termdefeq(test, teq.getTerm1(), pe, new AlphaEnv()))
                    s.add(teq.getTerm2());
                else if (b.getVal() instanceof ASTTEq teq && DefEq.termdefeq(test, teq.getTerm2(), pe, new AlphaEnv()))
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
