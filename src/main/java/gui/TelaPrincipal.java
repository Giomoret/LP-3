package gui;

import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.FlatDarculaLaf;
// Importações de LAF omitidas para brevidade, mas você as mantém
// import com.formdev.flatlaf.FlatDarkLaf;
// import com.formdev.flatlaf.FlatLightLaf;
// import com.formdev.flatlaf.themes.FlatMacLightLaf;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        setTitle("Sistema - Mães Que Oram");
        try {
            URL resource = TelaPrincipal.class.getResource("/Church_white.png");
            Image icon = new ImageIcon(resource).getImage();
            setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Erro ao carregar o ícone: " + e.getMessage());
        }

        setSize(450, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Sistema de Gestão - Mães que Oram", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(5, 1, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        JButton btnMae = new JButton("Cadastro de Mães");
        JButton btnEncontro = new JButton("Cadastro de Encontros");
        JButton btnLista = new JButton("Lista de Encontros");
        JButton btnAniversario = new JButton("Aniversariantes do Mês");
        JButton btnRelatorio = new JButton("Gerar Relatório");

        // --- Ações dos botões (CORREÇÃO DE ARGUMENTOS) ---

        // CORREÇÃO LINHA 51: Passando 'this' e 'null' (2 argumentos esperados)
        btnMae.addActionListener(e -> new TelaCadastroMae(this, null).setVisible(true));

        // CORREÇÃO LINHA 54: Já estava correta
        btnEncontro.addActionListener(e -> new TelaCadastroEncontro(this, null).setVisible(true));

        btnLista.addActionListener(e -> new TelaListaEncontros().setVisible(true));

        btnAniversario.addActionListener(e -> new TelaAniversariantes().setVisible(true));

        // CHAMA A TELA DO RELATÓRIO
        btnRelatorio.addActionListener(e -> new TelaRelatorio().setVisible(true));

        // --- Adicionando ao Painel ---

        painel.add(btnMae);
        painel.add(btnEncontro);
        painel.add(btnLista);
        painel.add(btnAniversario);
        painel.add(btnRelatorio);

        add(painel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {

        try {
            FlatDarculaLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}