package pt.bamer.bameropseccao.objectos;

public class OSPROD {
    public String bostamp;
    public String bistamp;
    public int qtt;
    public String ref;
    public String design;
    public String dim;
    public String mk;
    public String numlinha;

    @Override
    public String toString() {
        return "bostamp: " + bostamp + ", bistamp: " + bistamp + ", design: " + design
                + ", dim: " + dim + ", mk: " + mk
                + ", numlinha: " + numlinha + ", qtt: " + qtt + ", ref: " + ref
                ;
    }

    public OSPROD() {
    }

    public OSPROD(String bostamp, String bistamp, int qtt, String ref, String design, String dim, String mk, String numlinha) {
        this.bostamp = bostamp;
        this.bistamp = bistamp;
        this.qtt = qtt;
        this.ref = ref;
        this.design = design;
        this.dim = dim;
        this.mk = mk;
        this.numlinha = numlinha;
    }
}
