package gui;

import dao.MaeDAO;
import modelo.Mae;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

public class TelaCadastroMae extends JFrame {

    private JTextField txtNome, txtTelefone, txtEndereco, txtAniversario;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private MaeDAO maeDAO = new MaeDAO();

    public TelaCadastroMae() {
        setTitle("Cadastro de Mães");
        try {
            URL resource = TelaPrincipal.class.getResource("/Church_white.png");
            Image icon = new ImageIcon(resource).getImage();
            setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Erro ao carregar o ícone: " + e.getMessage());
        }
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(criarFormulario(), BorderLayout.NORTH);
        painel.add(criarTabela(), BorderLayout.CENTER);
        painel.add(criarBotoes(), BorderLayout.SOUTH); // ← GARANTE QUE OS BOTÕES APAREÇAM

        add(painel);
        listarMaes();  // ← Carrega a tabela
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
        return new JScrollPane(tabela);
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
            Mae mae = new Mae();
            mae.setNome(txtNome.getText().trim());
            mae.setTelefone(txtTelefone.getText().trim());
            mae.setEndereco(txtEndereco.getText().trim());

            String dataText = txtAniversario.getText().trim();
            if (!dataText.isEmpty()) {
                // aceita apenas formato YYYY-MM-DD
                java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
                mae.setDataAniversario(java.time.LocalDate.parse(dataText, fmt));
            } else {
                mae.setDataAniversario(null); // sem data informada
            }

            maeDAO.inserir(mae);
            listarMaes();
            limparCampos();
            JOptionPane.showMessageDialog(this, "✅ Mãe cadastrada com sucesso!");
        } catch (java.time.format.DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use: YYYY-MM-DD\nEx: 1980-05-21");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void excluirMae() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma mãe!");
            return;
        }

        int idMae = (int) modeloTabela.getValueAt(linha, 0);
        maeDAO.excluir(idMae);
        listarMaes();
    }

    private void listarMaes() {
        modeloTabela.setRowCount(0);
        List<Mae> maes = maeDAO.listar();
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
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new TelaCadastroMae().setVisible(true));
    }
}
