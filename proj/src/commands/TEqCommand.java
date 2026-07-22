package proj.src.commands;

import proj.src.defeq.*;
import proj.src.env.*;
import proj.src.types.ASTType;

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
