/**
 * Package:     truco
 * Class:       Jogador
 * Descrição:   Representa um jogador básico.
 * Matéria:     Programação de Computadores II
 * Autores:     Gabriel Ferreira, Lucas Rassilan, Rebeca Gaia
 */

package truco;

import baralho.Carta;
import java.util.ArrayList;

public abstract class Jogador {
    public Jogador (String nome) {
        this.nome = nome;
        this.tentos = 0;
        this.quedas = 0;
        this.pontuacaoPartida = 0;
        this.fezAPrimeira = false;
        this.cartas = new ArrayList<>();
    }
    
    protected boolean fezAPrimeira;
    protected String nome;
    protected int quedas;
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
     * Obtém a quantidade de quedas vencidas pelo jogador
     * @return quantidade de quedas
     */
    public int getQuedas () {
        return quedas;
    }

    /**
     * Altera a quantidade quedas vencidas pelo jogador
     * @param quedas nova quantidade de quedas
     */
    public void setQuedas (int quedas) {
        this.quedas = quedas;
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
     * Informa se o usuário venceu a primeira rodada
     * @return se o usuário fez a primeira
     */
    public boolean fezAPrimeira () {
        return fezAPrimeira;
    }

    /**
     * Defini se o usuário venceu a primeira rodada
     * @param fezAPrimeira resultado da primeira rodada
     */
    public void setFezAPrimeira (boolean fezAPrimeira) {
        this.fezAPrimeira = fezAPrimeira;
    }
    
    /**
     * Prepara o jogador para a próxima partida
     */
    public void preparaNovaPartida () {
        this.pontuacaoPartida = 0;
        this.fezAPrimeira = false;
    }
    
    /**
     * Adiciona uma carta a mão do jogador
     * @param c carta recebida
     */
    public void adicionaCarta (Carta c) {
        this.cartas.add(c);
    }
}
