package gui;

import dao.MaeDAO;
import modelo.Mae;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.time.format.DateTimeParseException;

public class TelaCadastroMae extends JDialog {

    private JTextField txtNome, txtTelefone, txtEndereco, txtAniversario;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private MaeDAO maeDAO = new MaeDAO();

    private Mae maeEmEdicao;

    public TelaCadastroMae(JFrame owner, Mae mae) {
        super(owner, "Cadastro de Mães", true);

        this.maeEmEdicao = mae;

        // Configuração visual da janela
        try {
            URL resource = TelaPrincipal.class.getResource("/Church_white.png");
            Image icon = new ImageIcon(resource).getImage();
            setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Erro ao carregar o ícone: " + e.getMessage());
        }

        setSize(650, 450);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(criarFormulario(), BorderLayout.NORTH);
        painel.add(criarTabela(), BorderLayout.CENTER);
        painel.add(criarBotoes(), BorderLayout.SOUTH);

        add(painel);

        if (maeEmEdicao != null) {
            carregarDadosParaEdicao();
        }

        listarMaes();
        // setVisible(true) FOI REMOVIDO PARA CORRIGIR A REABERTURA
    }

    private JPanel criarFormulario() {
        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Dados da Mãe"));

        txtNome = new JTextField();
        txtTelefone = new JTextField();
        txtEndereco = new JTextField();
        txtAniversario = new JTextField();

        form.add(new JLabel("Nome:"));
        form.add(txtNome);
        form.add(new JLabel("Telefone:"));
        form.add(txtTelefone);
        form.add(new JLabel("Endereço:"));
        form.add(txtEndereco);
        form.add(new JLabel("Data Aniversário (AAAA-MM-DD):"));
        form.add(txtAniversario);

        return form;
    }

    private JScrollPane criarTabela() {
        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Telefone", "Endereço", "Aniversário"}, 0);
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> carregarDadosDoSelecionado());

        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(0).setWidth(0);

        return new JScrollPane(tabela);
    }

    private void carregarDadosDoSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            txtNome.setText(modeloTabela.getValueAt(linha, 1).toString());
            txtTelefone.setText(modeloTabela.getValueAt(linha, 2).toString());
            txtEndereco.setText(modeloTabela.getValueAt(linha, 3).toString());

            Object dataObj = modeloTabela.getValueAt(linha, 4);
            txtAniversario.setText(dataObj != null ? dataObj.toString() : "");

            int id = (int) modeloTabela.getValueAt(linha, 0);

            maeEmEdicao = new Mae();
            maeEmEdicao.setIdMae(id);
        }
    }

    private JPanel criarBotoes() {
        JPanel botoes = new JPanel();

        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar Lista");

        btnSalvar.addActionListener(e -> salvarMae());
        btnExcluir.addActionListener(e -> excluirMae());
        btnAtualizar.addActionListener(e -> listarMaes());

        botoes.add(btnSalvar);
        botoes.add(btnExcluir);
        botoes.add(btnAtualizar);

        return botoes;
    }

    private void salvarMae() {
        try {
            Mae mae = (maeEmEdicao != null && maeEmEdicao.getIdMae() != 0) ? maeEmEdicao : new Mae();

            mae.setNome(txtNome.getText().trim());
            mae.setTelefone(txtTelefone.getText().trim());
            mae.setEndereco(txtEndereco.getText().trim());

            String dataText = txtAniversario.getText().trim();
            if (!dataText.isEmpty()) {
                java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
                mae.setDataAniversario(java.time.LocalDate.parse(dataText, fmt));
            } else {
                mae.setDataAniversario(null);
            }

            // 2. Decide entre INSERIR ou ATUALIZAR
            try {
                if (mae.getIdMae() == 0) {
                    maeDAO.inserir(mae);
                    JOptionPane.showMessageDialog(this, "✅ Mãe cadastrada com sucesso!");
                } else {
                    maeDAO.atualizar(mae);
                    JOptionPane.showMessageDialog(this, "✅ Mãe atualizada com sucesso!");
                }
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "Erro de Banco de Dados: " + sqle.getMessage(), "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
                sqle.printStackTrace();
                return;
            }

            listarMaes();
            limparCampos();
            maeEmEdicao = null;

            // CORREÇÃO FINAL: Fechar a janela após sucesso.
            this.dispose();

        } catch (DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use: YYYY-MM-DD\nEx: 1980-05-21");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarDadosParaEdicao() {
        setTitle("Editar Mães");
    }

    private void excluirMae() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma mãe!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir esta mãe?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int idMae = (int) modeloTabela.getValueAt(linha, 0);
            try {
                maeDAO.excluir(idMae);
                JOptionPane.showMessageDialog(this, "Mãe excluída com sucesso.");
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + sqle.getMessage(), "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
                sqle.printStackTrace();
                return;
            }
            listarMaes();
            limparCampos();
        }
    }

    private void listarMaes() {
        modeloTabela.setRowCount(0);
        List<Mae> maes = new ArrayList<>();

        try {
            maes = maeDAO.listar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar lista de Mães: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        for (Mae m : maes) {
            modeloTabela.addRow(new Object[]{
                    m.getIdMae(),
                    m.getNome(),
                    m.getTelefone(),
                    m.getEndereco(),
                    m.getDataAniversario()
            });
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtTelefone.setText("");
        txtEndereco.setText("");
        txtAniversario.setText("");
        maeEmEdicao = null;
        tabela.clearSelection();
    }
}