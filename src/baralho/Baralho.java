/**
 * Package:     baralho
 * Class:       Baralho
 * Descrição:   Representa um baralho de cartas.
 * Matéria:     Programação de Computadores II
 * Autores:     Gabriel, Lucas Rassilan, Rebeca Gaia
 */

package baralho;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import truco.Jogador;

public class Baralho {
    public Baralho () {
        this.cartas = new ArrayList<>();
        
        for (int i = 0; i < 4; i++) {
            for (int j = 1; j < 14; j++) {
                if (j >= 8 && j <= 10) continue; // Truco não tem as cartas 8, 9 e 10.
                this.cartas.add(new Carta(Carta.Naipe.values()[i], j));
            }
        }
        
        this.embaralhar();
    }
    
    private int proximaCarta;
    private ArrayList<Carta> cartas;
    
    /**
     * Embaralha as cartas e configura a distribuição para começar do topo do baralho
     */
    public void embaralhar (){
        this.proximaCarta = 0;
        SecureRandom r = new SecureRandom();
        for (int i = 0; i <= r.nextInt(5); i++)
            Collections.shuffle(this.cartas, r);
    }
    
    /**
     * Distribui cartas para os jogadores
     * @param jogadores 
     */
    public void distribuirCartas (Jogador[] jogadores) {
        for (Jogador j : jogadores) {
            for (int i = 0; i < 3; i++)
                j.adicionaCarta(this.cartas.get(this.proximaCarta++));
        }
    }
}
