package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.defeq.*;
import proj.src.env.*;
import proj.src.env.PureEnvSet.PENV;
import proj.src.errors.*;

import java.util.*;

public class ASTNever extends ASTNode  {
    private String label;
    private ASTNode test;

    public ASTNever() {}

    public void setFields(String l, ASTNode t) {
        label = l; test = t;
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
        Set<ASTUnion> s = new HashSet<ASTUnion>();
        pe.closeEnvScope(PENV.SIGMA);
        Env<ASTType> curr = pe.getSigma();
        while (curr != null) {
            for (Binder<ASTType> b : curr.getBindings().values()) {
                if (b.getVal() instanceof ASTTEq teq && teq.getTerm2() instanceof ASTUnion un
                    && DefEq.termdefeq(test, teq.getTerm1(), pe, new AlphaEnv()))
                        s.add(un);
                else if (b.getVal() instanceof ASTTEq teq && teq.getTerm1() instanceof ASTUnion un
                    && DefEq.termdefeq(test, teq.getTerm2(), pe, new AlphaEnv()))
                        s.add(un);
            }
            curr = curr.endScope();
        }
        for (ASTUnion u : s) if (!u.getLabel().equals(label)) return true;
        return false;
    }

    public boolean structEq(ASTNode o) {
        return o instanceof ASTNever;
    }

    @Override
    public String toString() {
        return "never";
    }
}
