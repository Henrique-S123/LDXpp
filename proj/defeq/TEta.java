package proj.defeq;

public class TEta extends Tactic {
    private final String var;

    public TEta(String v) {
        var = v;
    }

    public String getVar() {
        return var;
    }
}
