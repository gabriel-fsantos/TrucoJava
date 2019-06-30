/**
 * Package:     principal
 * Class:       TrucoFrame
 * Descrição:   Tela do jogo com todos os elementos visuais e interação com o usuário.
 * Matéria:     Programação de Computadores II
 * Autores:     Gabriel, Lucas Rassilan, Rebeca Gaia
 */

package principal;

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
        truco = new Truco(this);
        
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
        truco.travaVezDoJogador();
        hideLabel(cartaAtualCPU);
        hideLabel(cartaAtualJogador);
        updateScore();
        
        truco.usuario.jogarDeCoberta = false;
        cobertaBtn.setVisible(true);
        cobertaBtn.setBackground(new Color(141, 27, 36));
        cobertaBtn.setBorder(new LineBorder(new Color(102, 0, 0)));
        
        trucoBtn.setVisible(true);
        trucoBtn.setText(String.valueOf(ValoresPartida.TRUCO) + "!");
        
        truco.preparaNovaPartida();
        if (truco.usuario.getTentos() == 10 && truco.bot.getTentos() == 10) {
            // Trata mão de ferro
            TrucoFrame.displayImage("Verso.png", cartaJogador1);
            TrucoFrame.displayImage("Verso.png", cartaJogador2);
            TrucoFrame.displayImage("Verso.png", cartaJogador3);
            trucoBtn.setVisible(false);
            cobertaBtn.setVisible(false);
        } else {
            TrucoFrame.displayImage(truco.usuario.cartas.get(0).getPath(), cartaJogador1);
            TrucoFrame.displayImage(truco.usuario.cartas.get(1).getPath(), cartaJogador2);
            TrucoFrame.displayImage(truco.usuario.cartas.get(2).getPath(), cartaJogador3);
            
            // Trata mão de 10
            if (truco.usuario.getTentos() == 10) {
                trucoBtn.setVisible(false);
                cobertaBtn.setVisible(false);
                repaint();

                int vai = JOptionPane.showConfirmDialog(null, "Deseja ir na mão de 10?", "Truco", JOptionPane.YES_OPTION);
                if (vai == JOptionPane.YES_OPTION) {
                    truco.subirAposta();
                } else {
                    showInfo("Você correu!", 3000);
                    truco.bot.setTentos(truco.bot.getTentos() + truco.getValorNumericoPartida());

                    ScheduledThreadPoolExecutor wait = new ScheduledThreadPoolExecutor(1);
                    wait.schedule(() -> initializeGame(), 3000, TimeUnit.MILLISECONDS);

                    repaint();
                    return;
                }
            } else if (truco.bot.getTentos() == 10) {
                trucoBtn.setVisible(false);
                cobertaBtn.setVisible(false);
                repaint();

                if (truco.bot.respondeTruco()) {
                    truco.subirAposta(); 
                } else {
                    showInfo("O Bot correu!", 3000);
                    truco.usuario.setTentos(truco.usuario.getTentos() + truco.getValorNumericoPartida());

                    ScheduledThreadPoolExecutor wait = new ScheduledThreadPoolExecutor(1);
                    wait.schedule(() -> initializeGame(), 3000, TimeUnit.MILLISECONDS);

                    repaint();
                    return;
                }
            }
        }
        
        showInfo("É a sua vez.", 2000);
        
        // Se não for vez do jogador, manda o bot jogar
        if (!truco.eVezDoJogador()) {
            String message = truco.jogaBot();
            
            // Se o bot pediu truco, mostra resposta do usuário
            if (message.length() > 0) {
                showInfo(message, 3000);
                
                // Se o usuário tiver recusado o truco
                if (message.contains("correu")) {
                    ScheduledThreadPoolExecutor wait = new ScheduledThreadPoolExecutor(1);
                    wait.schedule(() -> initializeGame(), 3000, TimeUnit.MILLISECONDS);
                    
                    repaint();
                    return;
                } else {
                    // Mostra o botão de aumentar a aposta, se a aposta ainda puder ser ampliada
                    if (truco.getValorPartida() != ValoresPartida.DOZE) {
                        trucoBtn.setVisible(true);
                        trucoBtn.setText(String.valueOf(truco.getProximoValor()) + "!");
                    }
                }
            }
            
            TrucoFrame.displayImage(truco.bot.cartaJogada.getPath(), cartaAtualCPU);
        }
        
        repaint();
        
        // DEBUG_ONLY
        //for (Carta c : truco.bot.cartas)
        //    System.out.println(c);
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
        truco.travaVezDoJogador();
        info.setText(text);
        repaint();
        
        ScheduledThreadPoolExecutor waitThread = new ScheduledThreadPoolExecutor(1);
        waitThread.schedule(() -> {
            info.setText("");
            truco.liberaVezDoJogador();
        }, duration, TimeUnit.MILLISECONDS);
    }
    
    public void jogaCarta (int n) {
        // Ignora se o usuário mandar jogar fora da vez dele ou cartas já usadas
        if (truco.usuario.cartas.get(n).isUsada() || !truco.eVezDoJogador() || truco.vezDoJogadorTravada()) return;
        
        if (!truco.usuario.jogarDeCoberta)
            TrucoFrame.displayImage(truco.usuario.cartas.get(n).getPath(), cartaAtualJogador);
        else
            TrucoFrame.displayImage("Verso.png", cartaAtualJogador);
        
        // Esconde a carta que acabou de ser jogada
        this.hideLabel(n == 0 ? cartaJogador1 : (n == 1 ? cartaJogador2 : cartaJogador3));
        
        // Informa jogada ao controle lógico
        String message = truco.jogaUsuario(n);
        if (message.equals("ABORT")) {
            // Usuário correu
            truco.travaVezDoJogador();
            ScheduledThreadPoolExecutor wait = new ScheduledThreadPoolExecutor(1);
            wait.schedule(() -> initializeGame(), 3000, TimeUnit.MILLISECONDS);
            
            repaint();
            return;
        }
        
        // Garante exibição da carta do computador
        TrucoFrame.displayImage(truco.bot.cartaJogada.getPath(), cartaAtualCPU);
        
        // Desativa coberta, caso esteja ativada
        if (truco.usuario.jogarDeCoberta)
            cobertaBtnMouseClicked(null);
        
        repaint();
        
        // Verifica se alguém venceu
        if (message.contains("venceu") || message.equals("Jogo empatado!")) {
            // Nova partida
            showInfo(message, 3000);
            ScheduledThreadPoolExecutor wait = new ScheduledThreadPoolExecutor(1);
            wait.schedule(() -> initializeGame(), 3000, TimeUnit.MILLISECONDS);
        } else {
            // Nova rodada
            showInfo(message, 2000);
            preparaNovaRodada();
        }
    }
    
    private void preparaNovaRodada () {
        ScheduledThreadPoolExecutor waitThread = new ScheduledThreadPoolExecutor(1);
        waitThread.schedule(() -> {
            hideLabel(cartaAtualCPU);
            hideLabel(cartaAtualJogador);

            truco.preparaNovaRodada();
            
            // Se não for vez do jogador, manda o bot jogar
            if (!truco.usuarioEProximo()) {
                String message = truco.jogaBot();

                // Se o bot pediu truco, mostra resposta do usuário
                if (message.length() > 0) {
                    showInfo(message, 3000);

                    // Se o usuário tiver recusado o truco
                    if (message.contains("correu")) {
                        ScheduledThreadPoolExecutor wait = new ScheduledThreadPoolExecutor(1);
                        wait.schedule(() -> initializeGame(), 3000, TimeUnit.MILLISECONDS);
                        
                        repaint();
                        return;
                    } else {
                        // Mostra o botão de aumentar a aposta, se a aposta ainda puder ser ampliada
                        if (truco.getValorPartida() != ValoresPartida.DOZE) {
                            trucoBtn.setVisible(true);
                            trucoBtn.setText(String.valueOf(truco.getProximoValor()) + "!");
                        }
                    }
                }
                
                TrucoFrame.displayImage(truco.bot.cartaJogada.getPath(), cartaAtualCPU);
            }

            repaint();
        }, 3000, TimeUnit.MILLISECONDS);
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
        cobertaBtn = new javax.swing.JLabel();
        info = new javax.swing.JLabel();
        espacoLayoutEsquerdo = new javax.swing.JLabel();
        espacoLayoutDireito = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Truco");
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
     * Método usado para aumentar a aposta pelo jogador ou pelo bot
     */
    private void aumentaAposta () {                                      
        truco.subirAposta();
        showInfo(truco.getValorPartida() + "!", 3000);
        
        if (truco.getValorPartida() == ValoresPartida.DOZE)
            trucoBtn.setVisible(false);
        else
            trucoBtn.setText(String.valueOf(truco.getProximoValor()) + "!");
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
        if (truco.bot.respondeTruco()) {
            aumentaAposta();
            trucoBtn.setVisible(false);
            showInfo("Bot aceitou!", 2000);
        } else {
            showInfo("O Bot correu!", 3000);
            truco.usuario.setTentos(truco.usuario.getTentos() + truco.getValorNumericoPartida());
            
            repaint();
            ScheduledThreadPoolExecutor wait = new ScheduledThreadPoolExecutor(1);
            wait.schedule(() -> initializeGame(), 3000, TimeUnit.MILLISECONDS);
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
    
    private Truco truco;
    private javax.swing.JLabel background;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel cartaAtualCPU;
    private javax.swing.JLabel cartaAtualJogador;
    private javax.swing.JLabel cartaJogador1;
    private javax.swing.JLabel cartaJogador2;
    private javax.swing.JLabel cartaJogador3;
    private javax.swing.JLabel cobertaBtn;
    private javax.swing.JLabel espacoLayoutDireito;
    private javax.swing.JLabel espacoLayoutEsquerdo;
    private javax.swing.JLabel info;
    private javax.swing.JLabel placarCPU;
    private javax.swing.JLabel placarJogador;
    private javax.swing.JLabel titulo;
    public javax.swing.JLabel trucoBtn;
    // End of variables declaration//GEN-END:variables
}
