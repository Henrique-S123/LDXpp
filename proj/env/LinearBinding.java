package proj.env;

import proj.types.ASTType;

public class LinearBinding {
    ASTType type;
    boolean available;

    public LinearBinding(ASTType t, boolean a) {
        type = t;
        available = a;
    }

    public ASTType getType() {
        return type;
    }

    public boolean isAvailable() {
        return available;
    }

    public void free() {
        available = true;
    }

    public void use() {
        available = false;
    }
}
