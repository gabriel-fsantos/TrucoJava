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

public class Bot extends Jogador {
    private Carta cartaInicial;

    public Bot (String nome) {
        super(nome);
    }

    // 1ª versão : Sempre joga a maior carta, quando houver
    @Override
    public Carta fazJogada (Carta jogadaOposta) {
        if(jogadaOposta != null){
                for(Carta c : cartas){
                    if(c.comparaCartas(jogadaOposta)== ComparacaoCartas.MAIOR){
                        return c;
                    }
                }
                for(Carta c : cartas){
                    if(c.comparaCartas(jogadaOposta)== ComparacaoCartas.IGUAIS){
                        return c;
                    }
                }
                for(Carta c : cartas){
                    if(c.comparaCartas(jogadaOposta)== ComparacaoCartas.MENOR){
                        return c;
                    }
                
            }
        }else{ 
            for(int i=0; i<=(cartas.size()-2);i++){
                if(cartas.get(i).comparaCartas(cartas.get(i+1))== ComparacaoCartas.MAIOR){
                    cartaInicial = cartas.get(i);
                }
            }
            return cartaInicial; 
        }            
        return null;
    }
}
