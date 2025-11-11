package proj.values;

public class VBool implements IValue {
    boolean val, lin;

    public VBool(boolean v, boolean l) {
        val = v;
        lin = l;
    }

    public boolean getval() {
        return val;
    }

    public boolean islin() {
        return lin;
    }

    public String toStr() {
        return Boolean.toString(val) + (lin ? "l" : "");
    }
}