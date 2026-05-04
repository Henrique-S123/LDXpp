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

    public ASTNode getFunc() {
        return func;
    }

    public ASTNode getArg() {
        return arg;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        IValue vfunc = func.eval(e);
        if (vfunc instanceof VClos vf) {
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

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType tf = func.typecheck(e);
        tf = e.unfold(tf);

        ASTType dom, codom;
        if (tf instanceof ASTTArrow fun) { dom = fun.getDom(); codom = fun.getCodom(); }
        else if (tf instanceof ASTTLollipop fun) { dom = fun.getDom(); codom = fun.getCodom(); }
        else throw new TypeCheckError("illegal type for func app: " + tf);

        ASTType ta = arg.typecheck(e, dom);
        if (ta instanceof ASTTUnit || ta.isSubtypeOf(dom, e)) return codom;
        else throw new TypeCheckError("func app: argument type (" + ta + ") is not subtype of the function parameter (" + dom + ")");
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

    public ASTNode normalize(Environment<ASTType> sigma, Environment<ASTNode> e) {
        ASTNode body, fn = func.normalize(sigma, e);
        ASTNode argn = arg.normalize(sigma, e);
        Environment<ASTNode> normEnv;
        Environment<ASTType> normSigma;
        String id;
        if (fn instanceof ASTFunc f) { body = f.getBody(); normEnv = f.getNormEnv(); normSigma = f.getNormSigma(); id = f.getId(); }
        else if (fn instanceof ASTLFunc lf) { body = lf.getBody(); normEnv = lf.getNormEnv(); normSigma = lf.getNormSigma(); id = lf.getId(); }
        else return new ASTApp(fn, argn);

        Environment<ASTNode> env = normEnv.beginScope();
        env.assoc(id, argn);
        return body.normalize(normSigma, env);
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTApp oapp && oapp.getFunc().defequals(func, sigma)
            && oapp.getArg().defequals(arg, sigma);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", func, arg);
	}
}
