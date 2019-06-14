/**
 * Package:     truco
 * Class:       JogadorManual
 * Descrição:   Representa um jogador humano.
 * Matéria:     Programação de Computadores II
 * Autores:     Gabriel, Lucas Rassilan, Rebeca Gaia
 */

package truco;

import baralho.Carta;

public class JogadorManual extends Jogador {
    public JogadorManual (String nome) {
        super(nome);
        jogarDeCoberta = false;
    }
    
    public boolean jogarDeCoberta;

    @Override
    protected Carta escolheCarta () {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Carta fazJogada (Carta jogadaOposta) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private void mostraOpcoes () {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
