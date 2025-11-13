package proj.values;

public class VUnion implements IValue {

    String label;
    IValue val;

    public VUnion(String l, IValue v) {
        label = l;
        val = v;
    }

    public String getLabel() {
        return label;
    }

    public IValue getValue() {
        return val;
    }
    
    public String toStr() {
        return "union " + label + "(" + val.toStr() + ")";
    }
}