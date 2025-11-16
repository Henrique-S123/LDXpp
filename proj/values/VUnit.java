package proj.values;

public class VUnit implements IValue {
    boolean lin;

    public VUnit(boolean l) {
        lin = l;
    }

    public String toStr() {
        return lin ? "*" : "()";
    }
}