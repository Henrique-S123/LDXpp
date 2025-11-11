package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTSelect implements ASTNode {

    ASTNode struct;
	String field;

	public ASTSelect(ASTNode s, String f) {
		struct = s;
		field = f;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue vs = struct.eval(e);
		if (vs instanceof VStruct) {
			IValue val = ((VStruct) vs).getList().get(field);
			if (val != null) {
				return val;
			} else {
				throw new InterpreterError(". operator: struct does not contain field " + field);
			}
		} else {
			throw new InterpreterError(". operator: struct expected, found " + vs);
		}
    }

	public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
		ASTType ts = struct.typecheck(e);
		if (ts instanceof ASTTStruct) {
			ASTType tf = ((ASTTStruct) ts).getList().getMap().get(field);
			tf = e.unfold(tf);
			if (tf != null) {
				return tf;
			} else {
				throw new TypeCheckError("struct does not contain field " + field);
			}
		} else {
			throw new TypeCheckError("illegal type for . operator struct: " + ts.toStr());
		}
	}

}
