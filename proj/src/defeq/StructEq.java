package proj.src.defeq;

import proj.src.ast.*;
import proj.src.env.*;
import proj.src.types.*;
import proj.src.debug.Debug;

import java.util.Set;
import java.util.Map;

public final class StructEq {
    public final static boolean termEqStruct(ASTNode l, ASTNode r, AlphaEnv alpha) {
        if (l instanceof ASTInt ln && r instanceof ASTInt rn) return ln.getVal() == rn.getVal() && ln.isLinear() == rn.isLinear();
        if (l instanceof ASTBool ln && r instanceof ASTBool rn) return ln.getVal() == rn.getVal() && ln.isLinear() == rn.isLinear();
        if (l instanceof ASTString ln && r instanceof ASTString rn) return ln.getVal().equals(rn.getVal());
        
        if (l instanceof ASTId ln && r instanceof ASTId rn) {
            if (alpha.equiv(ln.getId(), rn.getId())) return true;
            String bid1 = ln.getBid();
            String bid2 = rn.getBid();
            Debug.log("LEFT BINDER ID: " + bid1);
            Debug.log("RIGHT BINDER ID: " + bid2);
            if (bid1 != null && bid1.equals(bid2)) return true;
        }
        if (l instanceof ASTLet ln && r instanceof ASTLet rn)
            return typeEqStruct(ln.getDeclType(), rn.getDeclType(), alpha) && termEqStruct(ln.getExpr(), rn.getExpr(), alpha)
                && termEqStruct(ln.getBody(), rn.getBody(), alpha.extend(ln.getId(), rn.getId()));

        if (l instanceof ASTOp ln && r instanceof ASTOp rn && ln.getOp().equals(rn.getOp()))
            return termEqStruct(ln.getLhs(), rn.getLhs(), alpha) && termEqStruct(ln.getRhs(), rn.getRhs(), alpha);
        if (l instanceof ASTIf ln && r instanceof ASTIf rn)
            return (termEqStruct(ln.getTest(), rn.getTest(), alpha) && termEqStruct(ln.getConseq(), rn.getConseq(), alpha)
                && termEqStruct(ln.getAlt(), rn.getAlt(), alpha));

        if (l instanceof ASTFunc ln && r instanceof ASTFunc rn && ln.isLinear() == rn.isLinear())
            return typeEqStruct(ln.getArgtype(), rn.getArgtype(), alpha)
                && termEqStruct(ln.getBody().weaknorm(), rn.getBody().weaknorm(), alpha.extend(ln.getId(), rn.getId()));
        if (l instanceof ASTApp ln && r instanceof ASTApp rn)
            return termEqStruct(ln.getFunc(), rn.getFunc(), alpha) && termEqStruct(ln.getArg(), rn.getArg(), alpha);
        if (l instanceof ASTRec ln && r instanceof ASTRec rn)
            return typeEqStruct(ln.getFunctype(), rn.getFunctype(), alpha) && termEqStruct(ln.getFuncbody(), rn.getFuncbody(), alpha)
                && termEqStruct(ln.getBody(), rn.getBody(), alpha.extend(ln.getFuncid(), rn.getFuncid()));

        if (l instanceof ASTPair ln && r instanceof ASTPair rn && ln.isLinear() == rn.isLinear())
            return termEqStruct(ln.getFirst(), rn.getFirst(), alpha) && termEqStruct(ln.getSecond(), rn.getSecond(), alpha);
        if (l instanceof ASTChoice ln && r instanceof ASTChoice rn && ln.getChoice() == rn.getChoice())
            return termEqStruct(ln.getPair(), rn.getPair(), alpha);
        if (l instanceof ASTSplit ln && r instanceof ASTSplit rn)
            return termEqStruct(ln.getPair(), rn.getPair(), alpha)
                && termEqStruct(ln.getBody(), rn.getBody(), alpha.extend(ln.getId1(), rn.getId1()).extend(ln.getId2(), ln.getId2()));

        if (l instanceof ASTUnion ln && r instanceof ASTUnion rn && ln.getLabel().equals(rn.getLabel()))
            return termEqStruct(ln.getExpr(), rn.getExpr(), alpha);
        if (l instanceof ASTMatch ln && r instanceof ASTMatch rn) {
            if (termEqStruct(ln.getTest(), rn.getTest(), alpha)) {
                Set<String> left = ln.getLabels();
                Set<String> right = rn.getLabels();
                if (left.size() != right.size() || !left.containsAll(right)) return false;
                for (String label : left)
                    if (!termEqStruct(ln.getCaseExp(label), rn.getCaseExp(label), alpha.extend(ln.getCaseId(label), rn.getCaseId(label))))
                            return false;
                return true;
            }
            return false;
        }

        if (l instanceof ASTUnit && r instanceof ASTUnit) return true;
        if (l instanceof ASTSeq ln && r instanceof ASTSeq rn)
            return termEqStruct(ln.getFirst(), rn.getFirst(), alpha) && termEqStruct(ln.getSecond(), rn.getSecond(), alpha);
        if (l instanceof ASTPrint ln && r instanceof ASTPrint rn && ln.getNewline() == rn.getNewline())
            return termEqStruct(ln.getExp(), rn.getExp(), alpha);

        if (l instanceof ASTRefl && r instanceof ASTRefl) return true;
        if (l instanceof ASTLeteq ln && r instanceof ASTLeteq rn)
            return termEqStruct(ln.getExpr(), rn.getExpr(), alpha) && termEqStruct(ln.getBody(), rn.getBody(), alpha.extend(ln.getId(), rn.getId()));
        
        if (l instanceof ASTTypeDef ln && r instanceof ASTTypeDef rn)
            return ln.getLtd().equals(rn.getLtd()) && termEqStruct(ln.getBody(), rn.getBody(), alpha);

        return false;
    }

