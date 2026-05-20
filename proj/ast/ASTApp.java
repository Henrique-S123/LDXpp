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
        else throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("app", tf));

        ASTType targ = arg.typecheck(e, dom);
        if (targ instanceof ASTTUnit || targ.isSubtypeOf(dom, e, new AlphaEnv())) return codom.inst(id, arg);
        else throw new TypeCheckError(ErrorMessages.notSubtypeApp(targ, dom));
	}

    public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode body, fn = func.weaknorm(sub);
        ASTNode argn = arg.weaknorm(sub);
        Env<ASTNode> normEnv;
        String id;
        if (fn instanceof ASTFunc f) { body = f.getBody(); normEnv = f.getNormEnv(); id = f.getId(); }
        else if (fn instanceof ASTLFunc lf) { body = lf.getBody(); normEnv = lf.getNormEnv(); id = lf.getId(); }
        else return new ASTApp(fn, argn);

        Env<ASTNode> env = normEnv.beginScope();
        env.assoc(id, argn);
        return body.weaknorm(env);
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

    public boolean defequals(Env<ASTType> sl, ASTNode o, Env<ASTType> sr, AlphaEnv alpha) {
        return o instanceof ASTApp oapp && func.defequals(sl, oapp.getFunc(), sr, alpha)
            && arg.defequals(sl, oapp.getArg(), sr, alpha);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", func, arg);
	}
}
