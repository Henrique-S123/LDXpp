package proj.src.commands;

import proj.src.errors.*;

public interface Command {
    public void executeCommand() throws TypeCheckError, InterpreterError;
}
