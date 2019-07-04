/**
 * Package:     baralho
 * Class:       Carta
 * Descrição:   Representa uma carta do baralho.
 * Matéria:     Programação de Computadores II
 * Autores:     Gabriel, Lucas Rassilan, Rebeca Gaia
 */

package baralho;

public class Carta {
    public enum Naipe { COPAS, OUROS, ESPADAS, PAUS };
    public enum ComparacaoCartas { IGUAIS, MAIOR, MENOR, ABORT };
    
    /**
     * Traduz o naipe da carta para uma String
     * @param n naipe da carta
     * @return nome do naipe
     */
    public static String stringifyNaipe (Naipe n) {
        switch (n) {
            case COPAS:
                return "Copas";
            case OUROS:
                return "Ouros";
            case ESPADAS:
                return "Espadas";
            case PAUS:
                return "Paus";
            default:
                return "?";
        }
    }
    
    public Carta (Naipe naipe, int valor) {
        this.naipe = naipe;
        this.valor = valor;
        this.usada = false;
    }
    
    private Naipe naipe;
    private int valor;
    private boolean usada;

    /**
     * Informa se a carta já foi jogada
     * @return indica se a carta disponível
     */
    public boolean isUsada () {
        return usada;
    }

    /**
     * Altera o estado de uso da carta
     * @param usada 
     */
    public void setUsada (boolean usada) {
        this.usada = usada;
    }

    /**
     * Obtém o naipe desta carta
     * @return naipe da carta
     */
    public Naipe getNaipe () {
        return naipe;
    }
    
    /**
     * Obtém o valor desta carta de acordo com a ordem das cartas do truco
     * @return o valor da carta
     */
    public int getValor () {
        if (this.valor == 4 && this.naipe == Naipe.PAUS)
            return 17;
        if (this.valor == 7 && this.naipe == Naipe.COPAS)
            return 16;
        if (this.valor == 1 && this.naipe == Naipe.ESPADAS)
            return 15;
        if (this.valor == 7 && this.naipe == Naipe.OUROS)
            return 14;
        if (this.valor < 4)
            return this.valor + 10;
        if (this.valor == 13)
            return 10;
        if (this.valor == 11)
            return 9;
        if (this.valor == 12)
            return 8;
        
        return this.valor;
    }
    
    /**
     * Obtém o nome desta carta de forma legível
     * @return nome da carta
     */
    public String getNome () {
        if (this.valor >= 2 && this.valor <= 10) {
            return String.valueOf(this.valor);
        } else {
            switch (this.valor) {
                case 1:
                    return "Ás";
                case 11:
                    return "Valete";
                case 12:
                    return "Rainha";
                case 13:
                    return "Rei";
                default:
                    return "?";
            }
        }
    }
    
    /**
     * Compara esta carta com outra de acordo com seus valores no truco
     * @param b carta para ser comparada
     * @return indica se esta carta é maior, menor ou igual a outra
     */
    public ComparacaoCartas comparaCartas (Carta b) {
        if (this.getValor() > b.getValor())
            return ComparacaoCartas.MAIOR;
        
        return b.getValor() > this.getValor() ? ComparacaoCartas.MENOR : ComparacaoCartas.IGUAIS;
    }
    
    /**
     * Obtém o caminho para a imagem desta carta
     * @return caminho da imagem
     */
    public String getPath () {
        return stringifyNaipe(this.naipe).toUpperCase() + "\\" + this.toString() + ".png";
    }
    
    /**
     * Traduz a carta em seu nome completo
     * @return 
     */
    @Override
    public String toString () {
        return this.getNome() + " de " + stringifyNaipe(this.naipe);
    }
}
