package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTApp extends ASTNode  {
    private final ASTNode func, arg;

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
        } else if (vfunc instanceof VRec vr) {
            IValue varg = arg.eval(e);
            if (varg instanceof VUnit)
                return new ASTApp(vr.getBody(), arg).eval(vr.getEnv());
            Env<IValue> env = vr.getEnv().beginScope();
            env.assoc(vr.getFid(), vr);
            String pid;
            ASTNode body;
            if (vr.getBody() instanceof ASTFunc f) { body = f.getBody(); pid = f.getId(); }
            else if (vr.getBody() instanceof ASTLFunc lf) { body = lf.getBody(); pid = lf.getId(); }
            else throw new InterpreterError(ErrorMessages.wrongValueToUnary("app", vfunc));
            env.assoc(pid, varg);
            return body.eval(env);
        } else {
            throw new InterpreterError(ErrorMessages.wrongValueToUnary("app", vfunc));
        }          
    }
    
    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        this.setSig(e.getSigma());
        ASTType tf = func.typecheck(e, null);
        tf = e.unfold(tf);
        ASTType dom, codom;
        String id;
        if (tf instanceof ASTTArrow fun) { dom = fun.getDom(); codom = fun.getCodom(); id = fun.getId(); }
        else if (tf instanceof ASTTLollipop fun) { dom = fun.getDom(); codom = fun.getCodom(); id = fun.getId(); }
        else throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("app", tf));

        ASTType targ = arg.typecheck(e, dom);
        if (targ instanceof ASTTUnit || targ.isSubtypeOf(dom, e.getSigma(), e.getPhi(), e.getAlpha())) return codom.inst(id, arg);
        else throw new TypeCheckError(ErrorMessages.notSubtypeApp(targ, dom));
	}

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
        this.setSig(sigma);
        ASTType tf = func.puretypecheck(sigma, phi, alpha, null);
        tf = phi.unfold(tf);
        ASTType dom, codom;
        String id;
        if (tf instanceof ASTTArrow fun) { dom = fun.getDom(); codom = fun.getCodom(); id = fun.getId(); }
        else if (tf instanceof ASTTLollipop fun) { dom = fun.getDom(); codom = fun.getCodom(); id = fun.getId(); }
        else throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("app", tf));

        ASTType targ = arg.puretypecheck(sigma, phi, alpha, dom);
        if (targ instanceof ASTTUnit || targ.isSubtypeOf(dom, sigma, phi, alpha)) return codom.inst(id, arg);
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

    public ASTApp solve(Env<ASTType> sigma) {
        ASTNode nfunc = func.solve(sigma);
        return (nfunc == null) ? null : new ASTApp(nfunc, arg);
    }

    public ASTApp subs(String subsId, ASTNode node) {
        return new ASTApp(func.subs(subsId, node), arg.subs(subsId, node));
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", func, arg);
	}
}
