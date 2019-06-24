/**
 * Package:     truco
 * Class:       Truco
 * Descrição:   Gerenciador de fluxo do jogo e das rodadas.
 * Matéria:     Programação de Computadores II
 * Autores:     Gabriel, Lucas Rassilan, Rebeca Gaia
 */

package truco;

import baralho.Baralho;
import baralho.Carta;

public class Truco {
    public enum ValoresPartida { REGULAR, TRUCO, SEIS, NOVE, DOZE }
    
    private boolean usuarioComecouNaPartidaAnterior;
    private boolean vezDoUsuario;
    private Baralho baralho;
    private ValoresPartida valorPartida;
    
    public boolean eMaoDaMaior;
    public JogadorManual usuario;
    public Bot bot;
    
    public Truco () {
        usuarioComecouNaPartidaAnterior = false;
        valorPartida = ValoresPartida.REGULAR;
        baralho = new Baralho();
        usuario = new JogadorManual("Jogador");
        bot = new Bot("CPU");
    }
    
    public Carta.ComparacaoCartas jogar (Carta jogadaUsuario, boolean coberta) {
        vezDoUsuario = false;
        if (bot.cartaJogada == null)
            bot.fazJogada(jogadaUsuario, coberta, eMaoDaMaior);
        
        return !coberta ? jogadaUsuario.comparaCartas(bot.cartaJogada) : Carta.ComparacaoCartas.MENOR;
    }
    
    public boolean eVezDoJogador () {
        return vezDoUsuario;
    }
    
    public void botJogou () {
        vezDoUsuario = true;
    }
    
    /**
     * Obtém o valor atual da aposta da partida
     * @return valor da partida
     */
    public ValoresPartida getValorPartida () {
        return this.valorPartida;
    }
    
    public int getValorNumericoPartida () {
        switch (this.valorPartida) {
            case DOZE:
                return 12;
            case NOVE:
                return 10;
            case SEIS:
                return 8;
            case TRUCO:
                return 4;
            default:
                return 2;
        }
    }
    
    /**
     * Aumenta a aposta da partida, ou seja, pede Truco, Seis, Nove ou Doze
     */
    public void subirAposta () {
        if (this.valorPartida != ValoresPartida.DOZE)
            this.valorPartida = ValoresPartida.values()[this.valorPartida.ordinal() + 1];
    }
    
    public void zeraAposta (){
        this.valorPartida = ValoresPartida.REGULAR;
    }
    
    /**
     * Zera todas as variáveis de controle da rodada, embaralha e distribui cartas
     */
    public void preparaNovaPartida () {
        vezDoUsuario = !usuarioComecouNaPartidaAnterior;
        usuarioComecouNaPartidaAnterior = vezDoUsuario;
        
        usuario.preparaNovaPartida();
        bot.preparaNovaPartida();
        bot.cartaJogada = null;
        
        eMaoDaMaior = false;
        baralho.embaralhar();
        baralho.distribuirCartas(new Jogador[] { usuario, bot });
    }
    
    public void preparaNovaRodada () {
        bot.cartaJogada = null;
        vezDoUsuario = true;
    }
}