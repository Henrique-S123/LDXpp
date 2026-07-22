package proj.commands;

import proj.ast.ASTNode;
import proj.defeq.*;
import proj.env.*;

public class EqCommand implements Command {
    private ASTNode left, right;

    public EqCommand(ASTNode l, ASTNode r) {
        left = l;
        right = r;
    }

    public void executeCommand() {
        EnvSet e = new EnvSet();
        ASTNode ln = left.weaknorm();
        ASTNode rn = right.weaknorm();
        DefEq eq = new DefEq(e.getSigma());
        System.out.println(eq.termdefeq(ln, rn, e.getSigma(), e.getPhi(), e.getAlpha(), new TRefl()) ? "TRUE" : "FALSE");
    }
}
