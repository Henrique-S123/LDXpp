package proj.commands;

import proj.defeq.*;
import proj.env.*;
import proj.types.ASTType;

public class TEqCommand implements Command {
    private ASTType left, right;

    public TEqCommand(ASTType l, ASTType r) {
        left = l;
        right = r;
    }

    public void executeCommand() {
        EnvSet e = new EnvSet();
        DefEq eq = new DefEq(e.getSigma());
        System.out.println(eq.typedefeq(left, right, e.getSigma(), e.getPhi()) ? "TRUE" : "FALSE");
    }
}
