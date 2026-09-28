package proj.src.defeq;

import proj.src.ast.*;
import proj.src.env.*;
import proj.src.types.*;
import proj.src.debug.Debug;

import java.util.*;

public final class DefEq {
    public static final boolean termdefeq(ASTNode l, ASTNode r, PureEnvSet pe, AlphaEnv alpha) {
        return termdefeq(l, r, pe, alpha, false);
    }

    public static final boolean termdefeq(ASTNode l, ASTNode r, PureEnvSet pe, AlphaEnv alpha, boolean hyp) {
        Debug.log(String.format("left: %s", l));
        Debug.log(String.format("right: %s", r));
        Debug.nl();

        if (congruent(l, r, pe, alpha, hyp)) return true;

        if (etaEquiv(l, r, pe, alpha, hyp)) return true;

        if (hyp && useHyp(l, r, pe, alpha)) return true;

        if (solveTerm(true, l, r, pe, alpha, hyp)) return true;
        if (solveTerm(false, l, r, pe, alpha, hyp)) return true;

        Debug.log("Failed to prove equality");
        return false;
    }

    private static final boolean congruent(ASTNode l, ASTNode r, PureEnvSet pe, AlphaEnv alpha, boolean hyp) {
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
            return typedefeq(ln.getDeclType(), rn.getDeclType(), pe, alpha, new HashSet<IdPair>(), hyp)
                && termdefeq(ln.getExpr(), rn.getExpr(), pe, alpha, hyp)
                && termdefeq(ln.getBody(), rn.getBody(), pe, alpha.extend(ln.getId(), rn.getId()), hyp);
        
        if (l instanceof ASTOp ln && r instanceof ASTOp rn && ln.getOp().equals(rn.getOp()))
            return termdefeq(ln.getLhs(), rn.getLhs(), pe, alpha, hyp)
                && termdefeq(ln.getRhs(), rn.getRhs(), pe, alpha, hyp);
        if (l instanceof ASTIf ln && r instanceof ASTIf rn)
            return termdefeq(ln.getTest(), rn.getTest(), pe, alpha, hyp)
                && termdefeq(ln.getConseq(), rn.getConseq(), pe, alpha, hyp)
                && termdefeq(ln.getAlt(), rn.getAlt(), pe, alpha, hyp);
        
        if (l instanceof ASTFunc ln && r instanceof ASTFunc rn && ln.isLinear() == rn.isLinear())
            return typedefeq(ln.getArgtype(), rn.getArgtype(), pe, alpha, new HashSet<IdPair>(), hyp)
                && termdefeq(ln.getBody().weaknorm(), rn.getBody().weaknorm(), pe, alpha.extend(ln.getId(), rn.getId()), hyp);
        if (l instanceof ASTApp ln && r instanceof ASTApp rn)
            return termdefeq(ln.getFunc(), rn.getFunc(), pe, alpha, hyp) && termdefeq(ln.getArg(), rn.getArg(), pe, alpha, hyp);
        if (l instanceof ASTLetrec ln && r instanceof ASTLetrec rn)
            return typedefeq(ln.getFunctype(), rn.getFunctype(), pe, alpha, new HashSet<IdPair>(), hyp)
                && termdefeq(ln.getFuncbody(), rn.getFuncbody(), pe, alpha, hyp)
                && termdefeq(ln.getBody(), rn.getBody(), pe, alpha.extend(ln.getFuncid(), rn.getFuncid()), hyp);
        
        if (l instanceof ASTPair ln && r instanceof ASTPair rn && ln.isLinear() == rn.isLinear())
            return termdefeq(ln.getFirst(), rn.getFirst(), pe, alpha, hyp)
                && termdefeq(ln.getSecond(), rn.getSecond(), pe, alpha, hyp);
        if (l instanceof ASTProj ln && r instanceof ASTProj rn && ln.getPos() == rn.getPos())
            return termdefeq(ln.getPair(), rn.getPair(), pe, alpha, hyp);
        if (l instanceof ASTSplit ln && r instanceof ASTSplit rn)
            return (termdefeq(ln.getPair(), rn.getPair(), pe, alpha, hyp)
                && termdefeq(ln.getBody(), rn.getBody(), pe, alpha.extend(ln.getId1(), rn.getId1()).extend(ln.getId2(), rn.getId2()), hyp));
        
        if (l instanceof ASTUnion ln && r instanceof ASTUnion rn && ln.getLabel().equals(rn.getLabel()))
            return termdefeq(ln.getExpr(), rn.getExpr(), pe, alpha, hyp);
        if (l instanceof ASTMatch ln && r instanceof ASTMatch rn) {
            if (termdefeq(ln.getTest(), rn.getTest(), pe, alpha, hyp)) {
                Set<String> left = ln.getLabels();
                Set<String> right = rn.getLabels();
                if (left.size() != right.size() || !left.containsAll(right)) return false;
                for (String label : left)
                    if (!termdefeq(ln.getCaseExp(label), rn.getCaseExp(label), pe, alpha.extend(ln.getCaseId(label), rn.getCaseId(label)), hyp))
                            return false;
                return true;
            }
            return false;
        }
        if (l instanceof ASTNever && r instanceof ASTNever) return true;

        if (l instanceof ASTUnit && r instanceof ASTUnit) return true;
        if (l instanceof ASTSeq ln && r instanceof ASTSeq rn)
            return (termdefeq(ln.getFirst(), rn.getFirst(), pe, alpha, hyp) && termdefeq(ln.getSecond(), rn.getSecond(), pe, alpha, hyp));
        if (l instanceof ASTPrint ln && r instanceof ASTPrint rn && ln.getNewline() == rn.getNewline())
            return termdefeq(ln.getExp(), rn.getExp(), pe, alpha, hyp);

        if (l instanceof ASTRefl && r instanceof ASTRefl) return true;
        if (l instanceof ASTLeteq ln && r instanceof ASTLeteq rn)
            return (termdefeq(ln.getExpr(), rn.getExpr(), pe, alpha, hyp)
                && termdefeq(ln.getBody(), rn.getBody(), pe, alpha.extend(ln.getId(), rn.getId()), hyp));
        
        if (l instanceof ASTTypeDef ln && r instanceof ASTTypeDef rn)
            return ln.getLtd().equals(rn.getLtd()) && termdefeq(ln.getBody(), rn.getBody(), pe, alpha, hyp);

        return false;
    }

