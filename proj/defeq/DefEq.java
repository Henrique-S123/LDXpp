package proj.defeq;

import proj.ast.*;
import proj.env.*;
import proj.types.*;
import proj.debug.Debug;

import java.util.*;

public final class DefEq {
    public record IdPair(String id1, String id2) {}

    public static final boolean termdefeq(ASTNode l, ASTNode r, Env<ASTType> sigma, Env<ASTType> phi, boolean hyp) {
        return termdefeq(l, sigma, r, sigma, new AlphaEnv(), phi, hyp);
    }

    public static final boolean termdefeq(ASTNode l, ASTNode r, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        return termdefeq(l, sigma, r, sigma, alpha, phi, false);
    }

    public static final boolean termdefeq(ASTNode l, ASTNode r, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, boolean hyp) {
        return termdefeq(l, sigma, r, sigma, alpha, phi, hyp);
    }

    private static final boolean termdefeq(ASTNode l, Env<ASTType> sl, ASTNode r, Env<ASTType> sr, AlphaEnv alpha, Env<ASTType> phi, boolean ctx) {
        Debug.log(String.format("left: %s", l));
        Debug.log(String.format("right: %s", r));

        if (l instanceof ASTInt ln && r instanceof ASTInt rn) return ln.getVal() == rn.getVal();
        if (l instanceof ASTLInt ln && r instanceof ASTLInt rn) return ln.getVal() == rn.getVal();
        if (l instanceof ASTBool ln && r instanceof ASTBool rn) return ln.getVal() == rn.getVal();
        if (l instanceof ASTLBool ln && r instanceof ASTLBool rn) return ln.getVal() == rn.getVal();
        if (l instanceof ASTString ln && r instanceof ASTString rn) return ln.getVal().equals(rn.getVal());

        if (l instanceof ASTId ln && r instanceof ASTId rn) {
            if (alpha.equiv(ln.getId(), rn.getId())) return true;
            String bid1 = ln.getBinderId();
            String bid2 = rn.getBinderId();
            Debug.log("LEFT BINDER ID: " + bid1);
            Debug.log("RIGHT BINDER ID: " + bid2);
            if (bid1 != null && bid1.equals(bid2)) return true;
        }

        if (l instanceof ASTLet ln && r instanceof ASTLet rn)
            return typedefeq(ln.getDeclType(), sl, rn.getDeclType(), sr, alpha, phi, new HashSet<IdPair>(), ctx)
                && termdefeq(ln.getExpr(), sl, rn.getExpr(), sr, alpha, phi, ctx)
                && termdefeq(ln.getBody(), sl, rn.getBody(), sr, alpha.extend(ln.getId(), rn.getId()), phi, ctx);
        
        if (l instanceof ASTArithOp ln && r instanceof ASTArithOp rn && ln.getOp().equals(rn.getOp()))
            return termdefeq(ln.getLhs(), sl, rn.getLhs(), sr, alpha, phi, ctx)
                && termdefeq(ln.getRhs(), sl, rn.getRhs(), sr, alpha, phi, ctx);
        if (l instanceof ASTCmpOp ln && r instanceof ASTCmpOp rn && ln.getOp().equals(rn.getOp()))
            return termdefeq(ln.getLhs(), sl, rn.getLhs(), sr, alpha, phi, ctx)
                && termdefeq(ln.getRhs(), sl, rn.getRhs(), sr, alpha, phi, ctx);
        if (l instanceof ASTLogicOp ln && r instanceof ASTLogicOp rn && ln.getOp().equals(rn.getOp()))
            return termdefeq(ln.getLhs(), sl, rn.getLhs(), sr, alpha, phi, ctx)
                && termdefeq(ln.getRhs(), sl, rn.getRhs(), sr, alpha, phi, ctx);
        
        if (l instanceof ASTIf ln && r instanceof ASTIf rn)
            if (termdefeq(ln.getTest(), sl, rn.getTest(), sr, alpha, phi, ctx)
                && termdefeq(ln.getConseq(), sl, rn.getConseq(), sr, alpha, phi, ctx)
                && termdefeq(ln.getAlt(), sl, rn.getAlt(), sr, alpha, phi, ctx)) return true;
        
        if (l instanceof ASTFunc ln && r instanceof ASTFunc rn)
            return typedefeq(ln.getArgtype(), sl, rn.getArgtype(), sr, alpha, phi, new HashSet<IdPair>(), ctx)
                && termdefeq(ln.getBody().weaknorm(), sl, rn.getBody().weaknorm(), sr, alpha.extend(ln.getId(), rn.getId()), phi, ctx);
        if (l instanceof ASTLFunc ln && r instanceof ASTLFunc rn)
            return typedefeq(ln.getArgtype(), sl, rn.getArgtype(), sr, alpha, phi, new HashSet<IdPair>(), ctx)
                && termdefeq(ln.getBody().weaknorm(), sl, rn.getBody().weaknorm(), sr, alpha.extend(ln.getId(), rn.getId()), phi, ctx);
        if (l instanceof ASTApp ln && r instanceof ASTApp rn) {
            Debug.open();
            Debug.log("Checking if parts are equal");
            boolean res = termdefeq(ln.getFunc(), sl, rn.getFunc(), sr, alpha, phi, ctx) && termdefeq(ln.getArg(), sl, rn.getArg(), sr, alpha, phi, ctx);
            Debug.close();
            if (res) return true;
        }
        // TODO: add ASTRec case
        
        if (l instanceof ASTPair ln && r instanceof ASTPair rn)
            return termdefeq(ln.getFirst(), sl, rn.getFirst(), sr, alpha, phi, ctx)
                && termdefeq(ln.getSecond(), sl, rn.getSecond(), sr, alpha, phi, ctx);
        if (l instanceof ASTChoice ln && r instanceof ASTChoice rn && ln.getChoice() == rn.getChoice())
            if (termdefeq(ln.getPair(), sl, rn.getPair(), sr, alpha, phi, ctx)) return true;
        if (l instanceof ASTTensor ln && r instanceof ASTTensor rn)
            return termdefeq(ln.getFirst(), sl, rn.getFirst(), sr, alpha, phi, ctx)
                && termdefeq(ln.getSecond(), sl, rn.getSecond(), sr, alpha, phi, ctx);
        if (l instanceof ASTSplit ln && r instanceof ASTSplit rn)
            if (termdefeq(ln.getPair(), sl, rn.getPair(), sr, alpha, phi, ctx)
                && termdefeq(ln.getBody(), sl, rn.getBody(), sr, alpha.extend(ln.getId1(), rn.getId1()).extend(ln.getId2(), rn.getId2()), phi, ctx)) return true;
        
        if (l instanceof ASTUnion ln && r instanceof ASTUnion rn && ln.getLabel().equals(rn.getLabel()))
            return termdefeq(ln.getExpr(), sl, rn.getExpr(), sr, alpha, phi, ctx);
        if (l instanceof ASTLUnion ln && r instanceof ASTLUnion rn && ln.getLabel().equals(rn.getLabel()))
            return termdefeq(ln.getExpr(), sl, rn.getExpr(), sr, alpha, phi, ctx);
        if (l instanceof ASTMatch ln && r instanceof ASTMatch rn) {
            Debug.open();
            Debug.log("Checking if parts are equal");
            boolean res = false;
            if (termdefeq(ln.getTest(), sl, rn.getTest(), sr, alpha, phi, ctx)) {
                Map<String, MatchCase> left = ln.getCases();
                Map<String, MatchCase> right = rn.getCases();
                if (left.size() == right.size()) {
                    boolean diff = false;
                    for (String label : ln.getCases().keySet()) {
                        MatchCase leftCase = left.get(label);
                        MatchCase rightCase = right.get(label);
                        if (rightCase == null ||
                            !termdefeq(leftCase.getExp(), sl, rightCase.getExp(), sr, alpha.extend(leftCase.getId(), rightCase.getId()), phi, ctx)) diff = true;
                    }
                    if (!diff) res = true;
                }
            }
            Debug.close();
            if (res) return true;
        }

        if (l instanceof ASTUnit && r instanceof ASTUnit) return true;
        if (l instanceof ASTSeq ln && r instanceof ASTSeq rn)
            if (termdefeq(ln.getFirst(), sl, rn.getSecond(), sr, alpha, phi, ctx)
                && termdefeq(ln.getSecond(), sl, rn.getSecond(), sr, alpha, phi, ctx)) return true;
        if (l instanceof ASTPrint ln && r instanceof ASTPrint rn && ln.getNewline() == rn.getNewline())
            return termdefeq(ln.getExp(), sl, rn.getExp(), sr, alpha, phi, ctx);

        if (l instanceof ASTRefl && r instanceof ASTRefl) return true;
        if (l instanceof ASTLeteq ln && r instanceof ASTLeteq rn)
            if (termdefeq(ln.getExpr(), sl, rn.getExpr(), sr, alpha, phi, ctx)
                && termdefeq(ln.getBody(), sl, rn.getBody(), sr, alpha.extend(ln.getId(), rn.getId()), phi, ctx)) return true;
        
        if (l instanceof ASTTypeDef ln && r instanceof ASTTypeDef rn)
            return ln.getLtd().equals(rn.getLtd())
                && termdefeq(ln.getBody(), sl, rn.getBody(), sr, alpha, phi, ctx);
        
        if (ctx) {
            Debug.log("Search Sigma environment for a proof");
            ASTType proof = sl.findProof(l, r, sl, phi);
            if (proof != null) {
                Debug.log("Found proof: " + proof);
                return true;
            }
            if (sl != sr) proof = sr.findProof(l, r, sr, phi);
            if (proof != null) {
                Debug.log("Found proof: " + proof);
                return true;
            }
        }
        
        Debug.log("Trying to solve the left side");
        ASTNode s = l.solve(l.getSig() != null ? l.getSig() : sl);
        if (s != null) {
            Debug.log("Solved left side");
            return termdefeq(s.weaknorm(), (s.getSig() != null) ? s.getSig() : sl, r, sr, alpha, phi, ctx);
        }
        Debug.log("Trying to solve the right side");
        s = r.solve(r.getSig() != null ? r.getSig() : sr);
        if (s != null) {
            Debug.log("Solved right side");
            return termdefeq(l, sl, s.weaknorm(), (s.getSig() != null) ? s.getSig() : sr, alpha, phi, ctx);
        }
        Debug.log("Failed to prove equality");
        Debug.nl();
        return false;
    }

