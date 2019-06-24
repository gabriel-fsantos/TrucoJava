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
import java.util.ArrayList;
import java.util.Random;
import static javax.swing.UIManager.get;

public class Bot extends Jogador {
    public Bot (String nome) {
        super(nome);
        this.cartaJogada = null;
    }
    
    public Carta cartaJogada;

    // 2ª versão
    
    public void fazJogadaAleatoria(){
        Random gerador = new Random();
        Carta carta = null; 
        carta = cartas.get(gerador.nextInt(3));
        cartaJogada = carta;
        cartaJogada.setUsada(true);
    }
    
    public void fazJogada (Carta jogadaOposta, boolean coberta, boolean maoDaMaior) {
        Carta carta = null;
        
        if (maoDaMaior) {
            // Joga a maior
            for (int i = 0; i < 3; i++) {
                System.out.println(cartas.get(i) + ": " + cartas.get(i).isUsada());
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
                System.out.println(cartas.get(i) + ": " + cartas.get(i).isUsada());
                if (!cartas.get(i).isUsada() && (carta == null || cartas.get(i).comparaCartas(carta) == ComparacaoCartas.MENOR)) {
                    carta = cartas.get(i);
                }
            }
        }
        
        cartaJogada = carta;
        cartaJogada.setUsada(true);
    }
}
