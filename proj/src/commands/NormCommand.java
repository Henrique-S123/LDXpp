package proj.src.commands;

import proj.src.ast.ASTNode;

public class NormCommand implements Command {
    private ASTNode program;

    public NormCommand(ASTNode p) {
        program = p;
    }

    public void executeCommand() {
        System.out.println(program.weaknorm());
    }
}
