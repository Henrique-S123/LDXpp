package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTOp extends ASTNode {
    private final ASTNode lhs, rhs;
	private final String op;

	public ASTOp(ASTNode l, ASTNode r, String o) {
		lhs = l; rhs = r; op = o;
    }

	public ASTNode getLhs() { return lhs; }

	public ASTNode getRhs() { return rhs; }

	public String getOp() { return op; }

    public IValue eval(Env<IValue> e) throws InterpreterError {
		IValue v1 = lhs.eval(e);
		IValue v2 = rhs.eval(e);
		if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("-u")) return evalArithOp(e, v1, v2);
		if (op.equals("==") || op.equals("!=") || op.equals(">") || op.equals(">=") || op.equals("<") || op.equals("<=")) return evalCmpOp(e, v1, v2);
		if (op.equals("&&") || op.equals("||") || op.equals("~")) return evalLogicOp(e, v1, v2);
		throw new InterpreterError(ErrorMessages.unexpectedOperation(op));
    }

	private IValue evalArithOp(Env<IValue> e, IValue v1, IValue v2) throws InterpreterError {
		if (v1 instanceof VInt vi1 && v2 instanceof VInt vi2) {
			int i1 = vi1.getval();
			int i2 = vi2.getval();
			int res = switch (op) {
				case "+" -> i1 + i2;
				case "-" -> i1 - i2;
				case "*" -> i1 * i2;
				case "/" -> i1 / i2;
				case "-u" -> -i2;
				default -> throw new InterpreterError(ErrorMessages.unexpectedOperation(op));
			};
			boolean lin = (((VInt) v1).islin() || ((VInt) v2).islin());
			return new VInt(res, lin);
		} else if ((v1 instanceof VString || v1 instanceof VInt) && (v2 instanceof VInt || v2 instanceof VString) && op == "+") {
			String s1 = v1 instanceof VString ? ((VString) v1).getval() : v1.toString();
			String s2 = v2 instanceof VString ? ((VString) v2).getval() : v2.toString();
			return new VString(s1 + s2);
		} else {
			if (op == "-u") throw new InterpreterError(ErrorMessages.wrongValueToUnary("unary -", v2));
			else throw new InterpreterError(ErrorMessages.wrongValueToBinary(op, v1, v2));
		}
	}

	private IValue evalCmpOp(Env<IValue> e, IValue v1, IValue v2) throws InterpreterError {
		if (v1 instanceof VInt vi1 && v2 instanceof VInt vi2) {
			int i1 = vi1.getval();
			int i2 = vi2.getval();
			boolean res = switch (op) {
				case "==" -> i1 == i2;
				case "!=" -> i1 != i2;
				case ">" -> i1 > i2;
				case ">=" -> i1 >= i2;
				case "<" -> i1 < i2;
				case "<=" -> i1 <= i2;
				default -> throw new InterpreterError(ErrorMessages.unexpectedOperation(op));
			};
			boolean lin = (((VInt) v1).islin() || ((VInt) v2).islin());
			return new VBool(res, lin);
		} else throw new InterpreterError(ErrorMessages.wrongValueToBinary(op, v1, v2));
	}

	private IValue evalLogicOp(Env<IValue> e, IValue v1, IValue v2) throws InterpreterError {
		if (v1 instanceof VBool vb1 && v2 instanceof VBool vb2) {
			boolean i1 = vb1.getval();
			boolean i2 = vb2.getval();
			boolean res = switch (op) {
				case "&&" -> i1 && i2;
				case "||" -> i1 || i2;
				case "~" -> !i2;
				default -> throw new InterpreterError(ErrorMessages.unexpectedOperation(op));
			};
			boolean lin = (((VBool) v1).islin() || ((VBool) v2).islin());
			return new VBool(res, lin);
		} else {
			if (op == "~") throw new InterpreterError(ErrorMessages.wrongValueToUnary("~", v2));
			else throw new InterpreterError(ErrorMessages.wrongValueToBinary(op, v1, v2));
		}
	}

	public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		ASTType tl = lhs.typecheck(e, null);
		ASTType tr = rhs.typecheck(e, null);
		if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("-u")) return typecheckArithOp(tl, tr);
		if (op.equals("==") || op.equals("!=") || op.equals(">") || op.equals(">=") || op.equals("<") || op.equals("<=")) return typecheckCmpOp(tl, tr);
		if (op.equals("&&") || op.equals("||") || op.equals("~")) return typecheckLogicOp(tl, tr);
		// TODO: throw error instead of null
		return null;
	}

	public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
		ASTType tl = lhs.puretypecheck(sigma, phi, alpha, null);
		ASTType tr = rhs.puretypecheck(sigma, phi, alpha, null);
		if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("-u")) return typecheckArithOp(tl, tr);
		if (op.equals("==") || op.equals("!=") || op.equals(">") || op.equals(">=") || op.equals("<") || op.equals("<=")) return typecheckCmpOp(tl, tr);
		if (op.equals("&&") || op.equals("||") || op.equals("~")) return typecheckLogicOp(tl, tr);
		// TODO: throw error instead of null
		return null;
	}

	private ASTType typecheckArithOp(ASTType tl, ASTType tr) throws TypeCheckError {
		if (tl instanceof ASTTInt && tr instanceof ASTTInt) return new ASTTInt();
		else if ((tl instanceof ASTTInt || tl instanceof ASTTLInt) && (tr instanceof ASTTInt || tr instanceof ASTTLInt)) return new ASTTLInt();
		else if ((tl instanceof ASTTInt || tl instanceof ASTTString) && (tr instanceof ASTTInt || tr instanceof ASTTString) && op == "+") return new ASTTString();
		else if (op == "-u") throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("unary -", tr)); 
		else throw new TypeCheckError(ErrorMessages.illegalTypeToBinary(op, tl, tr));
	}

	private ASTType typecheckCmpOp(ASTType tl, ASTType tr) throws TypeCheckError {
		if (tl instanceof ASTTInt && tr instanceof ASTTInt) return new ASTTBool();
		else if ((tl instanceof ASTTInt || tl instanceof ASTTLInt) && (tr instanceof ASTTInt || tr instanceof ASTTLInt)) return new ASTTLBool();
		else throw new TypeCheckError(ErrorMessages.illegalTypeToBinary(op, tl, tr));
	}

	private ASTType typecheckLogicOp(ASTType tl, ASTType tr) throws TypeCheckError {
		if (tl instanceof ASTTBool && tr instanceof ASTTBool) return new ASTTBool();
		else if ((tl instanceof ASTTBool || tl instanceof ASTTLBool) && (tr instanceof ASTTBool || tr instanceof ASTTLBool)) return new ASTTLBool();
		else if (op == "~") throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("unary ~", tr));
		else throw new TypeCheckError(ErrorMessages.illegalTypeToBinary(op, tl, tr));
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		ASTNode ln = lhs.weaknorm(sub);
		ASTNode rn = rhs.weaknorm(sub);
		if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("-u")) return weaknormArithOp(sub, ln, rn);
		if (op.equals("==") || op.equals("!=") || op.equals(">") || op.equals(">=") || op.equals("<") || op.equals("<=")) return weaknormCmpOp(sub, ln, rn);
		if (op.equals("&&") || op.equals("||") || op.equals("~")) return weaknormLogicOp(sub, ln, rn);
		return new ASTOp(ln, rn, op);
    }

	private ASTNode weaknormArithOp(Env<ASTNode> sub, ASTNode ln, ASTNode rn) {
		if ((ln instanceof ASTInt || ln instanceof ASTLInt) && (rn instanceof ASTInt || rn instanceof ASTLInt)) {
			int i1 = (ln instanceof ASTInt) ? ((ASTInt) ln).getVal() : ((ASTLInt) ln).getVal();
			int i2 = (rn instanceof ASTInt) ? ((ASTInt) rn).getVal() : ((ASTLInt) rn).getVal();
			int res = switch (op) {
				case "+" -> i1 + i2;
				case "-" -> i1 - i2;
				case "*" -> i1 * i2;
				case "/" -> i1 / i2;
				case "-u" -> -i2;
				default -> -1; // unreachable code
			};
			return (ln instanceof ASTInt && rn instanceof ASTInt) ? new ASTInt(res) : new ASTLInt(res);
		}
		else if (op == "+" && (ln instanceof ASTString || rn instanceof ASTString) &&
				(ln instanceof ASTInt || ln instanceof ASTLInt || ln instanceof ASTString) &&
				(rn instanceof ASTInt || rn instanceof ASTLInt || rn instanceof ASTString)) {
			String s1 = "";
			if (ln instanceof ASTInt n) s1 = "" + n.getVal();
			if (ln instanceof ASTLInt n) s1 = "" + n.getVal();
			if (ln instanceof ASTString n) s1 = n.getVal();
			String s2 = "";
			if (rn instanceof ASTInt n) s2 = "" + n.getVal();
			if (rn instanceof ASTLInt n) s2 = "" + n.getVal();
			if (rn instanceof ASTString n) s2 = n.getVal();
			return new ASTString(s1 + s2);
		}
		return new ASTOp(lhs.weaknorm(sub), rhs.weaknorm(sub), op);
	}
	
	private ASTNode weaknormCmpOp(Env<ASTNode> sub, ASTNode ln, ASTNode rn) {
		if ((ln instanceof ASTInt || ln instanceof ASTLInt) && (rn instanceof ASTInt || rn instanceof ASTLInt)) {
			int i1 = (ln instanceof ASTInt) ? ((ASTInt) ln).getVal() : ((ASTLInt) ln).getVal();
			int i2 = (rn instanceof ASTInt) ? ((ASTInt) rn).getVal() : ((ASTLInt) rn).getVal();
			boolean res = switch (op) {
				case "==" -> i1 == i2;
				case "!=" -> i1 != i2;
				case ">" -> i1 > i2;
				case ">=" -> i1 >= i2;
				case "<" -> i1 < i2;
				case "<=" -> i1 <= i2;
				default -> false; // unreachable code
			};
			return (ln instanceof ASTInt && rn instanceof ASTInt) ? new ASTBool(res) : new ASTLBool(res);
		}
		return new ASTOp(lhs.weaknorm(sub), rhs.weaknorm(sub), op);
	}
	
	private ASTNode weaknormLogicOp(Env<ASTNode> sub, ASTNode ln, ASTNode rn) {
		if ((ln instanceof ASTBool || ln instanceof ASTLBool) && (rn instanceof ASTBool || rn instanceof ASTLBool)) {
			boolean i1 = (ln instanceof ASTBool) ? ((ASTBool) ln).getVal() : ((ASTLBool) ln).getVal();
			boolean i2 = (rn instanceof ASTBool) ? ((ASTBool) rn).getVal() : ((ASTLBool) rn).getVal();
			boolean res = switch (op) {
				case "&&" -> i1 && i2;
				case "||" -> i1 || i2;
				case "~" -> !i2;
				default -> false; // unreachable code
			};
			return (ln instanceof ASTBool && rn instanceof ASTBool) ? new ASTBool(res) : new ASTLBool(res);
		}
		return new ASTOp(lhs.weaknorm(sub), rhs.weaknorm(sub), op);
	}
	
	public ASTOp solve(Env<ASTType> sigma) {
		ASTNode nlhs = lhs.solve(sigma);
        if (nlhs != null) return new ASTOp(nlhs, rhs, op);
        ASTNode nrhs = rhs.solve(sigma);
        if (nrhs != null) return new ASTOp(lhs, nrhs, op);
        return null;
    }

	public ASTOp subs(String subsId, ASTNode node) {
        return new ASTOp(lhs.subs(subsId, node), rhs.subs(subsId, node), op);
    }

	@Override
	public String toString() {
		if (op == "-u") return String.format("-%s", rhs);
		if (op == "~") return String.format("~%s", rhs);
		return String.format("%s %s %s", lhs, op, rhs);
	}
}
