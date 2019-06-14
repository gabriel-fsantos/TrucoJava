/**
 * Package:     truco
 * Class:       Bot
 * Descrição:   Representa o jogador computador.
 * Matéria:     Programação de Computadores II
 * Autores:     Gabriel, Lucas Rassilan, Rebeca Gaia
 */

package truco;

import baralho.Carta;

public class Bot extends Jogador {
    public Bot (String nome) {
        super(nome);
    }

    @Override
    protected Carta escolheCarta () {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Carta fazJogada (Carta jogadaOposta) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