    public static final boolean typedefeq(ASTType l, ASTType r, Env<ASTType> sigma, Env<ASTType> phi) {
        return typedefeq(l, sigma, r, sigma, new AlphaEnv(), phi, new HashSet<IdPair>(), false);
    }

    public static final boolean typedefeq(ASTType l, ASTType r, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        return typedefeq(l, sigma, r, sigma, alpha, phi, new HashSet<IdPair>(), false);
    }

    private static final boolean typedefeq(ASTType l, Env<ASTType> sl, ASTType r, Env<ASTType> sr, AlphaEnv alpha, Env<ASTType> phi, Set<IdPair> seen, boolean hyp) {
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
            return typedefeq(newl, newl.getSig(), newr, newr.getSig(), alpha, phi, seen, hyp);
        }
        
        if (l instanceof ASTTArrow lt && r instanceof ASTTArrow rt)
            return typedefeq(lt.getDom(), sl, rt.getDom(), sr, alpha, phi, seen, hyp)
                && typedefeq(lt.getCodom(), sl, rt.getCodom(), sr, alpha.extend(lt.getId(), rt.getId()), phi, seen, hyp);
        if (l instanceof ASTTLollipop lt && r instanceof ASTTLollipop rt)
            return typedefeq(lt.getDom(), sl, rt.getDom(), sr, alpha, phi, seen, hyp)
                && typedefeq(lt.getCodom(), sl, rt.getCodom(), sr, alpha.extend(lt.getId(), rt.getId()), phi, seen, hyp);
        
