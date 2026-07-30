package proj.src.defeq;

public class THyp extends Tactic {
    private final String var;

    public THyp(String v) {
        var = v;
    }

    public String getHyp() {
        return var;
    }
}
