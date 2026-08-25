package proj.src.defeq;

import proj.src.ast.*;
import proj.src.env.*;
import proj.src.types.*;
import proj.src.debug.Debug;

import java.util.*;

public final class DefEq {
    Env<ASTType> sigma;

    public DefEq(Env<ASTType> sig) { sigma = sig; }

    public final boolean termdefeq(ASTNode l, ASTNode r, Env<ASTType> phi, AlphaEnv alpha) {
        return termdefeq(l, r, phi, alpha, new TRefl());
    }

    public final boolean termdefeq(ASTNode l, ASTNode r, Env<ASTType> phi, AlphaEnv alpha, Tactic t) {
        Debug.log(String.format("left: %s", l));
        Debug.log(String.format("right: %s", r));
        Debug.nl();

        if (congruent(l, r, phi, alpha, t)) return true;

        if (t instanceof THyp h && useHyp(l, r, phi, alpha, h.getHyp())) return true;

        if (r instanceof ASTId) {
            if (solveRight(l, r, phi, alpha, t)) return true;
            if (solveLeft(l, r, phi, alpha, t)) return true;
        } else {
            if (solveLeft(l, r, phi, alpha, t)) return true;
            if (solveRight(l, r, phi, alpha, t)) return true;
        }

        Debug.log("Failed to prove equality");
        return false;
    }

