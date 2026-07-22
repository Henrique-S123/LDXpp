package proj.commands;

import proj.errors.*;

public interface Command {
    public void executeCommand() throws TypeCheckError, InterpreterError;
}
