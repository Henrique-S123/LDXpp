package proj.values;

public class VPair implements IValue {
    IValue f, s;
    boolean lin;

    public VPair(IValue f0, IValue s0, boolean l) {
        f = f0;
        s = s0;
        lin = l;
    }

    public IValue getFirst() {
        return f;
    }

    public IValue getSecond() {
        return s;
    }

    public void setFirst(IValue f0) {
        f = f0;
    }

    public void setSecond(IValue s0) {
        s = s0;
    }

    public boolean islin() {
        return lin;
    }

    public String toStr() {
        return lin ? "(" + f.toStr() + "@" + s.toStr() + ")"
            : "(" + f.toStr() + "&" + s.toStr() + ")";
    }
}