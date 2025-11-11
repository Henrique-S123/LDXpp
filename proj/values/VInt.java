package proj.values;

public class VInt implements IValue {
    int v;

    public VInt(int v0) {
        v = v0;
    }

    public int getval() {
        return v;
    }

    public String toStr() {
        return Integer.toString(v);
    }
}