package gui;

import dao.EncontroDAO;
import modelo.Encontro;
import modelo.Servico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TelaListaEncontros extends JFrame {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private final EncontroDAO encontroDAO = new EncontroDAO();

    public TelaListaEncontros() {
        setTitle("Lista de Encontros");
        try {
            URL resource = TelaPrincipal.class.getResource("/Church_white.png");
            if (resource != null) {
                Image icon = new ImageIcon(resource).getImage();
                setIconImage(icon);
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar o ícone: " + e.getMessage());
        }

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
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Oculta a coluna ID
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(0).setWidth(0);

        return new JScrollPane(tabela);
    }

    private JPanel criarBotoes() {
        JPanel botoes = new JPanel();

        JButton btnNovo = new JButton("Novo Encontro");
        JButton btnEditar = new JButton("Editar Encontro");
        JButton btnAtualizar = new JButton("Atualizar Lista");
        JButton btnCancelar = new JButton("Cancelar Encontro");
        JButton btnExcluir = new JButton("Excluir Encontro");

        btnNovo.addActionListener(e -> new TelaCadastroEncontro(this, null).setVisible(true));
        btnEditar.addActionListener(e -> editarEncontro());
        btnAtualizar.addActionListener(e -> carregarEncontros());
        btnCancelar.addActionListener(e -> cancelarEncontro());
        btnExcluir.addActionListener(e -> excluirEncontro());

        botoes.add(btnNovo);
        botoes.add(btnEditar);
        botoes.add(btnAtualizar);
        botoes.add(btnCancelar);
        botoes.add(btnExcluir);

        return botoes;
    }

    // ================== CARREGAR ENCONTROS ==================
    public void carregarEncontros() {
        modeloTabela.setRowCount(0);

        try {
            List<Encontro> encontros = encontroDAO.listar();

            for (Encontro e : encontros) {
                StringBuilder infoServicos = new StringBuilder();

                for (Servico s : e.getServicos()) {
                    infoServicos.append(s.getTipo())
                            .append(" - ")
                            .append(s.getNomeMae() != null ? s.getNomeMae() : "SEM RESPONSÁVEL")
                            .append("\n");
                }

                modeloTabela.addRow(new Object[]{
                        e.getIdEncontro(),
                        e.getDataEncontro(),
                        infoServicos.toString(),
                        e.isCancelado() ? "SIM" : "NÃO"
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar encontros: " + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================== EDITAR ENCONTRO (somente futuros) ==================
    public void editarEncontro() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um encontro para editar.");
            return;
        }

        int id = (int) tabela.getValueAt(linha, 0);
        try {
            Encontro encontro = encontroDAO.buscarPorId(id);

            if (encontro == null) {
                JOptionPane.showMessageDialog(this, "Encontro não encontrado.");
                return;
            }

            if (encontro.getDataEncontro().isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Encontros passados não podem ser editados.");
                return;
            }

            TelaCadastroEncontro telaEdicao = new TelaCadastroEncontro(this, encontro);
            telaEdicao.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar encontro: " + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================== CANCELAR ENCONTRO ==================
    private void cancelarEncontro() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um encontro para cancelar!");
            return;
        }

        int id = (int) tabela.getValueAt(linha, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja CANCELAR este encontro?",
                "Confirmação de Cancelamento",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                encontroDAO.cancelar(id);
                JOptionPane.showMessageDialog(this, "Encontro cancelado com sucesso!");
                carregarEncontros();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao cancelar encontro: " + e.getMessage(),
                        "Erro de Banco de Dados",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================== EXCLUIR ENCONTRO ==================
    private void excluirEncontro() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um encontro para excluir!");
            return;
        }

        int id = (int) tabela.getValueAt(linha, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja EXCLUIR este encontro permanentemente?",
                "Confirmação de Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                encontroDAO.excluir(id);
                JOptionPane.showMessageDialog(this, "Encontro excluído com sucesso!");
                carregarEncontros();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao excluir encontro: " + e.getMessage(),
                        "Erro de Banco de Dados",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================== MAIN ==================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new TelaListaEncontros().setVisible(true));
    }
}
