package proj.values;

public class VCons implements IValue {
    IValue f;
    IValue s;

    public VCons(IValue f0, IValue s0) {
        f = f0;
        s = s0;
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

    public String toStr() {
        return "(" + f.toStr() + "::" + s.toStr() + ")";
    }
}