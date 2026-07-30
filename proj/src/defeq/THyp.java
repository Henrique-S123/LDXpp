package proj.src.defeq;

public class THyp extends Tactic {
    private final String hyp;

    public THyp(String h) {
        hyp = h;
    }

    public String getHyp() {
        return hyp;
    }
}
