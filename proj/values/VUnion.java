package proj.values;

public class VUnion implements IValue {

    String label;
    IValue val;
    boolean lin;

    public VUnion(String la, IValue v, boolean l) {
        label = la;
        val = v;
        lin = l;
    }

    public String getLabel() {
        return label;
    }

    public IValue getValue() {
        return val;
    }
    
    public String toString() {
        return (lin ? "linear union " : "union ") + label + "(" + val.toString() + ")";
    }
}