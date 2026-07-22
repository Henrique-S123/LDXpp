package proj.src.commands;

import proj.src.ast.ASTNode;
import proj.src.env.EnvSet;
import proj.src.errors.*;

public class TypeCommand implements Command {
    private ASTNode program;

    public TypeCommand(ASTNode p) {
        program = p;
    }

    public void executeCommand() throws TypeCheckError {
		System.out.println(program.typecheck(new EnvSet(), null));
    }
}
