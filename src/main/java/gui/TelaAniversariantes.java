package gui;

import dao.MaeDAO;
import modelo.Mae;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.sql.SQLException; // NECESSÁRIO
import java.util.ArrayList; // Para inicializar lista em caso de erro

public class TelaAniversariantes extends JFrame {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private MaeDAO maeDAO = new MaeDAO();

    public TelaAniversariantes() {
        setTitle("Aniversariantes do Mês");
        try {
            URL resource = TelaPrincipal.class.getResource("/Church_white.png");
            Image icon = new ImageIcon(resource).getImage();
            setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Erro ao carregar o ícone: " + e.getMessage());
        }

        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(criarTabela(), BorderLayout.CENTER);
        painel.add(criarBotoes(), BorderLayout.SOUTH);

        add(painel);
        carregarAniversariantes();
    }

    // ... (criarTabela e criarBotoes inalterados) ...

    private JScrollPane criarTabela() {
        modeloTabela = new DefaultTableModel(
                new Object[]{"Nome", "Telefone", "Endereço", "Aniversário"}, 0
        );
        tabela = new JTable(modeloTabela);
        return new JScrollPane(tabela);
    }

    private JPanel criarBotoes() {
        JPanel botoes = new JPanel();

        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.addActionListener(e -> carregarAniversariantes());

        botoes.add(btnAtualizar);
        return botoes;
    }

    private void carregarAniversariantes() {
        modeloTabela.setRowCount(0);

        List<Mae> lista = new ArrayList<>(); // Inicializa lista

        try {
            // LINHA 61 CORRIGIDA: Encapsulada em try-catch
            lista = maeDAO.listarAniversariantesDoMes();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar aniversariantes: " + e.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        for (Mae m : lista) {
            modeloTabela.addRow(new Object[]{
                    m.getNome(),
                    m.getTelefone(),
                    m.getEndereco(),
                    m.getDataAniversario()
            });
        }
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new TelaAniversariantes().setVisible(true));
    }
}