
public abstract class Jogadores {
    
    protected String nome;
    protected Cartas[] cartas;
    protected  int pontuacao;

    public Jogadores(String nome, int pontuacao) {
        this.nome = nome;
        this.pontuacao = pontuacao;
    }
    
    protected abstract Cartas escolheCarta();
    
    public abstract void recebeCartas(Cartas cartas);
    
    public abstract Cartas fazJogada(Cartas jogadaOposta);
    
}
