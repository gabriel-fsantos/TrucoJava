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
import javax.swing.JOptionPane;
import principal.TrucoFrame;

public class Truco {
    public enum ValoresPartida { REGULAR, TRUCO, SEIS, NOVE, DOZE }
    
    private final Baralho baralho;
    private final TrucoFrame trucoFrame;
    
    private int qtdRodadasEmpatadas;
    private boolean vezDoUsuario;
    private boolean vezDoUsuarioTravada;
    private boolean usuarioEProximoNaRodada;
    private boolean usuarioComecouNaPartidaAnterior;
    private ValoresPartida valorPartida;
    
    public boolean eMaoDaMaior;
    public JogadorManual usuario;
    public Bot bot;
    
    public Truco (TrucoFrame trucoFrame) {
        this.trucoFrame = trucoFrame;
        
        usuarioComecouNaPartidaAnterior = false;
        valorPartida = ValoresPartida.REGULAR;
        
        baralho = new Baralho();
        usuario = new JogadorManual("Jogador");
        bot = new Bot("CPU");
    }
    
    public Carta.ComparacaoCartas jogar (Carta jogadaUsuario, boolean coberta) {
        vezDoUsuario = false;
        
        // Se for a vez do bot, joga antes de comparar as cartas
        if (bot.cartaJogada == null) {
            String message = jogaBot(jogadaUsuario);

            // Se o bot pediu truco, mostra resposta do usuário
            if (message.length() > 0) {
                trucoFrame.showInfo(message, 3000);

                // Se o usuário tiver recusado o truco
                if (message.contains("correu"))
                    return Carta.ComparacaoCartas.ABORT;

                // Mostra o botão de aumentar a aposta, se a aposta ainda puder ser ampliada
                if (valorPartida != ValoresPartida.DOZE) {
                    trucoFrame.trucoBtn.setVisible(true);
                    trucoFrame.trucoBtn.setText(String.valueOf(getProximoValor()) + "!");
                }
            }
        }
        
        return !coberta ? jogadaUsuario.comparaCartas(bot.cartaJogada) : Carta.ComparacaoCartas.MENOR;
    }
    
    public boolean eVezDoJogador () {
        return vezDoUsuario;
    }
    
    public boolean vezDoJogadorTravada () {
        return vezDoUsuarioTravada;
    }
    
    public void travaVezDoJogador () {
        vezDoUsuarioTravada = true;
    }
    
    public void liberaVezDoJogador () {
        vezDoUsuarioTravada = false;
    }
    
    public boolean usuarioEProximo () {
        return usuarioEProximoNaRodada;
    }
    
    public String jogaUsuario (int cartaIdx) {
        usuario.cartas.get(cartaIdx).setUsada(true);
        
        // Realiza a jogada do bot ou valida a rodada
        Carta.ComparacaoCartas resultadoRodada = jogar(usuario.cartas.get(cartaIdx), usuario.jogarDeCoberta);
        
        String message = "";
        switch (resultadoRodada) {
            case IGUAIS:
                eMaoDaMaior = true;
                usuarioEProximoNaRodada = !usuarioEProximoNaRodada;
                message = "Jogada cangada!";
                break;
            case MAIOR:
                usuarioEProximoNaRodada = true;
                message = "Você fez a rodada!";
                usuario.venceuRodada();
                
                // Se for a primeira rodada, salva a informação de que o usuário
                // venceu ela para ser usado como critério de desempate
                if (!bot.fezAPrimeira() && !usuario.fezAPrimeira() && !eMaoDaMaior)
                    usuario.setFezAPrimeira(true);
                
                break;
            case MENOR:
                usuarioEProximoNaRodada = false;
                message = "Você perdeu a rodada!";
                bot.venceuRodada();
                
                // Se for a primeira rodada, salva a informação de que o bot
                // venceu ela para ser usado como critério de desempate
                if (!bot.fezAPrimeira() && !usuario.fezAPrimeira() && !eMaoDaMaior)
                    bot.setFezAPrimeira(true);
                
                break;
            case ABORT:
                message = "ABORT";
                break;
        }
        
        // Caso o usuário correu de um eventual truco do bot, não continua a partida
        if (message.equals("ABORT"))
            return message;
        
        String vencedor = validaResultadoRodada(resultadoRodada);
        
        // Se alguém venceu ou o jogo empatou
        if (vencedor.length() > 0)
            message = vencedor;
        
        return message;
    }
    
