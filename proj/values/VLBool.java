package proj.values;

public class VLBool implements IValue {
    boolean val;

    public VLBool(boolean v) {
        val = v;
    }

    public boolean getval() {
        return val;
    }

    public String toStr() {
        return Boolean.toString(val) + "l";
    }
}