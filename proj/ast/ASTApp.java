package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTApp extends ASTNode  {
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

    public IValue eval(Env<IValue> e) throws InterpreterError {
        IValue vfunc = func.eval(e);
        if (vfunc instanceof VClos vf) {
            IValue varg = arg.eval(e);
            if (varg instanceof VUnit)
                return vf.getBody().eval(vf.getEnv());
            Env<IValue> env = vf.getEnv().beginScope();
            env.assoc(vf.getId(), varg);
            return vf.getBody().eval(env);
        } else {
            throw new InterpreterError(ErrorMessages.wrongValueToUnary("app", vfunc));
        }          
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType tf = func.typecheck(e);
        tf = e.unfold(tf);

        ASTType dom, codom;
        String id;
        if (tf instanceof ASTTArrow fun) { dom = fun.getDom(); codom = fun.getCodom(); id = fun.getId(); }
        else if (tf instanceof ASTTLollipop fun) { dom = fun.getDom(); codom = fun.getCodom(); id = fun.getId(); }
        else throw new TypeCheckError("illegal type for func app: " + tf);

        ASTType ta = arg.typecheck(e, dom);
        if (ta instanceof ASTTUnit || ta.isSubtypeOf(dom, e)) return codom.inst(id, arg);
        else throw new TypeCheckError("func app: argument type (" + ta + ") is not subtype of the function parameter (" + dom + ")");
	}

    public ASTNode weaknorm(Env<ASTType> sigma, Env<ASTNode> sub) {
        ASTNode body, fn = func.weaknorm(sigma, sub);
        ASTNode argn = arg.weaknorm(sigma, sub);
        Env<ASTNode> normEnv;
        Env<ASTType> normSigma;
        String id;
        if (fn instanceof ASTFunc f) { body = f.getBody(); normEnv = f.getNormEnv(); normSigma = f.getNormSigma(); id = f.getId(); }
        else if (fn instanceof ASTLFunc lf) { body = lf.getBody(); normEnv = lf.getNormEnv(); normSigma = lf.getNormSigma(); id = lf.getId(); }
        else return new ASTApp(fn, argn);

        Env<ASTNode> env = normEnv.beginScope();
        env.assoc(id, argn);
        return body.weaknorm(normSigma, env);
    }

    public ASTNode solve(Env<ASTType> sigma) {
        ASTNode nfunc = func.solve(sigma);
        if (nfunc != null) return new ASTApp(nfunc, arg);
        ASTNode narg = arg.solve(sigma);
        if (narg != null) return new ASTApp(func, narg);
        return null;
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTApp(func.subs(subsId, node), arg.subs(subsId, node));
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTApp oapp && oapp.getFunc().defequals(func, sigma, alpha)
            && oapp.getArg().defequals(arg, sigma, alpha);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", func, arg);
	}
}
