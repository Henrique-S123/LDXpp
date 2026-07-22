package proj.commands;

import proj.ast.ASTNode;
import proj.env.EnvSet;
import proj.errors.*;

public class TypeCommand implements Command {
    private ASTNode program;

    public TypeCommand(ASTNode p) {
        program = p;
    }

    public void executeCommand() throws TypeCheckError, InterpreterError {
		System.out.println(program.typecheck(new EnvSet(), null));
    }
}
