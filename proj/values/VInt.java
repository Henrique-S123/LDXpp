package proj.values;

public class VInt implements IValue {
    int val;
    boolean lin;

    public VInt(int v, boolean l) {
        val = v;
        lin = l;
    }

    public int getval() {
        return val;
    }

    public boolean islin() {
        return lin;
    }

    public String toStr() {
        return Integer.toString(val) + (lin ? "l" : "");
    }
}