package proj.src.commands;

import proj.src.ast.ASTNode;
import proj.src.defeq.*;
import proj.src.env.*;

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
