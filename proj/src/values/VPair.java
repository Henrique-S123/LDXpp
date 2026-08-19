package proj.src.values;

public class VPair implements IValue {
    IValue first, second;
    boolean lin;

    public VPair(IValue f, IValue s, boolean l) {
        first = f; second = s; lin = l;
    }

    public IValue getFirst() { return first; }

    public IValue getSecond() { return second; }

    public boolean islin() { return lin; }

    public String toString() {
        return String.format("(%s%s%s)", first, lin ? " | " : ", ", second);
    }
}