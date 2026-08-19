package proj.src.values;

public class VString implements IValue {
    String val;

    public VString(String s) {
        val = s;
    }

    public String getval() { return val; }

    public String toString() {
        return "\"" + val + "\"";
    }
}