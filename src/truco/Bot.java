/**
 * Package:     truco
 * Class:       Bot
 * Descrição:   Representa o jogador computador.
 * Matéria:     Programação de Computadores II
 * Autores:     Gabriel, Lucas Rassilan, Rebeca Gaia
 */

package truco;

import baralho.Carta;
import baralho.Carta.ComparacaoCartas;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

public class Bot extends Jogador {
    public Bot (String nome) {
        super(nome);
        this.cartaJogada = null;
        this.valorUltimaAportaFeita = null;
        this.random = new SecureRandom();
    }
    
    private SecureRandom random;
    
    public Carta cartaJogada;
    public Truco.ValoresPartida valorUltimaAportaFeita;

    public void fazJogadaAleatoria () {
        Random gerador = new Random();
        Carta carta = null;
        
        do {
            // Continua escolhendo uma carta aleatória enquanto a carta escolhida já tiver sido usada
            carta = cartas.get(gerador.nextInt(3));
        } while (carta.isUsada());
        
        cartaJogada = carta;
        cartaJogada.setUsada(true);
    }
    
    // Lógica de escolha da carta do bot (2ª versão)
    public void fazJogada (Carta jogadaOposta, boolean coberta, boolean maoDaMaior) {
        Carta carta = null;
        
        if (maoDaMaior) {
            // Joga a maior
            for (int i = 0; i < 3; i++) {
                if (!cartas.get(i).isUsada() && (carta == null || cartas.get(i).comparaCartas(carta) == ComparacaoCartas.MAIOR)) {
                    carta = cartas.get(i);
                }
            }
        }
        
        if (!coberta && carta == null) {
            if (jogadaOposta != null) {
                // Joga a menor que for maior que jogadaOposta
                ArrayList<Carta> cartasValidas = new ArrayList<>();
                
                // Separa as cartas que são maiores que jogadaOposta
                for (int i = 0; i < 3; i++) {
                    if (!cartas.get(i).isUsada() && (cartas.get(i).comparaCartas(jogadaOposta) == ComparacaoCartas.MAIOR)) {
                        cartasValidas.add(cartas.get(i));
                    }
                }
                
                // Joga a menor carta, dentre as selecionadas
                for (int i = 0; i < cartasValidas.size(); i++) {
                    if (carta == null || cartasValidas.get(i).comparaCartas(carta) == ComparacaoCartas.MENOR) {
                        carta = cartasValidas.get(i);
                    }
                }
            } else {
                // Joga a maior
                for (int i = 0; i < 3; i++) {
                    if (!cartas.get(i).isUsada() && (carta == null || cartas.get(i).comparaCartas(carta) == ComparacaoCartas.MAIOR)) {
                        carta = cartas.get(i);
                    }
                }
            }
        }
        
        if (carta == null) {
            // Joga a menor
            for (int i = 0; i < 3; i++) {
                if (!cartas.get(i).isUsada() && (carta == null || cartas.get(i).comparaCartas(carta) == ComparacaoCartas.MENOR)) {
                    carta = cartas.get(i);
                }
            }
        }
        
        cartaJogada = carta;
        cartaJogada.setUsada(true);
    }
    
    // Lógica de aposta do bot (2ª versão)
    public boolean pedeTruco (boolean respondendo) {
        boolean mentir = random.nextDouble() < 0.22;
        boolean atenuacao = respondendo ? random.nextDouble() < 0.65 : random.nextDouble() < 0.85;
        boolean vitoriaQuaseGarantida = false;
        
        // ------------------------------------------------------------
        // Início da área que deve ser alterada
        
        int somaValor = 0;
        for (int i = 0; i < 3; i++) {
            if (!cartas.get(i).isUsada()) {
                somaValor = somaValor + cartas.get(i).getValor();
            }
        }
        
        boolean verdade = somaValor > 25;
        
        // Fim da área que deve ser alterada
        // ------------------------------------------------------------
        
        return vitoriaQuaseGarantida || (mentir && !respondendo) || (verdade && atenuacao);
    }
    
    public boolean pedeTruco () {
        return pedeTruco(false);
    }
    
    public boolean respondeTruco () {
        return pedeTruco(true);
    }
}
