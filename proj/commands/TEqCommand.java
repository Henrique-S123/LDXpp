package proj.commands;

import proj.defeq.*;
import proj.env.*;
import proj.errors.*;
import proj.types.ASTType;

public class TEqCommand implements Command {
    private ASTType left, right;

    public TEqCommand(ASTType l, ASTType r) {
        left = l;
        right = r;
    }

    public void executeCommand() throws TypeCheckError, InterpreterError {
        EnvSet e = new EnvSet();
        DefEq eq = new DefEq(e.getSigma());
        if (eq.typedefeq(left, right, e.getSigma(), e.getPhi())) System.out.println("TRUE");
        else throw new TypeCheckError(ErrorMessages.typesNotDefeq(left, right));
    }
}