    private static final boolean etaEquiv(ASTNode l, ASTNode r, PureEnvSet pe, AlphaEnv alpha, boolean hyp) {
        ASTId id = null;
        ASTNode other = null;
        ASTType type = null;

        if (l instanceof ASTId i) { id = i; other = r; }
        else if (r instanceof ASTId i) { id = i; other = l; }
        else return false;

        type = pe.getSigma().find(id.getId());
        if (type == null) return false;

        if (type instanceof ASTTUnit && other instanceof ASTUnit) return true;
        else if (type instanceof ASTTPair tp && other instanceof ASTPair np && !tp.isLinear() && !np.isLinear())
            return termdefeq(new ASTProj(id, true), np.getFirst(), pe, alpha, hyp)
                && termdefeq(new ASTProj(id, false), np.getSecond(), pe, alpha, hyp);

        return false;
    }

    private static final boolean useHyp(ASTNode l, ASTNode r, PureEnvSet pe, AlphaEnv alpha) {
        Debug.log("Searching Sigma environment for a proof");
        Env<ASTType> curr = pe.getSigma();
        while (curr != null) {
            for (Binder<ASTType> e : curr.getBindings().values())
                if (e.val instanceof ASTTEq teq) {
                    ASTTEq res = null;
                    Debug.log("Testing proof: " + e);
                    Debug.open();
                    if (isProof(teq, l, r, pe, alpha)) res = teq;
                    Debug.close();
                    Debug.nl();
                    if (res != null) {
                        Debug.log("Found proof: " + res);
                        return true;
                    }
                }
            curr = curr.endScope();
        }
        Debug.log("Found no proof.");
        return false;
    }

