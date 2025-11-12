package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTApp implements ASTNode  {
    ASTNode func, arg;

    public ASTApp(ASTNode f, ASTNode a) {
        func = f;
        arg = a;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        IValue vfunc = func.eval(e);
        if (vfunc instanceof VClos) {
            IValue varg = arg.eval(e);
            if (varg instanceof VUnit)
                return ((VClos) vfunc).getBody().eval(((VClos) vfunc).getEnv());
            Environment<IValue> env = (((VClos) vfunc)).getEnv().beginScope();
            env.assoc(((VClos) vfunc).getId(), varg);
            return ((VClos) vfunc).getBody().eval(env);
        } else {
            throw new InterpreterError("func app: closure expected, found " + vfunc);
        }          
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
        ASTType tf = func.typecheck(e);
        tf = e.unfold(tf);
        if (tf instanceof ASTTArrow) {
            ASTType ta = arg.typecheck(e);
            if (ta instanceof ASTTUnit) {
                return ((ASTTArrow) tf).getCodom();
            } else if (ta.isSubtypeOf(((ASTTArrow) tf).getDom(), e)) {
                return ((ASTTArrow) tf).getCodom();
            } else {
                throw new TypeCheckError("func app: argument type (" + ta.toStr() + ") is not subtype of the function parameter (" + ((ASTTArrow) tf).getDom().toStr() + ")");
            }
        } else if (tf instanceof ASTTLollipop) {
            ASTType ta = arg.typecheck(e);
            if (ta instanceof ASTTUnit) {
                return ((ASTTLollipop) tf).getCodom();
            } else if (ta.isSubtypeOf(((ASTTLollipop) tf).getDom(), e)) {
                return ((ASTTLollipop) tf).getCodom();
            } else {
                throw new TypeCheckError("func app: argument type (" + ta.toStr() + ") is not subtype of the function parameter (" + ((ASTTArrow) tf).getDom().toStr() + ")");
            }
        } else {
            throw new TypeCheckError("illegal type for app func: " + tf.toStr());
        }
	}
}
