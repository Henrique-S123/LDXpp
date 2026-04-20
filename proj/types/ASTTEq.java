package proj.types;

import proj.ast.*;
import proj.env.*;

public class ASTTEq implements ASTType {
    ASTNode term1, term2;
    ASTType type;

    public ASTTEq(ASTNode t1, ASTNode t2, ASTType t) {
        term1 = t1;
        term2 = t2;
        type = t;
    }

    public ASTNode getTerm1() {
        return term1;
    }

    public ASTNode getTerm2() {
        return term2;
    }

    public ASTType getType() {
        return type;
    }

    public String toStr() {
        return term1+" ≡ "+ term2+" : "+type.toStr();
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTEq && type.isSubtypeOf(((ASTTEq) o).getType(), e)
            && term1.defequals(((ASTTEq) o).getTerm1()) && term2.defequals(((ASTTEq) o).getTerm2());
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma) {
        return o instanceof ASTTEq && ((ASTTEq) o).getTerm1().normalize(sigma).defequals(term1.normalize(sigma))
            && ((ASTTEq) o).getTerm2().normalize(sigma).defequals(term2.normalize(sigma)) && ((ASTTEq) o).getType().defequals(type, sigma);
    }
}

