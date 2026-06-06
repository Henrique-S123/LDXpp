package proj.defeq;

import proj.ast.*;
import proj.env.*;
import proj.types.*;
import proj.debug.Debug;

import java.util.*;

public final class DefEq {
    public static final boolean termdefeq(ASTNode l, Env<ASTType> sl, ASTNode r, Env<ASTType> sr, Env<ASTType> phi) {
        return termdefeq(l, sl, r, sr, new AlphaEnv(), phi);
    }

    public static final boolean termdefeq(ASTNode l, Env<ASTType> sl, ASTNode r, Env<ASTType> sr, AlphaEnv alpha, Env<ASTType> phi) {
        Debug.log(String.format("left: %s", l));
        Debug.log(String.format("right: %s", r));
        Debug.nl();

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
            return typedefeq(ln.getDeclType(), sl, rn.getDeclType(), sr, alpha, phi, new HashSet<IdPair>())
                && termdefeq(ln.getExpr(), sl, rn.getExpr(), sr, alpha, phi)
                && termdefeq(ln.getBody(), sl, rn.getBody(), sr, alpha.extend(ln.getId(), rn.getId()), phi);
        
        if (l instanceof ASTArithOp ln && r instanceof ASTArithOp rn && ln.getOp().equals(rn.getOp()))
            return termdefeq(ln.getLhs(), sl, rn.getLhs(), sr, alpha, phi)
                && termdefeq(ln.getRhs(), sl, rn.getRhs(), sr, alpha, phi);
        if (l instanceof ASTCmpOp ln && r instanceof ASTCmpOp rn && ln.getOp().equals(rn.getOp()))
            return termdefeq(ln.getLhs(), sl, rn.getLhs(), sr, alpha, phi)
                && termdefeq(ln.getRhs(), sl, rn.getRhs(), sr, alpha, phi);
        if (l instanceof ASTLogicOp ln && r instanceof ASTLogicOp rn && ln.getOp().equals(rn.getOp()))
            return termdefeq(ln.getLhs(), sl, rn.getLhs(), sr, alpha, phi)
                && termdefeq(ln.getRhs(), sl, rn.getRhs(), sr, alpha, phi);
        
        if (l instanceof ASTIf ln && r instanceof ASTIf rn)
            return termdefeq(ln.getTest(), sl, rn.getTest(), sr, alpha, phi)
                && termdefeq(ln.getConseq(), sl, rn.getConseq(), sr, alpha, phi)
                && termdefeq(ln.getAlt(), sl, rn.getAlt(), sr, alpha, phi);
        
        if (l instanceof ASTFunc ln && r instanceof ASTFunc rn)
            return typedefeq(ln.getArgtype(), ln.getSig(), rn.getArgtype(), rn.getSig(), alpha, phi, new HashSet<IdPair>())
                && termdefeq(ln.getBody().weaknorm(), ln.getSig(), rn.getBody().weaknorm(), rn.getSig(), alpha.extend(ln.getId(), rn.getId()), phi);
        if (l instanceof ASTLFunc ln && r instanceof ASTLFunc rn)
            return typedefeq(ln.getArgtype(), ln.getSig(), rn.getArgtype(), rn.getSig(), alpha, phi, new HashSet<IdPair>())
                && termdefeq(ln.getBody().weaknorm(), ln.getSig(), rn.getBody().weaknorm(), rn.getSig(), alpha.extend(ln.getId(), rn.getId()), phi);
        if (l instanceof ASTApp ln && r instanceof ASTApp rn)
            return termdefeq(ln.getFunc(), sl, rn.getFunc(), sr, alpha, phi)
                && termdefeq(ln.getArg(), sl, rn.getArg(), sr, alpha, phi);
        // TODO: add ASTRec case
        
        if (l instanceof ASTPair ln && r instanceof ASTPair rn)
            return termdefeq(ln.getFirst(), ln.getSig(), rn.getFirst(), rn.getSig(), alpha, phi)
                && termdefeq(ln.getSecond(), ln.getSig(), rn.getSecond(), rn.getSig(), alpha, phi);
        if (l instanceof ASTChoice ln && r instanceof ASTChoice rn && ln.getChoice() == rn.getChoice())
            return termdefeq(ln.getPair(), sl, rn.getPair(), sr, alpha, phi);
        if (l instanceof ASTTensor ln && r instanceof ASTTensor rn)
            return termdefeq(ln.getFirst(), ln.getSig(), rn.getFirst(), rn.getSig(), alpha, phi)
                && termdefeq(ln.getSecond(), ln.getSig(), rn.getSecond(), rn.getSig(), alpha, phi);
        if (l instanceof ASTSplit ln && r instanceof ASTSplit rn)
            return termdefeq(ln.getPair(), sl, rn.getPair(), sr, alpha, phi)
                && termdefeq(ln.getBody(), sl, rn.getBody(), sr, alpha.extend(ln.getId1(), rn.getId1()).extend(ln.getId2(), rn.getId2()), phi);
        
        if (l instanceof ASTUnion ln && r instanceof ASTUnion rn && ln.getLabel().equals(rn.getLabel()))
            return termdefeq(ln.getExpr(), sl, rn.getExpr(), sr, alpha, phi);
        if (l instanceof ASTLUnion ln && r instanceof ASTLUnion rn && ln.getLabel().equals(rn.getLabel()))
            return termdefeq(ln.getExpr(), sl, rn.getExpr(), sr, alpha, phi);
        if (l instanceof ASTMatch ln && r instanceof ASTMatch rn) {
            if (termdefeq(ln.getTest(), sl, rn.getTest(), sr, alpha, phi)) return false;
            Map<String, MatchCase> left = ln.getCases();
            Map<String, MatchCase> right = rn.getCases();
			if (left.size() != right.size()) return false;
            for (String label : ln.getCases().keySet()) {
                MatchCase leftCase = left.get(label);
				MatchCase rightCase = right.get(label);
				if (rightCase == null ||
                    !termdefeq(leftCase.getExp(), sl, rightCase.getExp(), sr, alpha.extend(leftCase.getId(), rightCase.getId()), phi)) return false;
            }
            return true;
        }

        if (l instanceof ASTUnit && r instanceof ASTUnit) return true;
        if (l instanceof ASTSeq ln && r instanceof ASTSeq rn)
            return termdefeq(ln.getFirst(), sl, rn.getSecond(), sr, alpha, phi)
                && termdefeq(ln.getSecond(), sl, rn.getSecond(), sr, alpha, phi);
        if (l instanceof ASTPrint ln && r instanceof ASTPrint rn && ln.getNewline() == rn.getNewline())
            return termdefeq(ln.getExp(), sl, rn.getExp(), sr, alpha, phi);

        if (l instanceof ASTRefl && r instanceof ASTRefl) return true;
        if (l instanceof ASTLeteq ln && r instanceof ASTLeteq rn)
            return termdefeq(ln.getExpr(), sl, rn.getExpr(), sr, alpha, phi)
                && termdefeq(ln.getBody(), sl, rn.getBody(), sr, alpha.extend(ln.getId(), rn.getId()), phi);
        
        if (l instanceof ASTTypeDef ln && r instanceof ASTTypeDef rn)
            return ln.getLtd().equals(rn.getLtd())
                && termdefeq(ln.getBody(), sl, rn.getBody(), sr, alpha, phi);
        
        TermClosure s = l.solve(sl);
        if (s != null) return termdefeq(s.term().weaknorm(), s.env(), r, sr, alpha, phi);
        s = r.solve(sr);
        if (s != null) return termdefeq(l, sl, s.term().weaknorm(), s.env(), alpha, phi);
        return false;
    }

