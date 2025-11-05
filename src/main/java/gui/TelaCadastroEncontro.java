package gui;

import dao.EncontroDAO;
import dao.MaeDAO;
import modelo.Encontro;
import modelo.Mae;
import modelo.Servico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TelaCadastroEncontro extends JFrame {

    private JTable tabelaServicos;
    private JComboBox<Mae> comboMae;
    private JTextField txtDataEncontro;
    private List<Servico> servicosDoEncontro = new ArrayList<>();

    private MaeDAO maeDAO = new MaeDAO();
    private EncontroDAO encontroDAO = new EncontroDAO();

    public TelaCadastroEncontro() {
        setTitle("Cadastro de Encontro");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(criarFormulario(), BorderLayout.NORTH);
        painel.add(criarTabelaServicos(), BorderLayout.CENTER);
        painel.add(criarBotoes(), BorderLayout.SOUTH);

        add(painel);

        carregarServicosFixos();
        carregarMaes();
    }

    private JPanel criarFormulario() {
        JPanel form = new JPanel(new GridLayout(1, 4, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Informações"));

        txtDataEncontro = new JTextField();
        comboMae = new JComboBox<>();

        form.add(new JLabel("Data do Encontro (YYYY-MM-DD):"));
        form.add(txtDataEncontro);
        form.add(new JLabel("Responsável pelo Serviço:"));
        form.add(comboMae);

        return form;
    }

    private JScrollPane criarTabelaServicos() {
        tabelaServicos = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Serviço", "Mãe Responsável"}
        ));
        return new JScrollPane(tabelaServicos);
    }

    private void carregarServicosFixos() {
        String[] servicosFixos = {
                "MÚSICA", "RECEPÇÃO DE MÃES", "ACOLHIDA", "TERÇO",
                "FORMAÇÃO", "MOMENTO ORACIONAL", "PROCLAMAÇÃO DA VITÓRIA",
                "SORTEIO DAS FLORES", "ENCERRAMENTO", "ARRUMAÇÃO CAPELA",
                "QUEIMA DOS PEDIDOS", "COMPRA DAS FLORES"
        };

        DefaultTableModel model = (DefaultTableModel) tabelaServicos.getModel();
        for (String servico : servicosFixos) {
            model.addRow(new Object[]{servico, ""});
        }
    }

    private void carregarMaes() {
        comboMae.removeAllItems();
        for (Mae m : maeDAO.listar()) {
            comboMae.addItem(m);
        }
    }

    private JPanel criarBotoes() {
        JPanel botoes = new JPanel();
        JButton btnAtribuir = new JButton("Atribuir Responsável");
        JButton btnSalvar = new JButton("Salvar Encontro");

        btnAtribuir.addActionListener(this::atribuirResponsavel);
        btnSalvar.addActionListener(this::salvarEncontro);

        botoes.add(btnAtribuir);
        botoes.add(btnSalvar);

        return botoes;
    }

    private void atribuirResponsavel(ActionEvent e) {
        int linha = tabelaServicos.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço na tabela!");
            return;
        }

        Mae maeSelecionada = (Mae) comboMae.getSelectedItem();
        if (maeSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Nenhuma mãe cadastrada!");
            return;
        }

        tabelaServicos.setValueAt(maeSelecionada.getNome(), linha, 1);

        Servico servico = new Servico();
        servico.setTipo((String) tabelaServicos.getValueAt(linha, 0));
        servico.setMae(maeSelecionada.getNome()); // ✅ agora passa apenas o nome (String)

        servicosDoEncontro.add(servico);
    }

    private void salvarEncontro(ActionEvent e) {
        try {
            LocalDate data = LocalDate.parse(txtDataEncontro.getText());

            Encontro encontro = new Encontro();
            encontro.setDataEncontro(data);
            encontro.setServicos(servicosDoEncontro);

            encontroDAO.inserir(encontro); // ✅ Envia os serviços para o DAO corretamente

            JOptionPane.showMessageDialog(this, "✅ Encontro salvo com sucesso!");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Erro ao salvar encontro: " + ex.getMessage());
        }
    }
}