    private final boolean congruent(ASTNode l, ASTNode r, Env<ASTType> phi, AlphaEnv alpha, Tactic t) {
        if (l instanceof ASTInt ln && r instanceof ASTInt rn) return ln.getVal() == rn.getVal() && ln.isLinear() == rn.isLinear();
        if (l instanceof ASTBool ln && r instanceof ASTBool rn) return ln.getVal() == rn.getVal() && ln.isLinear() == rn.isLinear();
        if (l instanceof ASTString ln && r instanceof ASTString rn) return ln.getVal().equals(rn.getVal());

        if (l instanceof ASTId ln && r instanceof ASTId rn) {
            if (alpha.equiv(ln.getId(), rn.getId())) return true;
            String bid1 = ln.getBid();
            String bid2 = rn.getBid();
            Debug.log("LEFT BINDER ID: " + bid1);
            Debug.log("RIGHT BINDER ID: " + bid2);
            return (bid1 != null && bid1.equals(bid2));
        }
        if (l instanceof ASTLet ln && r instanceof ASTLet rn)
            return typedefeq(ln.getDeclType(), rn.getDeclType(), phi, alpha, new HashSet<IdPair>(), t)
                && termdefeq(ln.getExpr(), rn.getExpr(), phi, alpha, t)
                && termdefeq(ln.getBody(), rn.getBody(), phi, alpha.extend(ln.getId(), rn.getId()), t);
        
        if (l instanceof ASTOp ln && r instanceof ASTOp rn && ln.getOp().equals(rn.getOp()))
            return termdefeq(ln.getLhs(), rn.getLhs(), phi, alpha, t)
                && termdefeq(ln.getRhs(), rn.getRhs(), phi, alpha, t);
        if (l instanceof ASTIf ln && r instanceof ASTIf rn)
            return termdefeq(ln.getTest(), rn.getTest(), phi, alpha, t)
                && termdefeq(ln.getConseq(), rn.getConseq(), phi, alpha, t)
                && termdefeq(ln.getAlt(), rn.getAlt(), phi, alpha, t);
        
        if (l instanceof ASTFunc ln && r instanceof ASTFunc rn && ln.isLinear() == rn.isLinear())
            return typedefeq(ln.getArgtype(), rn.getArgtype(), phi, alpha, new HashSet<IdPair>(), t)
                && termdefeq(ln.getBody().weaknorm(), rn.getBody().weaknorm(), phi, alpha.extend(ln.getId(), rn.getId()), t);
        if (l instanceof ASTApp ln && r instanceof ASTApp rn)
            return termdefeq(ln.getFunc(), rn.getFunc(), phi, alpha, t) && termdefeq(ln.getArg(), rn.getArg(), phi, alpha, t);
        if (l instanceof ASTLetrec ln && r instanceof ASTLetrec rn)
            return typedefeq(ln.getFunctype(), rn.getFunctype(), phi, alpha, new HashSet<IdPair>(), t)
                && termdefeq(ln.getFuncbody(), rn.getFuncbody(), phi, alpha, t)
                && termdefeq(ln.getBody(), rn.getBody(), phi, alpha.extend(ln.getFuncid(), rn.getFuncid()), t);
        
        if (l instanceof ASTPair ln && r instanceof ASTPair rn && ln.isLinear() == rn.isLinear())
            return termdefeq(ln.getFirst(), rn.getFirst(), phi, alpha, t)
                && termdefeq(ln.getSecond(), rn.getSecond(), phi, alpha, t);
        if (l instanceof ASTChoice ln && r instanceof ASTChoice rn && ln.getChoice() == rn.getChoice())
            return termdefeq(ln.getPair(), rn.getPair(), phi, alpha, t);
        if (l instanceof ASTSplit ln && r instanceof ASTSplit rn)
            return (termdefeq(ln.getPair(), rn.getPair(), phi, alpha, t)
                && termdefeq(ln.getBody(), rn.getBody(), phi, alpha.extend(ln.getId1(), rn.getId1()).extend(ln.getId2(), rn.getId2()), t));
        
        if (l instanceof ASTUnion ln && r instanceof ASTUnion rn && ln.getLabel().equals(rn.getLabel()))
            return termdefeq(ln.getExpr(), rn.getExpr(), phi, alpha, t);
        if (l instanceof ASTMatch ln && r instanceof ASTMatch rn) {
            if (termdefeq(ln.getTest(), rn.getTest(), phi, alpha, t)) {
                Set<String> left = ln.getLabels();
                Set<String> right = rn.getLabels();
                if (left.size() != right.size() || !left.containsAll(right)) return false;
                for (String label : left)
                    if (!termdefeq(ln.getCaseExp(label), rn.getCaseExp(label), phi, alpha.extend(ln.getCaseId(label), rn.getCaseId(label)), t))
                            return false;
                return true;
            }
            return false;
        }

        if (l instanceof ASTUnit && r instanceof ASTUnit) return true;
        if (l instanceof ASTSeq ln && r instanceof ASTSeq rn)
            return (termdefeq(ln.getFirst(), rn.getFirst(), phi, alpha, t) && termdefeq(ln.getSecond(), rn.getSecond(), phi, alpha, t));
        if (l instanceof ASTPrint ln && r instanceof ASTPrint rn && ln.getNewline() == rn.getNewline())
            return termdefeq(ln.getExp(), rn.getExp(), phi, alpha, t);

        if (l instanceof ASTRefl && r instanceof ASTRefl) return true;
        if (l instanceof ASTLeteq ln && r instanceof ASTLeteq rn)
            return (termdefeq(ln.getExpr(), rn.getExpr(), phi, alpha, t)
                && termdefeq(ln.getBody(), rn.getBody(), phi, alpha.extend(ln.getId(), rn.getId()), t));
        
        if (l instanceof ASTTypeDef ln && r instanceof ASTTypeDef rn)
            return ln.getLtd().equals(rn.getLtd()) && termdefeq(ln.getBody(), rn.getBody(), phi, alpha, t);

        return false;
    }

    private final boolean useHyp(ASTNode l, ASTNode r, Env<ASTType> phi, AlphaEnv alpha, String name) {
        if (name != null) {
            Debug.log(String.format("Checking if %s is a correct proof", name));
            boolean res = sigma.checkProof(name, sigma, l, r, alpha, phi);
            Debug.log(name + (res ? " is a proof!" : " is not a proof!"));
            return res;
        } else {
            Debug.log("Search Sigma environment for a proof");
            ASTType proof = sigma.findProof(sigma, l, r, alpha, phi);
            if (proof != null) {
                Debug.log("Found proof: " + proof);
                return true;
            }
            Debug.log("Found no proof.");
        }
        return false;
    }

    private final boolean solveLeft(ASTNode l, ASTNode r, Env<ASTType> phi, AlphaEnv alpha, Tactic t) {
        ASTNode s = l.solve(sigma);
        if (s == null) return false;
        s = s.weaknorm();
        if (StructEq.termEqStruct(l, s, alpha)) return false;
        Debug.log("Solved left side");
        return termdefeq(s, r, phi, alpha, t);
    }

