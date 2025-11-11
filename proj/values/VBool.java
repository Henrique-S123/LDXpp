package proj.values;

public class VBool implements IValue {
    boolean b;

    public VBool(boolean b0) {
        b = b0;
    }

    public boolean getval() {
        return b;
    }

    public String toStr() {
        return Boolean.toString(b);
    }
}