    public String validaResultadoRodada (Carta.ComparacaoCartas resultadoRodada) {
        String message = "";
        
        // Se o placar for diferente de 0 x 0 e o resultado da rodada foi empate,
        // quem fez a primeira rodada venceu por desempate
        boolean venceuPelaPrimeiraRodada = resultadoRodada == Carta.ComparacaoCartas.IGUAIS && (usuario.getPontuacaoPartidada() == 1 || bot.getPontuacaoPartidada() == 1);
        
        // Se alguém venceu
        if (bot.getPontuacaoPartidada() == 2 || usuario.getPontuacaoPartidada() == 2 || venceuPelaPrimeiraRodada) {
            // Se o usuário venceu
            if (usuario.getPontuacaoPartidada() == 2 || (venceuPelaPrimeiraRodada && usuario.fezAPrimeira())) {
                message = "Você venceu a partida!";
                adicionaTentosVencedor(usuario);
            } else {
                // Senão, o bot venceu
                message = "O Computador venceu a partida!";
                adicionaTentosVencedor(bot);
            }
        } else if (eMaoDaMaior) {
            // Empate na primeira rodada e, possivelmente em outras
            switch (qtdRodadasEmpatadas) {
                case 1:
                case 2:
                    // Trata resultado das segunda e terceira rodadas após empate na(s) anterior(es)
                    switch (resultadoRodada) {
                        case IGUAIS:
                            if (qtdRodadasEmpatadas == 2) {
                                // Empate em todas as rodadas
                                message = "Jogo empatado!";
                                trucoFrame.showInfo(message, 3000);
                            }
                            break;
                        case MAIOR:
                            message = "Você venceu a partida!";
                            adicionaTentosVencedor(usuario);
                            break;
                        case MENOR:
                            message = "O Computador venceu a partida!";
                            adicionaTentosVencedor(bot);
                            break;
                    }
                    break;
            }
            
            // Conta quantas rodadas foram empatadas
            if (resultadoRodada == Carta.ComparacaoCartas.IGUAIS)
                qtdRodadasEmpatadas++;
        }
        
        return message;
    }
    
    /**
     * Realiza a jogada do bot levando em consideração a carta jogada pelo usuário, caso haja.
     * Também faz o bot pedir truco, se ele assim decidir
     * @param jogadaUsuario carta jogada pelo usuario
     * @return 
     */
    public String jogaBot (Carta jogadaUsuario) {
        String message = "";
        
        if (usuario.getTentos() != 10 && bot.getTentos() != 10 && getValorPartida() != ValoresPartida.DOZE && bot.valorUltimaAportaFeita != this.valorPartida && bot.pedeTruco(jogadaUsuario)) {
            trucoFrame.repaint();
            int resposta = JOptionPane.showConfirmDialog(null, "Computador pediu " + getProximoValor() + ". Deseja aceitar?", "Truco", JOptionPane.YES_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                message = "Você aceitou!";
                subirAposta();
                
                // Salva o último pedido de truco do bot para garantir que ele
                // não irá pedir truco repetidamente
                bot.valorUltimaAportaFeita = this.valorPartida;
            } else {
                message = "Você correu!";
                bot.setTentos(bot.getTentos() + getValorNumericoPartida());
                
                // Para a função aqui, pois, desde que o bot pediu truco,
                // só faz jogada se o usuário aceitar
                return message;
            }
        }
        
        // Joga cartas aleatórias em caso de mão de 10
        if (usuario.getTentos() == 10 && bot.getTentos() == 10)
            bot.fazJogadaAleatoria();
        else
            bot.fazJogada(jogadaUsuario, usuario.jogarDeCoberta, eMaoDaMaior);
        
        vezDoUsuario = true;
        return message;
    }
    
    /**
     * Realiza a jogada do bot no início de uma rodada.
     * Também faz o bot pedir truco, se ele assim decidir
     * @return 
     */
    public String jogaBot () {
        return jogaBot(null);
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
    
    public ValoresPartida getProximoValor () {
        if (this.valorPartida != ValoresPartida.DOZE)
            return ValoresPartida.values()[this.valorPartida.ordinal() + 1];
        
        return ValoresPartida.DOZE;
    }
    
    /**
     * Aumenta a aposta da partida, ou seja, pede Truco, Seis, Nove ou Doze
     */
    public void subirAposta () {
        this.valorPartida = getProximoValor();
    }
    
    public void zeraAposta (){
        this.valorPartida = ValoresPartida.REGULAR;
    }
    
    /**
     * Zera todas as variáveis de controle da rodada, embaralha e distribui cartas
     */
    public void preparaNovaPartida () {
        qtdRodadasEmpatadas = 0;
        vezDoUsuarioTravada = false;
        vezDoUsuario = !usuarioComecouNaPartidaAnterior;
        usuarioComecouNaPartidaAnterior = vezDoUsuario;
        
        usuario.preparaNovaPartida();
        bot.preparaNovaPartida();
        
        bot.valorUltimaAportaFeita = null;
        bot.cartaJogada = null;
        
        eMaoDaMaior = false;
        baralho.embaralhar();
        baralho.distribuirCartas(new Jogador[] { usuario, bot });
        zeraAposta();
    }
    
    public void preparaNovaRodada () {
        bot.cartaJogada = null;
        vezDoUsuario = true;
    }
    
    private void adicionaTentosVencedor (Jogador vencedor) {
        // Adiciona os tentos ao vencedor e caso ele tenha acumulado 12 ou mais tentos,
        // adiciona uma queda pra ele e zera os tentos dos jogadores
        int pontuacao = vencedor.getTentos() + getValorNumericoPartida();
        if (pontuacao >= 12) {
            vencedor.setQuedas(vencedor.getQuedas() + 1);
            
            bot.setTentos(0);
            usuario.setTentos(0);
            return;
        }

        vencedor.setTentos(pontuacao);
    }
}