    private final boolean solveRight(ASTNode l, ASTNode r, Env<ASTType> phi, AlphaEnv alpha, Tactic t) {
        ASTNode s = r.solve(sigma);
        if (s == null) return false;
        s = s.weaknorm();
        if (StructEq.termEqStruct(r, s, alpha)) return false;
        Debug.log("Solved right side");
        return termdefeq(l, s, phi, alpha, t);
    }

    record IdPair(String id1, String id2) {}

    public final boolean typedefeq(ASTType l, ASTType r, Env<ASTType> phi, AlphaEnv alpha) {
        return typedefeq(l, r, phi, alpha, new HashSet<IdPair>(), new TRefl());
    }

    private final boolean typedefeq(ASTType l, ASTType r, Env<ASTType> phi, AlphaEnv alpha, Set<IdPair> seen, Tactic t) {
        if (typecongruence(l, r, phi, alpha, seen, t)) return true;

        if (unfold(l, r, phi, alpha, seen, t)) return true;

        return false;
    }

    private boolean typecongruence(ASTType l, ASTType r, Env<ASTType> phi, AlphaEnv alpha, Set<IdPair> seen, Tactic t) {
        if (l instanceof ASTTInt li && r instanceof ASTTInt ri) return li.isLinear() == ri.isLinear();
        if (l instanceof ASTTBool lb && r instanceof ASTTBool rb) return lb.isLinear() == rb.isLinear();
        if (l instanceof ASTTString && r instanceof ASTTString) return true;
        if (l instanceof ASTTUnit && r instanceof ASTTUnit) return true;

        if (l instanceof ASTTId lt && r instanceof ASTTId rt) {
            if (lt.getId().equals(rt.getId())) return true;
            if (seen.contains(new IdPair(lt.getId(), rt.getId()))) return true;
            seen.add(new IdPair(lt.getId(), rt.getId()));
            ASTType newl = phi.unfold(lt);
            ASTType newr = phi.unfold(rt);
            return typedefeq(newl, newr, phi, alpha, seen, t);
        }
        
        if (l instanceof ASTTArrow lt && r instanceof ASTTArrow rt)
            return typedefeq(lt.getDom(), rt.getDom(), phi, alpha, seen, t)
                && typedefeq(lt.getCodom(), rt.getCodom(), phi, alpha.extend(lt.getId(), rt.getId()), seen, t);
        
        if (l instanceof ASTTPair lt && r instanceof ASTTPair rt)
            return typedefeq(lt.getFirst(), rt.getFirst(), phi, alpha, seen, t)
                && typedefeq(lt.getSecond(), rt.getSecond(), phi, alpha.extend(lt.getId(), rt.getId()), seen, t);
        
        if (l instanceof ASTTUnion lt && r instanceof ASTTUnion rt && lt.isLinear() == rt.isLinear()) {
            Map<String, ASTType> left = lt.getMap();
            Map<String, ASTType> right = rt.getMap();
            if (left.size() != right.size() || !left.keySet().containsAll(right.keySet())) return false;
            for (String label : left.keySet())
                if (!typedefeq(left.get(label), right.get(label), phi, alpha, seen, t)) return false;
            return true;
        }

        if (l instanceof ASTTEq lt && r instanceof ASTTEq rt)
            return termdefeq(lt.getTerm1(), rt.getTerm1(), phi, alpha, t)
                && termdefeq(lt.getTerm2(), rt.getTerm2(), phi, alpha, t)
                && typedefeq(lt.getType(), rt.getType(), phi, alpha, seen, t);

        return false;
    }

    private boolean unfold(ASTType l, ASTType r, Env<ASTType> phi, AlphaEnv alpha, Set<IdPair> seen, Tactic t) {
        if (l instanceof ASTTId lt) return typedefeq(phi.unfold(lt), r, phi, alpha, seen, t);
        if (r instanceof ASTTId rt) return typedefeq(l, phi.unfold(rt), phi, alpha, seen, t);
        return false;
    }
}
