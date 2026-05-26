package proj.defeq;

import proj.ast.*;
import proj.env.*;
import proj.types.*;

import java.util.*;

public final class DefEq {
    public static final boolean termdefeq(ASTNode l, Env<ASTType> sl, ASTNode r, Env<ASTType> sr) {
        return termdefeq(l, sl, r, sr, new AlphaEnv());
    }

    public static final boolean termdefeq(ASTNode l, Env<ASTType> sl, ASTNode r, Env<ASTType> sr, AlphaEnv alpha) {
        if (l instanceof ASTInt ln && r instanceof ASTInt rn) return ln.getVal() == rn.getVal();
        if (l instanceof ASTLInt ln && r instanceof ASTLInt rn) return ln.getVal() == rn.getVal();
        if (l instanceof ASTBool ln && r instanceof ASTBool rn) return ln.getVal() == rn.getVal();
        if (l instanceof ASTLBool ln && r instanceof ASTLBool rn) return ln.getVal() == rn.getVal();
        if (l instanceof ASTString ln && r instanceof ASTString rn) return ln.getVal().equals(rn.getVal());

        if (l instanceof ASTId ln && r instanceof ASTId rn) {
            String s1 = alpha.getLeft().find(ln.getId());
            String s2 = alpha.getRight().find(rn.getId());
            if (s1 != null && s2 != null) return s1.equals(s2);
            if (ln.getId().equals(rn.getId())
                && sl.retrieveScope(ln.getId()) == sr.retrieveScope(rn.getId()))
                    return true;
        }

        if (l instanceof ASTLet ln && r instanceof ASTLet rn)
            return ln.getDeclType().defequals(sl, rn.getDeclType(), sr, alpha)
                && termdefeq(ln.getExpr(), sl, rn.getExpr(), sr, alpha)
                && termdefeq(ln.getBody(), sl, rn.getBody(), sr, alpha.extend(ln.getId(), rn.getId()));
        
        if (l instanceof ASTArithOp ln && r instanceof ASTArithOp rn && ln.getOp().equals(rn.getOp()))
            return termdefeq(ln.getLhs(), sl, rn.getLhs(), sr, alpha)
                && termdefeq(ln.getRhs(), sl, rn.getRhs(), sr, alpha);
        if (l instanceof ASTCmpOp ln && r instanceof ASTCmpOp rn && ln.getOp().equals(rn.getOp()))
            return termdefeq(ln.getLhs(), sl, rn.getLhs(), sr, alpha)
                && termdefeq(ln.getRhs(), sl, rn.getRhs(), sr, alpha);
        if (l instanceof ASTLogicOp ln && r instanceof ASTLogicOp rn && ln.getOp().equals(rn.getOp()))
            return termdefeq(ln.getLhs(), sl, rn.getLhs(), sr, alpha)
                && termdefeq(ln.getRhs(), sl, rn.getRhs(), sr, alpha);
        
        if (l instanceof ASTIf ln && r instanceof ASTIf rn)
            return termdefeq(ln.getTest(), sl, rn.getTest(), sr, alpha)
                && termdefeq(ln.getConseq(), sl, rn.getConseq(), sr, alpha)
                && termdefeq(ln.getAlt(), sl, rn.getAlt(), sr, alpha);
        
        if (l instanceof ASTFunc ln && r instanceof ASTFunc rn)
            return ln.getArgtype().defequals(ln.getSig(), rn.getArgtype(), rn.getSig(), alpha)
                && termdefeq(ln.getBody().weaknorm(), ln.getSig(), rn.getBody().weaknorm(), rn.getSig(), alpha.extend(ln.getId(), rn.getId()));
        if (l instanceof ASTLFunc ln && r instanceof ASTLFunc rn)
            return ln.getArgtype().defequals(ln.getSig(), rn.getArgtype(), rn.getSig(), alpha)
                && termdefeq(ln.getBody().weaknorm(), ln.getSig(), rn.getBody().weaknorm(), rn.getSig(), alpha.extend(ln.getId(), rn.getId()));
        if (l instanceof ASTApp ln && r instanceof ASTApp rn)
            return termdefeq(ln.getFunc(), sl, rn.getFunc(), sr, alpha)
                && termdefeq(ln.getArg(), sl, rn.getArg(), sr, alpha);
        
        if (l instanceof ASTPair ln && r instanceof ASTPair rn)
            return termdefeq(ln.getFirst(), ln.getSig(), rn.getFirst(), rn.getSig(), alpha)
                && termdefeq(ln.getSecond(), ln.getSig(), rn.getSecond(), rn.getSig(), alpha);
        if (l instanceof ASTChoice ln && r instanceof ASTChoice rn && ln.getChoice() == rn.getChoice())
            return termdefeq(ln.getPair(), sl, rn.getPair(), sr, alpha);
        if (l instanceof ASTTensor ln && r instanceof ASTTensor rn)
            return termdefeq(ln.getFirst(), ln.getSig(), rn.getFirst(), rn.getSig(), alpha)
                && termdefeq(ln.getSecond(), ln.getSig(), rn.getSecond(), rn.getSig(), alpha);
        if (l instanceof ASTSplit ln && r instanceof ASTSplit rn)
            return termdefeq(ln.getPair(), sl, rn.getPair(), sr, alpha)
                && termdefeq(ln.getBody(), sl, rn.getBody(), sr, alpha.extend(ln.getId1(), rn.getId1()).extend(ln.getId2(), rn.getId2()));
        
        if (l instanceof ASTUnion ln && r instanceof ASTUnion rn && ln.getLabel().equals(rn.getLabel()))
            return termdefeq(ln.getExpr(), sl, rn.getExpr(), sr, alpha);
        if (l instanceof ASTLUnion ln && r instanceof ASTLUnion rn && ln.getLabel().equals(rn.getLabel()))
            return termdefeq(ln.getExpr(), sl, rn.getExpr(), sr, alpha);
        if (l instanceof ASTMatch ln && r instanceof ASTMatch rn) {
            if (termdefeq(ln.getTest(), sl, rn.getTest(), sr, alpha)) return false;
            Map<String, MatchCase> own = ln.getCases();
            Map<String, MatchCase> other = ln.getCases();
			if (own.size() != other.size()) return false;
            for (String label : ln.getCases().keySet()) {
                MatchCase ownCase = own.get(label);
				MatchCase otherCase = other.get(label);
				if (otherCase == null ||
                    !termdefeq(ownCase.getExp(), sl, otherCase.getExp(), sr, alpha.extend(ownCase.getId(), otherCase.getId()))) return false;
            }
            return true;
        }

        if (l instanceof ASTUnit && r instanceof ASTUnit) return true;
        if (l instanceof ASTSeq ln && r instanceof ASTSeq rn)
            return termdefeq(ln.getFirst(), sl, rn.getSecond(), sr, alpha)
                && termdefeq(ln.getSecond(), sl, rn.getSecond(), sr, alpha);
        if (l instanceof ASTPrint ln && r instanceof ASTPrint rn && ln.getNewline() == rn.getNewline())
            return termdefeq(ln.getExp(), sl, rn.getExp(), sr, alpha);

        if (l instanceof ASTRefl && r instanceof ASTRefl) return true;
        if (l instanceof ASTLeteq ln && r instanceof ASTLeteq rn)
            return termdefeq(ln.getExpr(), sl, rn.getExpr(), sr, alpha)
                && termdefeq(ln.getBody(), sl, rn.getBody(), sr, alpha.extend(ln.getId(), rn.getId()));
        
        if (l instanceof ASTTypeDef ln && r instanceof ASTTypeDef rn)
            return ln.getLtd().equals(rn.getLtd())
                && termdefeq(ln.getBody(), sl, rn.getBody(), sr, alpha);
        
        TermClosure s = l.solve(sl);
        if (s != null) return termdefeq(s.term().weaknorm(), s.env(), r, sr, alpha);
        s = r.solve(sr);
        if (s != null) return termdefeq(l, sl, s.term().weaknorm(), s.env(), alpha);
        return false;
    }
}
