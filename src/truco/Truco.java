/**
 * Package:     truco
 * Class:       Truco
 * Descrição:   Gerenciador de fluxo do jogo e das rodadas.
 * Matéria:     Programação de Computadores II
 * Autores:     Gabriel, Lucas Rassilan, Rebeca Gaia
 */

package truco;

import baralho.Baralho;

public class Truco {
    private Baralho baralho;
    
    public JogadorManual jogador;
    public Bot bot;
    
    public Truco () {
        baralho = new Baralho();
        jogador = new JogadorManual("Jogador");
        bot = new Bot("CPU");
    }
    
    private void verificaMaior () {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Zera todas as variáveis de controle da rodada, embaralha e distribui cartas
     */
    public void preparaNovaPartida () {
        jogador.preparaNovaPartida();
        bot.preparaNovaPartida();
        
        baralho.embaralhar();
        baralho.distribuirCartas(new Jogador[] { jogador, bot });
    }
    
    public void jogar () {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}