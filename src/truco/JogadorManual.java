/**
 * Package:     truco
 * Class:       JogadorManual
 * Descrição:   Representa um jogador humano.
 * Matéria:     Programação de Computadores II
 * Autores:     Gabriel Ferreira, Lucas Rassilan, Rebeca Gaia
 */

package truco;

public class JogadorManual extends Jogador {
    public JogadorManual (String nome) {
        super(nome);
        jogarDeCoberta = false;
    }
    
    public boolean jogarDeCoberta;
}
