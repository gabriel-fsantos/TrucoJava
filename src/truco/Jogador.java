/**
 * Package:     truco
 * Class:       Jogador
 * Descrição:   Representa um jogador básico.
 * Matéria:     Programação de Computadores II
 * Autores:     Gabriel, Lucas Rassilan, Rebeca Gaia
 */

package truco;

import baralho.Carta;
import java.util.ArrayList;

public abstract class Jogador {
    public Jogador (String nome) {
        this.nome = nome;
        this.tentos = 0;
        this.pontuacaoPartida = 0;
        this.cartas = new ArrayList<>();
    }
    
    protected String nome;
    protected int tentos;
    protected int pontuacaoPartida;
    public ArrayList<Carta> cartas;

    /**
     * Obtém o nome do jogador para exibição no placar
     * @return nome do jogador
     */
    public String getNome () {
        return nome;
    }

    /**
     * Obtém a quantidade de tentos acumulados no jogo pelo jogador
     * @return quantidade de tentos
     */
    public int getTentos () {
        return tentos;
    }

    /**
     * Altera a quantidade de tentos acumulados no jogo pelo jogador
     * @param tentos nova quantidade de tentos
     */
    public void setTentos (int tentos) {
        this.tentos = tentos;
    }

    /**
     * Obtém a quantidade de rodadas o jogador ganhou nessa partida
     * @return quantidade de rodadas vencidas na partida
     */
    public int getPontuacaoPartidada () {
        return pontuacaoPartida;
    }

    /**
     * Computa o ponto do jogador ao vencer a rodada
     */
    public void venceuRodada () {
        this.pontuacaoPartida++;
    }
    
    /**
     * Prepara o jogador para a próxima partida
     */
    public void preparaNovaPartida () {
        this.pontuacaoPartida = 0;
    }
    
    /**
     * Adiciona uma carta a mão do jogador
     * @param c carta recebida
     */
    public void adicionaCarta (Carta c) {
        this.cartas.add(c);
    }
    
    protected abstract Carta escolheCarta();
    
    public abstract Carta fazJogada(Carta jogadaOposta);
}
