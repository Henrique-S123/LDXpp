package proj.src.commands;

import proj.src.ast.ASTNode;
import proj.src.types.ASTType;
import proj.src.defeq.*;
import proj.src.env.*;

public class EqCommand implements Command {
    private ASTNode left, right;

    public EqCommand(ASTNode l, ASTNode r) {
        left = l;
        right = r;
    }

    public void executeCommand() {
        ASTNode ln = left.weaknorm();
        ASTNode rn = right.weaknorm();
        DefEq eq = new DefEq(new Env<ASTType>());
        System.out.println(eq.termdefeq(ln, rn, new Env<ASTType>(), new AlphaEnv()) ? "TRUE" : "FALSE");
    }
}
