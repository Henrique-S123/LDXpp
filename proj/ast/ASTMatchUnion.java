package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.*;

public class ASTMatchUnion implements ASTNode {

	ASTNode test;
	List<String> labels, ids;
	List<ASTNode> exprs;

	public ASTMatchUnion(ASTNode t, List<String> l, List<String> i, List<ASTNode> e) {
		test = t;
		labels = l;
		ids = i;
		exprs = e;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		IValue vt = test.eval(e);
		if (vt instanceof VUnion) {
			String testlabel = ((VUnion) vt).getLabel();
			int index = labels.indexOf(testlabel);
			if (index == -1) {
				throw new InterpreterError("match: missing case for label " + testlabel);
			} else {
				Environment<IValue> en = e.beginScope();
				en.assoc(ids.get(index), ((VUnion) vt).getValue());
				return exprs.get(index).eval(en);
			}
		} else {
			throw new InterpreterError("match: union expected, found " + vt.toStr());
		}
    }

	public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
		ASTType tt = test.typecheck(e);
		ASTType rettype = null;
		ArrayList<String> matchUsedLinears = null;
		tt = e.unfold(tt);
		if (tt instanceof ASTTUnion) {
			for (Map.Entry<String, ASTType> entry : ((ASTTUnion) tt).getList().getMap().entrySet()) {
				int index = labels.indexOf(entry.getKey());
				if (index == -1)
					throw new TypeCheckError("match missing label " + entry.getKey());

				Environment<ASTType> e2 = e.copy(true);
				e2.assoc(ids.get(index), e.unfold(entry.getValue()));
				ASTType tcase = exprs.get(index).typecheck(e2);

				// list of linear values every case must use
				if (matchUsedLinears == null) {
					matchUsedLinears = new ArrayList<String>(e2.getUsedLinears());
					matchUsedLinears.remove(ids.get(index));
				}
				ArrayList<String> caseUsedLineares = new ArrayList<String>(e2.getUsedLinears());
				if (!caseUsedLineares.containsAll(matchUsedLinears))
					throw new TypeCheckError("all match cases must use the same linear values");
				if ((entry.getValue() instanceof ASTLinType) && !caseUsedLineares.contains(ids.get(index)))
					throw new TypeCheckError("linear value " + ids.get(index) + " must be used");
				if ((tcase.isSubtypeOf(rettype, e2) && rettype.isSubtypeOf(tcase, e2)) || rettype == null) {
					rettype = tcase;
				} else {
					throw new TypeCheckError("different types for match cases: " + tcase.toStr() + " and " + rettype.toStr());
				}
			}
			for (String id : matchUsedLinears)
				e.find(id);
		} else {
			throw new TypeCheckError("illegal type to match test: " + tt.toStr());
		}
		return rettype;
	}
}