    public static final boolean typedefeq(ASTType l, Env<ASTType> sl, ASTType r, Env<ASTType> sr, Env<ASTType> phi) {
        return typedefeq(l, sl, r, sr, new AlphaEnv(), phi, new HashSet<IdPair>());
    }

    public static final boolean typedefeq(ASTType l, Env<ASTType> sl, ASTType r, Env<ASTType> sr, AlphaEnv alpha, Env<ASTType> phi, Set<IdPair> seen) {
        if (l instanceof ASTTInt && r instanceof ASTTInt) return true;
        if (l instanceof ASTTLInt && r instanceof ASTTLInt) return true;
        if (l instanceof ASTTBool && r instanceof ASTTBool) return true;
        if (l instanceof ASTTLBool && r instanceof ASTTLBool) return true;
        if (l instanceof ASTTString && r instanceof ASTTString) return true;
        if (l instanceof ASTTUnit && r instanceof ASTTUnit) return true;

        if (l instanceof ASTTId lt && r instanceof ASTTId rt) {
            if (lt.getId().equals(rt.getId())) return true;
            if (seen.contains(new IdPair(lt.getId(), rt.getId()))) return true;
            seen.add(new IdPair(lt.getId(), rt.getId()));
            ASTType newl = phi.unfold(lt);
            ASTType newr = phi.unfold(rt);
            return typedefeq(newl, newl.getSig(), newr, newr.getSig(), alpha, phi, seen);
        }
        
        if (l instanceof ASTTArrow lt && r instanceof ASTTArrow rt)
            return typedefeq(lt.getDom(), sl, rt.getDom(), sr, alpha, phi, seen)
                && typedefeq(lt.getCodom(), sl, rt.getCodom(), sr, alpha.extend(lt.getId(), rt.getId()), phi, seen);
        if (l instanceof ASTTLollipop lt && r instanceof ASTTLollipop rt)
            return typedefeq(lt.getDom(), sl, rt.getDom(), sr, alpha, phi, seen)
                && typedefeq(lt.getCodom(), sl, rt.getCodom(), sr, alpha.extend(lt.getId(), rt.getId()), phi, seen);
        
        if (l instanceof ASTTPair lt && r instanceof ASTTPair rt)
            return typedefeq(lt.getFirst(), sl, rt.getFirst(), sr, alpha, phi, seen)
                && typedefeq(lt.getSecond(), sl, rt.getSecond(), sr, alpha.extend(lt.getId(), rt.getId()), phi, seen);
        if (l instanceof ASTTTensor lt && r instanceof ASTTTensor rt)
            return typedefeq(lt.getFirst(), sl, rt.getFirst(), sr, alpha, phi, seen)
                && typedefeq(lt.getSecond(), sl, rt.getSecond(), sr, alpha.extend(lt.getId(), rt.getId()), phi, seen);
        
        if (l instanceof ASTTUnion lt && r instanceof ASTTUnion rt) {
            Map<String, ASTType> left = lt.getMap();
            Map<String, ASTType> right = rt.getMap();
            if (left.size() != right.size()) return false;
            for (String label : left.keySet()) {
                ASTType leftType = left.get(label);
                ASTType rightType = right.get(label);
                if (rightType == null || !typedefeq(leftType, sl, rightType, sr, alpha, phi, seen)) return false;
            }
            return true;
        }
        if (l instanceof ASTTLUnion lt && r instanceof ASTTLUnion rt) {
            Map<String, ASTType> left = lt.getMap();
            Map<String, ASTType> right = rt.getMap();
            if (left.size() != right.size()) return false;
            for (String label : left.keySet()) {
                ASTType leftType = left.get(label);
                ASTType rightType = right.get(label);
                if (rightType == null || !typedefeq(leftType, sl, rightType, sr, alpha, phi, seen)) return false;
            }
            return true;
        }

        if (l instanceof ASTTEq lt && r instanceof ASTTEq rt)
            return termdefeq(lt.getTerm1(), sl, rt.getTerm1(), sr, alpha, phi)
                && termdefeq(lt.getTerm2(), sl, rt.getTerm2(), sr, alpha, phi)
                && typedefeq(lt.getType(), sl, rt.getType(), sr, alpha, phi, seen);

        if (l instanceof ASTTId lt) {
            ASTType newl = phi.unfold(lt);
            return typedefeq(newl, newl.getSig(), r, sr, alpha, phi, seen);
        }
        if (r instanceof ASTTId rt) {
            ASTType newr = phi.unfold(rt);
            return typedefeq(l, sl, newr, newr.getSig(), alpha, phi, seen);
        }
        
        return false;
    }

}
