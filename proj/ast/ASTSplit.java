package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTSplit implements ASTNode {
    ASTNode pair, body;
	String id1, id2;

	public ASTSplit(ASTNode p, String i1, String i2, ASTNode b) {
		pair = p;
		id1 = i1;
		id2 = i2;
		body = b;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue v = pair.eval(e);
		if (v instanceof VPair) {
			Environment<IValue> en = e.beginScope();
			en.assoc(id1, ((VPair) v).getFirst());
			en.assoc(id2, ((VPair) v).getSecond());
			return body.eval(en);
		} else {
			throw new InterpreterError("split: pair expected, found " + v);
		}
    }

	public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
		if (id1.equals(id2)) throw new TypeCheckError("ids for split must be different");
		ASTType tt = pair.typecheck(e);
		tt = e.unfold(tt);
		if (!(tt instanceof ASTTTensor || tt instanceof ASTTPair))
			throw new TypeCheckError("illegal type to split: " + tt.toStr());
		ASTType tf = tt instanceof ASTTPair ? ((ASTTPair) tt).getFirst() : ((ASTTTensor) tt).getFirst();
		ASTType ts = tt instanceof ASTTPair ? ((ASTTPair) tt).getSecond() : ((ASTTTensor) tt).getSecond();
		Environment<ASTType> en = e.beginScope();
		en.assoc(id1, e.unfold(tf));
		en.assoc(id2, e.unfold(ts));
		ASTType rt = body.typecheck(en);
		if (!en.getLinears().isEmpty())
			throw new TypeCheckError("there are unused linear values: " + en.deltaToStr());
		return rt;
	}
}
