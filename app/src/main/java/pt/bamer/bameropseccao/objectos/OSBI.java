package pt.bamer.bameropseccao.objectos;

@SuppressWarnings("unused")
public class OSBI {
    public String design;
    public String dim;
    public String familia;
    public String mk;
    public int qtt;
    public String ref;
    public String tipo;
    public String bostamp;
    public String bistamp;

    public OSBI(String ref, String design, int qtt, String dim, String mk) {
        this.ref = ref;
        this.design = design;
        this.qtt = qtt;
        this.dim = dim;
        this.mk = mk;
    }

    public OSBI() {

    }
}
