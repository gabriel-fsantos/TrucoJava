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
    
    private final SecureRandom random;
    private boolean trucouNestaPartida;
    
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
    
    /**
     * Lógica de aposta do bot (3ª versão)
     * @param respondendo informa se o bot está respondendo a um pedido de truco do usuário
     * @param jogadaUsuario carta que o usuário jogou, caso ele tenha começado a rodada
     * @return 
     */
    public boolean pedeTruco (boolean respondendo, Carta jogadaUsuario) {
        boolean mentir = random.nextDouble() < 0.13;
        boolean atenuacao = random.nextDouble() < 0.85;
        boolean vitoriaGarantida = false;
        boolean posicaoFavoravel = false;
        
        // Se trucou nesta partida, insiste no truco
        if (trucouNestaPartida)
            atenuacao = true;
        
        int dois = 0;
        int tres = 0;
        int manilhas = 0;
        int reiOuAs = 0;
        boolean seteCopas = false;
        boolean zap = false;

        // Identifica cartas de interesse dentre as 3 distribuídas e a
        // que o bot eventualmente jogou nesta rodada
        for (int i = 0; i < 4; i++) {
            Carta carta = i < 3 ? cartas.get(i) : cartaJogada;
            
            // OBS: cartaJogada já foi usada, mas não pode ser ignorada
            // na tomada de decisão, pois pertence a esta jogada
            if (carta == null || (carta.isUsada() && i < 3)) continue;

            if (carta.getValor() >= 14)
                manilhas++;
            else if (carta.getValor() == 13)
                tres++;
            else if (carta.getValor() == 12)
                dois++;
            else if (carta.getValor() >= 10)
                reiOuAs++;
            
            if (carta.getValor() == 16)
                seteCopas = true;
            else if (carta.getValor() == 17)
                zap = true;
        }
        
        // Se tem carta atual, ignora ela na contagem de rodadas,
        // pois ela é uma rodada em andamento
        int rodada = cartaJogada == null ? 1 : 0;
        
        // Identifica a rodada atual
        for (int i = 0; i < 3; i++) {
            if (cartas.get(i).isUsada())
                rodada++;
        }
        
        switch (rodada) {
            case 1:
                // Lógica da primeira rodada
                vitoriaGarantida = manilhas == 3;
                posicaoFavoravel = manilhas >= 2 || (manilhas == 1 && (dois + tres >= 1 || trucouNestaPartida)) || (tres >= 2 && reiOuAs > 0) || (tres + dois >= 2 && trucouNestaPartida);
                break;
            case 2:
                // Lógica da segunda rodada
                boolean cartaMaiorOuIgual = false;
                if (jogadaUsuario != null) {
                    for (int i = 0; i < 4 && !cartaMaiorOuIgual; i++) {
                        Carta carta = i < 3 ? cartas.get(i) : cartaJogada;
                        if (carta != null && (!carta.isUsada() || i == 3))
                            cartaMaiorOuIgual = carta.comparaCartas(jogadaUsuario) != ComparacaoCartas.MENOR;
                    }
                }
                
                // Garantia de 90% de vitória aqui, mas vale a pena trucar
                vitoriaGarantida = this.fezAPrimeira() && (cartaMaiorOuIgual || zap || seteCopas || manilhas > 1);
                posicaoFavoravel = (this.fezAPrimeira() && manilhas + tres >= 1) || manilhas + tres + dois == 2;
                break;
            case 3:
                // Lógica da última rodada
                for (int i = 0; i < 4; i++) {
                    Carta carta = i < 3 ? cartas.get(i) : cartaJogada;
                    if (carta != null && (!carta.isUsada() || i == 3)) {
                        boolean cartaAlta = carta.getValor() >= 12;
                        boolean cartaMaior = jogadaUsuario != null && carta.comparaCartas(jogadaUsuario) == ComparacaoCartas.MAIOR;
                        boolean ganhaPelaPrimeira = jogadaUsuario != null && carta.comparaCartas(jogadaUsuario) == ComparacaoCartas.IGUAIS && this.fezAPrimeira();
                        
                        if (cartaMaior || ganhaPelaPrimeira) {
                            vitoriaGarantida = true;
                            break;
                        } else if (cartaAlta) {
                            posicaoFavoravel = true;
                            break;
                        }
                    }
                }
                break;
        }
        
        //System.out.println(vitoriaGarantida + " || (" + mentir + " && !" + respondendo + ") || (" + posicaoFavoravel + " && " + atenuacao + ")");
        boolean resposta = vitoriaGarantida || (mentir && !respondendo) || (posicaoFavoravel && atenuacao);
        
        if (resposta && !respondendo)
            trucouNestaPartida = true;
        
        return resposta;
    }
    
    public boolean pedeTruco (Carta jogadaUsuario) {
        return pedeTruco(false, jogadaUsuario);
    }
    
    public boolean respondeTruco () {
        return pedeTruco(true, null);
    }
    
    @Override
    public void preparaNovaPartida () {
        super.preparaNovaPartida();
        trucouNestaPartida = false;
    }
}
