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

    // A lista servicosDoEncontro será preenchida APENAS na hora de salvar,
    // garantindo que todos os 12 serviços sejam considerados.

    private MaeDAO maeDAO = new MaeDAO();
    private EncontroDAO encontroDAO = new EncontroDAO();

    public TelaCadastroEncontro() {
        setTitle("Cadastro de Encontro");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(criarFormulario(), BorderLayout.NORTH);

        // Coluna extra oculta para armazenar o ID da Mãe
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
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                // ADICIONADO: Coluna ID da Mãe (oculta, para o DAO)
                new String[]{"Serviço", "Mãe Responsável", "ID Mãe"}
        ) {
            // Torna a coluna 2 (ID Mãe) não editável e garante que o resto funcione
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaServicos = new JTable(model);

        // Esconde a coluna do ID da Mãe, pois ela é apenas para o DAO
        tabelaServicos.getColumnModel().getColumn(2).setMinWidth(0);
        tabelaServicos.getColumnModel().getColumn(2).setMaxWidth(0);
        tabelaServicos.getColumnModel().getColumn(2).setWidth(0);

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
            // Adicionado 0 para o ID da Mãe (coluna 2), indicando NULO no BD
            model.addRow(new Object[]{servico, "", 0});
        }
    }

    private void carregarMaes() {
        comboMae.removeAllItems();
        // A MaeDAO.listar() deve garantir que o objeto Mae tem o ID da Mae
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

        // 1. Define o nome visível (coluna 1)
        tabelaServicos.setValueAt(maeSelecionada.getNome(), linha, 1);

        // 2. Define o ID da Mãe (coluna 2, oculta) para ser lido no momento de salvar
        // É essencial que o objeto Mae retorne o ID correto (assumindo que Mae tem getIdMae())
        tabelaServicos.setValueAt(maeSelecionada.getIdMae(), linha, 2);
    }

    private void salvarEncontro(ActionEvent e) {
        // CORREÇÃO ESSENCIAL: Recria a lista de serviços a partir da tabela
        List<Servico> servicosParaSalvar = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) tabelaServicos.getModel();

        try {
            LocalDate data = LocalDate.parse(txtDataEncontro.getText());

            for (int i = 0; i < model.getRowCount(); i++) {
                String tipoServico = (String) model.getValueAt(i, 0);
                int idMae = (Integer) model.getValueAt(i, 2); // Pega o ID da Mãe da coluna oculta

                Servico s = new Servico();
                s.setTipo(tipoServico);
                s.setIdMae(idMae); // O DAO usará este ID para salvar
                // s.setDescricao (se houvesse um campo para isso, você leria aqui)

                servicosParaSalvar.add(s);
            }

            Encontro encontro = new Encontro();
            encontro.setDataEncontro(data);

            // ATRIBUI A LISTA COMPLETA
            encontro.setServicos(servicosParaSalvar);

            encontroDAO.inserir(encontro);

            JOptionPane.showMessageDialog(this, "✅ Encontro salvo com sucesso!");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Erro ao salvar encontro: " + ex.getMessage());
        }
    }
}