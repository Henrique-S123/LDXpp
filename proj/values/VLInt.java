package proj.values;

public class VLInt implements IValue {
    int val;

    public VLInt(int v) {
        val = v;
    }

    public int getval() {
        return val;
    }

    public String toStr() {
        return Integer.toString(val) + "l";
    }
}