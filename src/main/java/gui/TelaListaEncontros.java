package gui;

import dao.EncontroDAO;
import modelo.Encontro;
import modelo.Servico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TelaListaEncontros extends JFrame {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private EncontroDAO encontroDAO = new EncontroDAO();

    public TelaListaEncontros() {
        setTitle("Lista de Encontros");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(criarTabela(), BorderLayout.CENTER);
        painel.add(criarBotoes(), BorderLayout.SOUTH);

        add(painel);
        carregarEncontros();
    }

    private JScrollPane criarTabela() {
        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Data", "Serviços + Responsáveis", "Cancelado"}, 0
        );

        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        return new JScrollPane(tabela);
    }

    private JPanel criarBotoes() {
        JPanel botoes = new JPanel();

        JButton btnNovo = new JButton("Novo Encontro");
        JButton btnAtualizar = new JButton("Atualizar lista");
        JButton btnCancelar = new JButton("Cancelar encontro");
        JButton btnExcluir = new JButton("Excluir encontro (futuro)");

        btnNovo.addActionListener(e -> new TelaCadastroEncontro().setVisible(true));
        btnAtualizar.addActionListener(e -> carregarEncontros());
        btnCancelar.addActionListener(e -> cancelarEncontro());
        btnExcluir.addActionListener(e -> excluirEncontro());

        botoes.add(btnNovo);
        botoes.add(btnAtualizar);
        botoes.add(btnCancelar);
        botoes.add(btnExcluir);

        return botoes;
    }


    private void carregarEncontros() {
        modeloTabela.setRowCount(0);

        List<Encontro> encontros = encontroDAO.listar();

        for (Encontro e : encontros) {
            StringBuilder infoServicos = new StringBuilder();

            for (Servico s : e.getServicos()) {
                infoServicos.append(s.getTipo())
                        .append(" - ")
                        .append(s.getMae() != null ? s.getMae() : "SEM RESPONSÁVEL")
                        .append("\n");
            }

            modeloTabela.addRow(new Object[]{
                    e.getIdEncontro(),
                    e.getDataEncontro(),
                    infoServicos.toString(),
                    e.isCancelado() ? "SIM" : "NÃO"
            });
        }
    }

    private void cancelarEncontro() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um encontro para cancelar!");
            return;
        }

        int id = (int) tabela.getValueAt(linha, 0);
        encontroDAO.cancelar(id);

        JOptionPane.showMessageDialog(this, "Encontro cancelado!");
        carregarEncontros();
    }

    private void excluirEncontro() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um encontro!");
            return;
        }

        int id = (int) tabela.getValueAt(linha, 0);
        LocalDate data = (LocalDate) tabela.getValueAt(linha, 1);

        encontroDAO.excluirFuturo(id, data);

        carregarEncontros();
        JOptionPane.showMessageDialog(this, "Operação concluída!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaListaEncontros().setVisible(true));
    }
}
