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
}
