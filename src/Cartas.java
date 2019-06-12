public class Cartas {
    
    private String naipes;
    private int valor;

    public Cartas() {
    }
    
    public Cartas(String naipes, int valor) {
        this.naipes = naipes;
        this.valor = valor;
    }

    public String getNaipes() {
        return naipes;
    }

    public void setNaipes(String naipes) {
        this.naipes = naipes;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
    
}
