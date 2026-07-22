package proj.src.commands;

import proj.src.ast.ASTNode;
import proj.src.env.*;
import proj.src.values.IValue;
import proj.src.errors.*;

public class RunCommand implements Command {
    private ASTNode program;

    public RunCommand(ASTNode p) {
        program = p;
    }

    public void executeCommand() throws TypeCheckError, InterpreterError {
        System.out.println("type: " + program.typecheck(new EnvSet(), null) + ", value: " + program.eval(new Env<IValue>()));
    }
}
