package gui;

import dao.EncontroDAO;
import modelo.Encontro;
import modelo.Servico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException; // <--- NECESSÁRIO PARA TRATAR ERROS DO DAO
import java.time.LocalDate;
import java.util.List;

// Mantenha este import para que a classe TelaCadastroEncontro seja reconhecida
import gui.TelaCadastroEncontro;

public class TelaListaEncontros extends JFrame {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private final EncontroDAO encontroDAO = new EncontroDAO();

    public TelaListaEncontros() {
        setTitle("Lista de Encontros");
        try {
            // Ajustar o caminho se a TelaPrincipal não estiver no mesmo pacote
            URL resource = TelaPrincipal.class.getResource("/Church_white.png");
            Image icon = new ImageIcon(resource).getImage();
            setIconImage(icon);
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
        carregarEncontros(); // Chamada inicial
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
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Esconde a coluna do ID
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(0).setWidth(0);

        return new JScrollPane(tabela);
    }

    private JPanel criarBotoes() {
        JPanel botoes = new JPanel();

        JButton btnNovo = new JButton("Novo Encontro");
        JButton btnEditar = new JButton("Editar Encontro");
        JButton btnAtualizar = new JButton("Atualizar lista");
        JButton btnCancelar = new JButton("Cancelar encontro");
        JButton btnExcluir = new JButton("Excluir Encontro Selecionado");

        btnEditar.addActionListener(e -> editarEncontro());
        btnNovo.addActionListener(e -> new TelaCadastroEncontro(this, null).setVisible(true));
        btnAtualizar.addActionListener(e -> carregarEncontros());
        btnCancelar.addActionListener(e -> cancelarEncontro());
        btnExcluir.addActionListener(e -> excluirEncontroSelecionado());

        botoes.add(btnEditar);
        botoes.add(btnNovo);
        botoes.add(btnAtualizar);
        botoes.add(btnCancelar);
        botoes.add(btnExcluir);

        return botoes;
    }

    /** * MÉTODO PÚBLICO para ser chamado pela TelaCadastroEncontro (após salvar).
     * AGORA COM TRATAMENTO DE ERROS.
     */
    public void carregarEncontros() {
        modeloTabela.setRowCount(0);

        List<Encontro> encontros;
        try {
            // LINHA 97 CORRIGIDA: Trata a SQLException lançada por encontroDAO.listar()
            encontros = encontroDAO.listar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro de Banco de Dados ao carregar encontros: " + e.getMessage(),
                    "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        for (Encontro e : encontros) {
            StringBuilder infoServicos = new StringBuilder();

            for (Servico s : e.getServicos()) {
                infoServicos.append(s.getTipo())
                        .append(" - ")
                        .append(s.getNomeMae() != null ? s.getNomeMae() : "SEM RESPONSÁVEL")
                        .append("\n");
            }

            modeloTabela.addRow(new Object[]{
                    e.getIdEncontro(), // ID na coluna 0 (oculta)
                    e.getDataEncontro(),
                    infoServicos.toString(),
                    e.isCancelado() ? "SIM" : "NÃO"
            });
        }
    }

    public void editarEncontro() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um encontro para editar.");
            return;
        }

        int id = (int) tabela.getValueAt(linha, 0);

        Encontro encontroParaEditar = null;
        try {
            encontroParaEditar = encontroDAO.buscarPorId(id); // Deve tratar SQLException se o DAO lançar
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar detalhes do encontro: " + e.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        if (encontroParaEditar != null) {
            TelaCadastroEncontro telaEdicao = new TelaCadastroEncontro(this, encontroParaEditar);
            telaEdicao.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Erro: Encontro não encontrado no banco de dados.");
        }
    }

    private void cancelarEncontro() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um encontro para cancelar!");
            return;
        }

        int id = (int) tabela.getValueAt(linha, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja CANCELAR o encontro selecionado?",
                "Confirmação de Cancelamento", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try { // <-- TRATAMENTO DO MÉTODO cancelamento
                encontroDAO.cancelar(id);
                JOptionPane.showMessageDialog(this, "Encontro cancelado!");
                carregarEncontros();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro de Banco de Dados ao cancelar: " + e.getMessage(), "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /** Exclui o encontro selecionado e atualiza a tabela. */
    private void excluirEncontroSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um encontro para excluir!");
            return;
        }

        int id = (int) modeloTabela.getValueAt(linha, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja EXCLUIR o encontro selecionado? Esta ação é irreversível!",
                "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try { // <-- TRATAMENTO DO MÉTODO exclusão
                // LINHA 177 CORRIGIDA: exclusão
                encontroDAO.excluir(id);
                JOptionPane.showMessageDialog(this, "Encontro excluído com sucesso!");
                carregarEncontros(); // Atualiza a tabela
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro de Banco de Dados ao excluir: " + e.getMessage(), "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new TelaListaEncontros().setVisible(true));
    }
}