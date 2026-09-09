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
        DefEq eq = new DefEq(new Env<ASTType>());
        System.out.println(eq.typedefeq(left, right, new PureEnvSet(), new AlphaEnv()) ? "TRUE" : "FALSE");
    }
}
