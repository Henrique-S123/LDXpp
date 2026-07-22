package proj.commands;

import proj.ast.ASTNode;
import proj.defeq.*;
import proj.env.*;
import proj.errors.*;
import proj.types.ASTType;

public class EqCommand implements Command {
    private ASTNode left, right;

    public EqCommand(ASTNode l, ASTNode r) {
        left = l;
        right = r;
    }

    public void executeCommand() throws TypeCheckError, InterpreterError {
        EnvSet e = new EnvSet();
        ASTType t = left.typecheck(e, null);
        ASTType t2 = right.typecheck(e, null);
        if (!t.isSubtypeOf(t2, e.getSigma(), e.getPhi(), e.getAlpha()) || !t2.isSubtypeOf(t, e.getSigma(), e.getPhi(), e.getAlpha()))
            throw new TypeCheckError(ErrorMessages.termsWithDifferentTypes(left, right, t, t2));

        ASTNode ln = left.weaknorm();
        ASTNode rn = right.weaknorm();
        DefEq eq = new DefEq(e.getSigma());
        if (eq.termdefeq(ln, rn, e.getSigma(), e.getPhi(), e.getAlpha(), new TRefl())) System.out.println("TRUE");
        else throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }
}