    public final static boolean typeEqStruct(ASTType l, ASTType r, AlphaEnv alpha) {
        if (l instanceof ASTTInt li && r instanceof ASTTInt ri) return li.isLinear() == ri.isLinear();
        if (l instanceof ASTTBool lb && r instanceof ASTTBool rb) return lb.isLinear() == rb.isLinear();
        if (l instanceof ASTTString && r instanceof ASTTString) return true;
        if (l instanceof ASTTUnit && r instanceof ASTTUnit) return true;
        if (l instanceof ASTTId lt && r instanceof ASTTId rt) return lt.getId().equals(rt.getId());

        if (l instanceof ASTTArrow lt && r instanceof ASTTArrow rt)
            return typeEqStruct(lt.getDom(), rt.getDom(), alpha)
                && typeEqStruct(lt.getCodom(),rt.getCodom(), alpha.extend(lt.getId(), rt.getId()));
        
        if (l instanceof ASTTPair lt && r instanceof ASTTPair rt)
            return typeEqStruct(lt.getFirst(), rt.getFirst(), alpha)
                && typeEqStruct(lt.getSecond(), rt.getSecond(), alpha.extend(lt.getId(), rt.getId()));
        
        if (l instanceof ASTTUnion lt && r instanceof ASTTUnion rt && lt.isLinear() == rt.isLinear()) {
            Map<String, ASTType> left = lt.getMap();
            Map<String, ASTType> right = rt.getMap();
            if (left.size() != right.size()) return false;
            for (String label : left.keySet())
                if (right.get(label) == null || !typeEqStruct(left.get(label), right.get(label), alpha.extend(label, label))) return false;
            return true;
        }

        if (l instanceof ASTTEq lt && r instanceof ASTTEq rt)
            return termEqStruct(lt.getTerm1(), rt.getTerm1(), alpha) && termEqStruct(lt.getTerm2(), rt.getTerm2(), alpha)
                && typeEqStruct(lt.getType(), rt.getType(), alpha);

        return false;
    }
}
