package proj.values;

public class VString implements IValue {
    String s;

    public VString(String s0) {
        s = s0;
    }

    public String getval() {
        return s;
    }

    public String toStr() {
        return "\"" + s + "\"";
    }
}