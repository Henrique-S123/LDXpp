package proj.src.commands;

import proj.src.ast.ASTNode;
import proj.src.env.Env;
import proj.src.values.IValue;
import proj.src.errors.*;

public class EvalCommand implements Command {
    private ASTNode program;

    public EvalCommand(ASTNode p) {
        program = p;
    }

    public void executeCommand() throws InterpreterError {
		System.out.println(program.eval(new Env<IValue>()));
    }
}
