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
            VClos vf = ((VClos) vfunc);
            IValue varg = arg.eval(e);
            if (varg instanceof VUnit)
                return vf.getBody().eval(vf.getEnv());
            Environment<IValue> env = vf.getEnv().beginScope();
            env.assoc(vf.getId(), varg);
            return vf.getBody().eval(env);
        } else {
            throw new InterpreterError("func app: closure expected, found " + vfunc);
        }          
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, InterpreterError {
        ASTType tf = func.typecheck(e);
        tf = e.unfold(tf);
        ASTType dom, codom;
        if (tf instanceof ASTTArrow) {
            dom = ((ASTTArrow) tf).getDom();
            codom = ((ASTTArrow) tf).getCodom();
        } else if (tf instanceof ASTTLollipop) {
            dom = ((ASTTLollipop) tf).getDom();
            codom = ((ASTTLollipop) tf).getCodom();
        } else {
            throw new TypeCheckError("illegal type for func app: " + tf.toStr());
        }
        if (dom instanceof ASTTPair && arg instanceof ASTPair) {
            if (((ASTPair) arg).check(e, ((ASTTPair) dom))) {
                return codom;
            } else {
                throw new TypeCheckError("func app: pair " + arg.toString() + " does not match expected type " + tf.toStr());
            }
        } else {
            ASTType ta = arg.typecheck(e);
            if (ta instanceof ASTTUnit || ta.isSubtypeOf(dom, e)) {
                return codom;
            } else {
                throw new TypeCheckError("func app: argument type (" + ta.toStr() + ") is not subtype of the function parameter (" + dom.toStr() + ")");
            }
        }
	}

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }
}
