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

    @Override
    public Carta fazJogada (Carta jogadaOposta) {
        boolean r = false;
        if(jogadaOposta != null){
            while(r){
                for(Carta c : cartas){
                    if(c.comparaCartas(jogadaOposta)!= ComparacaoCartas.MAIOR){
                        r = true;
                        return c;
                    }
                }
                for(Carta c : cartas){
                    if(c.comparaCartas(jogadaOposta)!= ComparacaoCartas.MAIOR){
                        r = true;
                        return c;
                    }
                }
                for(Carta c : cartas){
                    if(c.comparaCartas(jogadaOposta)!= ComparacaoCartas.MAIOR){
                        r = true;
                        return c;
                    }
                }
            }
        }else{
            for(int i=0; i<cartas.size();i++){
                if(cartas.get(i).comparaCartas(cartas.get(i++))!= ComparacaoCartas.MAIOR){
                    cartaInicial = cartas.get(i);
                }
            }
        }
        return cartaInicial;        
    }
}