        if (l instanceof ASTTPair lt && r instanceof ASTTPair rt)
            return typedefeq(lt.getFirst(), sl, rt.getFirst(), sr, alpha, phi, seen, hyp)
                && typedefeq(lt.getSecond(), sl, rt.getSecond(), sr, alpha.extend(lt.getId(), rt.getId()), phi, seen, hyp);
        if (l instanceof ASTTTensor lt && r instanceof ASTTTensor rt)
            return typedefeq(lt.getFirst(), sl, rt.getFirst(), sr, alpha, phi, seen, hyp)
                && typedefeq(lt.getSecond(), sl, rt.getSecond(), sr, alpha.extend(lt.getId(), rt.getId()), phi, seen, hyp);
        
        if (l instanceof ASTTUnion lt && r instanceof ASTTUnion rt) {
            Map<String, ASTType> left = lt.getMap();
            Map<String, ASTType> right = rt.getMap();
            if (left.size() != right.size()) return false;
            for (String label : left.keySet()) {
                ASTType leftType = left.get(label);
                ASTType rightType = right.get(label);
                if (rightType == null || !typedefeq(leftType, sl, rightType, sr, alpha, phi, seen, hyp)) return false;
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
                if (rightType == null || !typedefeq(leftType, sl, rightType, sr, alpha, phi, seen, hyp)) return false;
            }
            return true;
        }

        if (l instanceof ASTTEq lt && r instanceof ASTTEq rt)
            return termdefeq(lt.getTerm1(), sl, rt.getTerm1(), sr, alpha, phi, hyp)
                && termdefeq(lt.getTerm2(), sl, rt.getTerm2(), sr, alpha, phi, hyp)
                && typedefeq(lt.getType(), sl, rt.getType(), sr, alpha, phi, seen, hyp);

        if (l instanceof ASTTId lt) {
            ASTType newl = phi.unfold(lt);
            return typedefeq(newl, newl.getSig(), r, sr, alpha, phi, seen, hyp);
        }
        if (r instanceof ASTTId rt) {
            ASTType newr = phi.unfold(rt);
            return typedefeq(l, sl, newr, newr.getSig(), alpha, phi, seen, hyp);
        }
        
        return false;
    }

}
