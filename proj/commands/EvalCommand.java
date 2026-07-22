package proj.commands;

import proj.ast.ASTNode;
import proj.env.Env;
import proj.values.IValue;
import proj.errors.*;

public class EvalCommand implements Command {
    private ASTNode program;

    public EvalCommand(ASTNode p) {
        program = p;
    }

    public void executeCommand() throws TypeCheckError, InterpreterError {
		System.out.println(program.eval(new Env<IValue>()));
    }
}
