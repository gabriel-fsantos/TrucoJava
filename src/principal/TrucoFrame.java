/**
 * Package:     principal
 * Class:       TrucoFrame
 * Descrição:   Tela do jogo com todos os elementos visuais e interação com o usuário.
 * Matéria:     Programação de Computadores II
 * Autores:     Gabriel, Lucas Rassilan, Rebeca Gaia
 */

package principal;

import baralho.Carta;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import truco.Truco;
import truco.Truco.ValoresPartida;

public class TrucoFrame extends javax.swing.JFrame {
    /**
     * Carrega uma imagem para mostra-la em um label
     * @param path caminho para a imagem
     * @param label componente que irá mostrar a imagem
     */
    public static void displayImage (String path, JLabel label) {
        try {
            BufferedImage img = ImageIO.read(new File("Images\\" + path));
            Image dimg = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(dimg);
            label.setIcon(imageIcon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates new form TrucoFrame
     */
    public TrucoFrame() {
        initComponents();
        truco = new Truco();
        
        background = new JLabel();
        background.setLocation(0, 0);
        background.setSize(this.getWidth(), this.getHeight());
        this.add(background);
        
        TrucoFrame.displayImage("Background.jpg", background);
        initializeGame();
    }
    
    /**
     * Inicializa um novo jogo
     */
    public void initializeGame () {
        hideLabel(cartaAtualCPU);
        hideLabel(cartaAtualJogador);
        
        // Para esconder os botões utilizamos setVisible
        aceitarBtn.setVisible(false);
        correrBtn.setVisible(false);
        
        updateScore();
        
        truco.usuario.jogarDeCoberta = false;
        cobertaBtn.setBackground(new Color(141, 27, 36));
        cobertaBtn.setBorder(new LineBorder(new Color(102, 0, 0)));
        
        truco.preparaNovaPartida();
        if(truco.usuario.getTentos()==10 && truco.bot.getTentos()==10){
            TrucoFrame.displayImage("Verso.png", cartaJogador1);
            TrucoFrame.displayImage("Verso.png", cartaJogador2);
            TrucoFrame.displayImage("Verso.png", cartaJogador3);
        }
        else{
            TrucoFrame.displayImage(truco.usuario.cartas.get(0).getPath(), cartaJogador1);
            TrucoFrame.displayImage(truco.usuario.cartas.get(1).getPath(), cartaJogador2);
            TrucoFrame.displayImage(truco.usuario.cartas.get(2).getPath(), cartaJogador3);
        }
        
        // DEBUG_ONLY
        //for (Carta c : truco.bot.cartas)
        //    System.out.println(c);
        
        if (!truco.eVezDoJogador()) {
            if(truco.usuario.getTentos()==10 && truco.bot.getTentos()==10){
                truco.bot.fazJogadaAleatoria();
            }
            else{
                truco.bot.fazJogada(null, false, truco.eMaoDaMaior);
            }
            TrucoFrame.displayImage(truco.bot.cartaJogada.getPath(), cartaAtualCPU);
            truco.botJogou();
        }
        truco.zeraAposta();
        trucoBtn.setVisible(true);
        showInfo("É a sua vez.", 2000);
        repaint();
    }
    
    /**
     * Atualiza os campos que mostram ao usuário os tentos de cada usuario
     */
    public void updateScore () {
        placarJogador.setText(truco.usuario.getNome() + ": " + truco.usuario.getTentos() + " tentos (" + truco.usuario.getQuedas()+ " quedas)");
        placarCPU.setText(truco.bot.getNome() + ": " + truco.bot.getTentos() + " tentos (" + truco.bot.getQuedas() + " quedas)");
    }
    
    /**
     * Esconde um componente sem remove-lo do layout
     * @param label componente a ser escondido
     */
    public void hideLabel (JLabel label) {
        label.setIcon(null);
        label.setBackground(new Color(0, 0, 0, 0));
        label.setForeground(new Color(0, 0, 0, 0));
    }
    
    /**
     * Mostra uma mensagem ao usuário sobre o decorrer da partida
     * @param text mensagem a ser exibida
     * @param duration por quantos milissegundos a mensagem será exibida
     */
    public void showInfo (String text, int duration) {
        info.setText(text);
        repaint();
        
        ScheduledThreadPoolExecutor waitThread = new ScheduledThreadPoolExecutor(1);
        waitThread.schedule(() -> { info.setText(""); }, duration, TimeUnit.MILLISECONDS);
    }
    
    public void jogaCarta (int n) {
        if (truco.usuario.cartas.get(n).isUsada() || !truco.eVezDoJogador()) return;
        
        if (!truco.usuario.jogarDeCoberta)
            TrucoFrame.displayImage(truco.usuario.cartas.get(n).getPath(), cartaAtualJogador);
        else
            TrucoFrame.displayImage("Verso.png", cartaAtualJogador);
        
        truco.usuario.cartas.get(n).setUsada(true);
        this.hideLabel(n == 0 ? cartaJogador1 : (n == 1 ? cartaJogador2 : cartaJogador3));
        
        // Realiza a jogada do bot ou valida a rodada
        Carta.ComparacaoCartas resultadoRodada = truco.jogar(truco.usuario.cartas.get(n), truco.usuario.jogarDeCoberta);
        if (resultadoRodada == Carta.ComparacaoCartas.IGUAIS)
            truco.eMaoDaMaior = true;
        
        // Garante exibição da carta do computador
        TrucoFrame.displayImage(truco.bot.cartaJogada.getPath(), cartaAtualCPU);
        
        // Desativa coberta, caso esteja ativada
        if (truco.usuario.jogarDeCoberta)
            cobertaBtnMouseClicked(null);
        
        repaint();
        switch (resultadoRodada) {
            case IGUAIS:
                usuarioEProximo = !usuarioEProximo;
                showInfo("Jogada cangada!", 2000);
                break;
            case MAIOR:
                usuarioEProximo = true;
                showInfo("Você fez a rodada!", 2000);
                truco.usuario.venceuRodada();
                if (!truco.bot.fezAPrimeira() && !truco.usuario.fezAPrimeira())
                    truco.usuario.setFezAPrimeira(true);
                
                break;
            case MENOR:
                usuarioEProximo = false;
                showInfo("Você perdeu a rodada!", 2000);
                truco.bot.venceuRodada();
                if (!truco.bot.fezAPrimeira() && !truco.usuario.fezAPrimeira())
                    truco.bot.setFezAPrimeira(true);
                
                break;
        }
        
        preparaNovaRodada(resultadoRodada);
    }
    
    private void preparaNovaRodada (Carta.ComparacaoCartas resultadoRodada) {
        if(truco.bot.pedeTruco() && this.truco.getValorPartida() != ValoresPartida.DOZE){
            int resposta = JOptionPane.showConfirmDialog(null, "Computador pediu TRUCO. Deseja aceitar?", "Truco", JOptionPane.YES_OPTION);
            if (resposta == JOptionPane.YES_OPTION) 
                aumentaAposta();
        }
        boolean venceuPelaPrimeiraRodada = resultadoRodada == Carta.ComparacaoCartas.IGUAIS && (truco.usuario.getPontuacaoPartidada() == 1 || truco.bot.getPontuacaoPartidada() == 1);
        if (truco.bot.getPontuacaoPartidada() == 2 || truco.usuario.getPontuacaoPartidada() == 2 || venceuPelaPrimeiraRodada) {
            if (truco.usuario.getPontuacaoPartidada() == 2 || (venceuPelaPrimeiraRodada && truco.usuario.fezAPrimeira())) {
                showInfo("Você venceu a partida!", 3000);

                int pontuacao = truco.usuario.getTentos() + truco.getValorNumericoPartida();
                if (pontuacao >= 12) {
                    pontuacao = 0;
                    truco.usuario.setQuedas(truco.usuario.getQuedas() + 1);
                    truco.bot.setTentos(0);
                }

                truco.usuario.setTentos(pontuacao);
            } else {
                showInfo("O Computador venceu a partida!", 3000);
                int pontuacao = truco.bot.getTentos() + truco.getValorNumericoPartida();
                if (pontuacao >= 12) {
                    pontuacao = 0;
                    truco.bot.setQuedas(truco.usuario.getQuedas() + 1);
                    truco.usuario.setTentos(0);
                }

                truco.bot.setTentos(pontuacao);
            }
            
            repaint();
            ScheduledThreadPoolExecutor wait = new ScheduledThreadPoolExecutor(1);
            wait.schedule(() -> initializeGame(), 3000, TimeUnit.MILLISECONDS);
            trucoBtn.setText(String.valueOf(ValoresPartida.TRUCO)+"!");
            
        } else {
            ScheduledThreadPoolExecutor waitThread = new ScheduledThreadPoolExecutor(1);
            waitThread.schedule(() -> {
                hideLabel(cartaAtualCPU);
                hideLabel(cartaAtualJogador);

                truco.preparaNovaRodada();
                if (!usuarioEProximo) {
                    truco.bot.fazJogada(null, false, truco.eMaoDaMaior);
                    TrucoFrame.displayImage(truco.bot.cartaJogada.getPath(), cartaAtualCPU);
                }
                
                repaint();
            }, 3000, TimeUnit.MILLISECONDS);
        }  
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titulo = new javax.swing.JLabel();
        cartaJogador2 = new javax.swing.JLabel();
        cartaJogador3 = new javax.swing.JLabel();
        cartaJogador1 = new javax.swing.JLabel();
        cartaAtualJogador = new javax.swing.JLabel();
        cartaAtualCPU = new javax.swing.JLabel();
        placarJogador = new javax.swing.JLabel();
        placarCPU = new javax.swing.JLabel();
        trucoBtn = new javax.swing.JLabel();
        correrBtn = new javax.swing.JLabel();
        cobertaBtn = new javax.swing.JLabel();
        aceitarBtn = new javax.swing.JLabel();
        info = new javax.swing.JLabel();
        espacoLayoutEsquerdo = new javax.swing.JLabel();
        espacoLayoutDireito = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Truco");
        setMaximumSize(new java.awt.Dimension(700, 530));
        setMinimumSize(new java.awt.Dimension(700, 530));
        setResizable(false);

        titulo.setFont(new java.awt.Font("Roboto Lt", 0, 48)); // NOI18N
        titulo.setForeground(new java.awt.Color(255, 255, 255));
        titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titulo.setText("TRUCO");

        cartaJogador2.setBackground(new java.awt.Color(255, 102, 102));
        cartaJogador2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        cartaJogador2.setOpaque(true);
        cartaJogador2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cartaJogador2MouseClicked(evt);
            }
        });

        cartaJogador3.setBackground(new java.awt.Color(255, 102, 102));
        cartaJogador3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        cartaJogador3.setOpaque(true);
        cartaJogador3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cartaJogador3MouseClicked(evt);
            }
        });

        cartaJogador1.setBackground(new java.awt.Color(255, 102, 102));
        cartaJogador1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        cartaJogador1.setOpaque(true);
        cartaJogador1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cartaJogador1MouseClicked(evt);
            }
        });

        cartaAtualJogador.setBackground(new java.awt.Color(255, 102, 102));
        cartaAtualJogador.setOpaque(true);

        cartaAtualCPU.setBackground(new java.awt.Color(255, 102, 102));
        cartaAtualCPU.setOpaque(true);

        placarJogador.setFont(new java.awt.Font("Roboto Lt", 0, 18)); // NOI18N
        placarJogador.setForeground(new java.awt.Color(255, 255, 255));
        placarJogador.setText("Jogador: 0 tentos (0 quedas)");

        placarCPU.setFont(new java.awt.Font("Roboto Lt", 0, 18)); // NOI18N
        placarCPU.setForeground(new java.awt.Color(255, 255, 255));
        placarCPU.setText("CPU: 0 tentos (0 quedas)");

        trucoBtn.setBackground(new java.awt.Color(141, 27, 36));
        trucoBtn.setFont(new java.awt.Font("Roboto Lt", 0, 16)); // NOI18N
        trucoBtn.setForeground(new java.awt.Color(255, 255, 255));
        trucoBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        trucoBtn.setText("TRUCO!");
        trucoBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 0, 0)));
        trucoBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        trucoBtn.setOpaque(true);
        trucoBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                trucoBtnMouseClicked(evt);
            }
        });

        correrBtn.setBackground(new java.awt.Color(141, 27, 36));
        correrBtn.setFont(new java.awt.Font("Roboto Lt", 0, 16)); // NOI18N
        correrBtn.setForeground(new java.awt.Color(255, 255, 255));
        correrBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        correrBtn.setText("CORRER!");
        correrBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 0, 0)));
        correrBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        correrBtn.setOpaque(true);

        cobertaBtn.setBackground(new java.awt.Color(141, 27, 36));
        cobertaBtn.setFont(new java.awt.Font("Roboto Lt", 0, 16)); // NOI18N
        cobertaBtn.setForeground(new java.awt.Color(255, 255, 255));
        cobertaBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cobertaBtn.setText("COBERTA");
        cobertaBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 0, 0)));
        cobertaBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        cobertaBtn.setOpaque(true);
        cobertaBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cobertaBtnMouseClicked(evt);
            }
        });

        aceitarBtn.setBackground(new java.awt.Color(141, 27, 36));
        aceitarBtn.setFont(new java.awt.Font("Roboto Lt", 0, 16)); // NOI18N
        aceitarBtn.setForeground(new java.awt.Color(255, 255, 255));
        aceitarBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        aceitarBtn.setText("ACEITAR!");
        aceitarBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 0, 0)));
        aceitarBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        aceitarBtn.setOpaque(true);

        info.setFont(new java.awt.Font("Roboto Lt", 0, 42)); // NOI18N
        info.setForeground(new java.awt.Color(255, 255, 255));
        info.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        info.setText("Você venceu!");

        espacoLayoutEsquerdo.setBackground(new java.awt.Color(141, 27, 36));
        espacoLayoutEsquerdo.setFont(new java.awt.Font("Roboto Lt", 0, 16)); // NOI18N
        espacoLayoutEsquerdo.setForeground(new java.awt.Color(255, 255, 255));
        espacoLayoutEsquerdo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        espacoLayoutEsquerdo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        espacoLayoutDireito.setBackground(new java.awt.Color(141, 27, 36));
        espacoLayoutDireito.setFont(new java.awt.Font("Roboto Lt", 0, 16)); // NOI18N
        espacoLayoutDireito.setForeground(new java.awt.Color(255, 255, 255));
        espacoLayoutDireito.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        espacoLayoutDireito.setToolTipText("");
        espacoLayoutDireito.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(placarCPU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(placarJogador, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(titulo)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(trucoBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(correrBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(aceitarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(espacoLayoutEsquerdo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(64, 64, 64)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cartaAtualJogador, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cartaJogador1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50)
                                .addComponent(cartaJogador2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cartaAtualCPU, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addComponent(cartaJogador3, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cobertaBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(espacoLayoutDireito, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(info, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titulo)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(placarJogador)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(placarCPU)))
                .addGap(14, 14, 14)
                .addComponent(cartaAtualCPU, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(info, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cartaAtualJogador, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(cartaJogador1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cartaJogador3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cartaJogador2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(espacoLayoutEsquerdo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aceitarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(correrBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trucoBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(espacoLayoutDireito, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cobertaBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(11, 11, 11))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Toggle para o usuário definir se quer jogar a carta de coberta ou não
     * @param evt 
     */
    
    //Método usado para aumentar a aposta pelo jogador ou pelo bot
    private void aumentaAposta(){                                      
        this.truco.subirAposta();
        showInfo(this.truco.getValorPartida() + "!", 3000);
        if(this.truco.getValorPartida() == ValoresPartida.DOZE)
            trucoBtn.setVisible(false);
        else
            trucoBtn.setText(String.valueOf(ValoresPartida.values()[this.truco.getValorPartida().ordinal() + 1])+"!");              
    }   
    
    private void cobertaBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cobertaBtnMouseClicked
        truco.usuario.jogarDeCoberta = !truco.usuario.jogarDeCoberta;
        if (!truco.usuario.jogarDeCoberta) {
            cobertaBtn.setBackground(new Color(141, 27, 36));
            cobertaBtn.setBorder(new LineBorder(new Color(102, 0, 0)));
        } else {
            cobertaBtn.setBackground(new Color(191, 67, 76));
            cobertaBtn.setBorder(new LineBorder(new Color(142, 20, 20)));
        }
    }//GEN-LAST:event_cobertaBtnMouseClicked

    /**
     * Aumenta a aposta do usuário, ou seja, pede Truco, Seis, Nove ou Doze
     * @param evt 
     */
    private void trucoBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_trucoBtnMouseClicked
        //método usado para aumentar a aposta pelo jogador ou pelo bot
        if(truco.bot.respondeTruco()){
            aumentaAposta();
        }else{
            JOptionPane.showMessageDialog(null, "Bot não aceitou o truco");
            trucoBtn.setVisible(false);
        }      
    }//GEN-LAST:event_trucoBtnMouseClicked

    private void cartaJogador2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cartaJogador2MouseClicked
        jogaCarta(1);
    }//GEN-LAST:event_cartaJogador2MouseClicked

    private void cartaJogador1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cartaJogador1MouseClicked
        jogaCarta(0);
    }//GEN-LAST:event_cartaJogador1MouseClicked

    private void cartaJogador3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cartaJogador3MouseClicked
        jogaCarta(2);
    }//GEN-LAST:event_cartaJogador3MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TrucoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TrucoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TrucoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TrucoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TrucoFrame().setVisible(true);
            }
        });
    }
    
    private boolean usuarioEProximo;
    private Truco truco;
    private javax.swing.JLabel background;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel aceitarBtn;
    private javax.swing.JLabel cartaAtualCPU;
    private javax.swing.JLabel cartaAtualJogador;
    private javax.swing.JLabel cartaJogador1;
    private javax.swing.JLabel cartaJogador2;
    private javax.swing.JLabel cartaJogador3;
    private javax.swing.JLabel cobertaBtn;
    private javax.swing.JLabel correrBtn;
    private javax.swing.JLabel espacoLayoutDireito;
    private javax.swing.JLabel espacoLayoutEsquerdo;
    private javax.swing.JLabel info;
    private javax.swing.JLabel placarCPU;
    private javax.swing.JLabel placarJogador;
    private javax.swing.JLabel titulo;
    private javax.swing.JLabel trucoBtn;
    // End of variables declaration//GEN-END:variables
}