    private static final boolean isProof(ASTTEq e, ASTNode l, ASTNode r, PureEnvSet pe, AlphaEnv alpha) {
        return ((termdefeq(l, e.getTerm1(), pe, alpha)) && termdefeq(r, e.getTerm2(), pe, alpha, false)) ||
            ((termdefeq(l, e.getTerm2(), pe, alpha)) && termdefeq(r, e.getTerm1(), pe, alpha, false));
    }

    private static final boolean solveTerm(boolean left, ASTNode l, ASTNode r, PureEnvSet pe, AlphaEnv alpha, boolean hyp) {
        ASTNode term = left ? l : r, other = left ? r : l;
        ASTNode solved = term.solve(pe.getSigma());
        if (solved == null) return false;
        solved = solved.weaknorm();
        if (term.structEq(solved)) return false;
        Debug.log(String.format("Solved %s side", left ? "left" : "right"));
        return termdefeq(solved, other, pe, alpha, hyp);
    }

    record IdPair(String id1, String id2) {}

    public static final boolean typedefeq(ASTType l, ASTType r, PureEnvSet pe, AlphaEnv alpha) {
        return typedefeq(l, r, pe, alpha, new HashSet<IdPair>(), true);
    }

    private static final boolean typedefeq(ASTType l, ASTType r, PureEnvSet pe, AlphaEnv alpha, Set<IdPair> seen, boolean hyp) {
        if (l instanceof ASTTInt li && r instanceof ASTTInt ri) return li.isLinear() == ri.isLinear();
        if (l instanceof ASTTBool lb && r instanceof ASTTBool rb) return lb.isLinear() == rb.isLinear();
        if (l instanceof ASTTString && r instanceof ASTTString) return true;
        if (l instanceof ASTTUnit && r instanceof ASTTUnit) return true;

        if (l instanceof ASTTId lt && r instanceof ASTTId rt) {
            if (lt.getId().equals(rt.getId())) return true;
            IdPair p = new IdPair(lt.getId(), rt.getId());
            if (seen.contains(p)) return true;
            seen.add(p);
            return typedefeq(pe.unfold(lt), pe.unfold(rt), pe, alpha, seen, hyp);
        }
        
        if (l instanceof ASTTArrow lt && r instanceof ASTTArrow rt)
            return typedefeq(lt.getDom(), rt.getDom(), pe, alpha, seen, hyp)
                && typedefeq(lt.getCodom(), rt.getCodom(), pe, alpha.extend(lt.getId(), rt.getId()), seen, hyp);
        
        if (l instanceof ASTTPair lt && r instanceof ASTTPair rt)
            return typedefeq(lt.getFirst(), rt.getFirst(), pe, alpha, seen, hyp)
                && typedefeq(lt.getSecond(), rt.getSecond(), pe, alpha.extend(lt.getId(), rt.getId()), seen, hyp);
        
        if (l instanceof ASTTUnion lt && r instanceof ASTTUnion rt && lt.isLinear() == rt.isLinear()) {
            Map<String, ASTType> left = lt.getMap();
            Map<String, ASTType> right = rt.getMap();
            if (left.size() != right.size() || !left.keySet().containsAll(right.keySet())) return false;
            for (String label : left.keySet())
                if (!typedefeq(left.get(label), right.get(label), pe, alpha, seen, hyp)) return false;
            return true;
        }

        if (l instanceof ASTTEq lt && r instanceof ASTTEq rt)
            return termdefeq(lt.getTerm1().weaknorm(), rt.getTerm1().weaknorm(), pe, alpha, hyp)
                && termdefeq(lt.getTerm2().weaknorm(), rt.getTerm2().weaknorm(), pe, alpha, hyp)
                && typedefeq(lt.getType(), rt.getType(), pe, alpha, seen, hyp);

        if (l instanceof ASTTId lt) return typedefeq(pe.unfold(lt), r, pe, alpha, seen, hyp);
        if (r instanceof ASTTId rt) return typedefeq(l, pe.unfold(rt), pe, alpha, seen, hyp);

        return false;
    }
}
