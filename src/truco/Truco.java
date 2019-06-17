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
    public enum ValoresPartida { REGULAR, TRUCO, SEIS, NOVE, DOZE }
    
    private Baralho baralho;
    private ValoresPartida valorPartida;
    
    public JogadorManual jogador;
    public Bot bot;
    
    public Truco () {
        valorPartida = ValoresPartida.REGULAR;
        baralho = new Baralho();
        jogador = new JogadorManual("Jogador");
        bot = new Bot("CPU");
    }
    
    private void verificaMaior () {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void jogar () {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Obtém o valor atual da aposta da partida
     * @return valor da partida
     */
    public ValoresPartida getValorPartida () {
        return this.valorPartida;
    }
    
    /**
     * Aumenta a aposta da partida, ou seja, pede Truco, Seis, Nove ou Doze
     */
    public void subirAposta () {
        if (this.valorPartida != ValoresPartida.DOZE)
            this.valorPartida = ValoresPartida.values()[this.valorPartida.ordinal() + 1];
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
}