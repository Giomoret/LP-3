package gui;

import dao.MaeDAO;
import modelo.Mae;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaAniversariantes extends JFrame {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private MaeDAO maeDAO = new MaeDAO();

    public TelaAniversariantes() {
        setTitle("Aniversariantes do Mês");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(criarTabela(), BorderLayout.CENTER);
        painel.add(criarBotoes(), BorderLayout.SOUTH);

        add(painel);
        carregarAniversariantes();
    }

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

        List<Mae> lista = maeDAO.listarAniversariantesDoMes();
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
        SwingUtilities.invokeLater(() -> new TelaAniversariantes().setVisible(true));
    }
}
