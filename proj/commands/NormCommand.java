package proj.commands;

import proj.ast.ASTNode;
import proj.errors.*;

public class NormCommand implements Command {
    private ASTNode program;

    public NormCommand(ASTNode p) {
        program = p;
    }

    public void executeCommand() throws TypeCheckError, InterpreterError {
        System.out.println(program.weaknorm());
    }
}
