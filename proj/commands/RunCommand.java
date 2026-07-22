package proj.commands;

import proj.ast.ASTNode;
import proj.env.*;
import proj.values.IValue;
import proj.errors.*;

public class RunCommand implements Command {
    private ASTNode program;

    public RunCommand(ASTNode p) {
        program = p;
    }

    public void executeCommand() throws TypeCheckError, InterpreterError {
        System.out.println("type: " + program.typecheck(new EnvSet(), null) + ", value: " + program.eval(new Env<IValue>()));
    }
}
