package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTPair implements ASTNode {
    ASTNode first, second;

    public ASTPair(ASTNode f, ASTNode s) {
        first = f;
        second = s;
    }

    public ASTNode getFirst() {
        return first;
    }

    public ASTNode getSecond() {
        return second;
    }
    
    public IValue eval(Environment<IValue> e) throws InterpreterError {
        IValue v1 = first.eval(e);
        IValue v2 = second.eval(e);
        return new VPair(v1, v2, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        Environment<ASTType> prevDelta = e.popDelta();
        ASTType t1 = first.typecheck(e);
        ASTType t2 = second.typecheck(e);
        e.setDelta(prevDelta);
        return new ASTTPair(t1, t2, null);
    }

    public boolean check(EnvSet e, ASTTPair t) throws TypeCheckError, EnvironmentError {
        ASTTPair tinfer = ((ASTTPair) this.typecheck(e));
        if (!(tinfer.getFirst().defequals(t.getFirst(), e.getSigma()))) return false;
        e.getSigma().addEq(new ASTTEq(new ASTId(t.getId()), first, tinfer.getFirst()));
        if (!(tinfer.getSecond().defequals(t.getSecond(), e.getSigma()))) return false;
        return true;
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return new ASTPair(first.normalize(sigma), second.normalize(sigma));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ASTPair &&
            first.equals(((ASTPair) o).getFirst()) && second.equals(((ASTPair) o).getSecond());
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", first.toString(), second.toString());
    }
}
