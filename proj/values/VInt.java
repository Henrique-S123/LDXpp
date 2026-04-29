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

    public String toString() {
        return String.format("%s%s", val, lin ? "l" : "");
    }
}