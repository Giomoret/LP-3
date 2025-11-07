package gui;

import dao.EncontroDAO;
import modelo.Encontro;
import modelo.Mae;
import modelo.Servico;
import dao.MaeDAO;
import dao.ServicoDAO;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.DefaultCellEditor;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.sql.SQLException; // Importação necessária
import java.util.ArrayList; // Necessário para inicializar List<Mae>

public class TelaCadastroEncontro extends JDialog {

    private TelaListaEncontros owner;
    private Encontro encontroEmEdicao;

    private EncontroDAO encontroDAO = new EncontroDAO();
    private MaeDAO maeDAO = new MaeDAO();
    private ServicoDAO servicoDAO = new ServicoDAO();

    // Componentes de Interface
    private JTextField txtData;
    private JButton btnSalvar;
    private JTable tabelaServicos;
    private ServicoTableModel servicoTableModel;

    // Dados de suporte
    private List<Mae> maesCadastradas; // Variável que causa o erro

    // Construtores... (inalterados)
    public TelaCadastroEncontro(JFrame owner, Encontro encontro) {
        super(owner, "Novo Encontro", true);
        this.owner = null;
        this.encontroEmEdicao = encontro;
        inicializar(owner);
    }

    public TelaCadastroEncontro(TelaListaEncontros owner, Encontro encontro) {
        super(owner, "Editar Encontro", true);
        this.owner = owner;
        this.encontroEmEdicao = encontro;
        inicializar(owner);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (owner != null) {
                    owner.carregarEncontros();
                }
            }
        });
    }

    // Método centralizado de inicialização (COM TRATAMENTO DE ERRO)
    private void inicializar(Frame owner) {
        // --- CORREÇÃO DO ERRO DE LINHA 70 (e 198): TRATAMENTO DE SQL EXCEPTION NO CARREGAMENTO INICIAL ---
        try {
            this.maesCadastradas = maeDAO.listar(); // Este método Lança SQLException
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(owner,
                    "Erro ao carregar lista de Mães. A tabela não será funcional: " + e.getMessage(),
                    "Erro de Inicialização do Banco", JOptionPane.ERROR_MESSAGE);
            this.maesCadastradas = new ArrayList<>(); // Inicializa com lista vazia para evitar NullPointerException
        }
        // ------------------------------------------------------------------------------------------------------

        setSize(550, 450);

        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel painelSuperior = criarPainelSuperior();
        // A chamada criarPainelTabela chama ServicoTableModel, que tem seu próprio try/catch
        JScrollPane painelTabela = criarPainelTabela();

        add(painelSuperior, BorderLayout.NORTH);
        add(painelTabela, BorderLayout.CENTER);
        add(criarBotaoSalvar(), BorderLayout.SOUTH);

        carregarDadosIniciais();

        if (this.encontroEmEdicao == null) {
            setTitle("Novo Encontro");
        } else {
            setTitle("Editar Encontro");
        }
    }

    private JPanel criarPainelSuperior() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtData = new JTextField(10);

        painel.add(new JLabel("Data do Encontro (YYYY-MM-DD):"));
        painel.add(txtData);
        return painel;
    }

    private JScrollPane criarPainelTabela() {
        // O construtor do ServicoTableModel (que chama servicoDAO.listar) está dentro de um try-catch interno
        servicoTableModel = new ServicoTableModel(encontroEmEdicao, servicoDAO, maesCadastradas);
        tabelaServicos = new JTable(servicoTableModel);

        // --- CONFIGURAÇÃO DO EDITOR DE COMBOBOX (MAE) ---
        Vector<Mae> itensMae = new Vector<>();
        Mae placeholder = new Mae();
        placeholder.setNome("--- Sem Responsável ---");
        itensMae.add(placeholder);
        itensMae.addAll(maesCadastradas);

        JComboBox<Mae> cmbMaes = new JComboBox<>(itensMae);
        tabelaServicos.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cmbMaes));

        // --- DEFINIÇÃO DA LARGURA DAS COLUNAS ---
        tabelaServicos.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabelaServicos.getColumnModel().getColumn(1).setPreferredWidth(200);

        return new JScrollPane(tabelaServicos);
    }

    private JPanel criarBotaoSalvar() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar Encontro");
        btnSalvar.addActionListener(e -> salvarEncontro());
        painel.add(btnSalvar);
        return painel;
    }

    private void carregarDadosIniciais() {
        if (encontroEmEdicao != null) {
            txtData.setText(encontroEmEdicao.getDataEncontro().toString());
        } else {
            txtData.setText(LocalDate.now().toString());
        }
    }

    private void salvarEncontro() {
        boolean sucesso = false;
        if (tabelaServicos.isEditing()) {
            tabelaServicos.getCellEditor().stopCellEditing();
        }

        try {
            LocalDate data = LocalDate.parse(txtData.getText());

            if (encontroEmEdicao == null) {
                encontroEmEdicao = new Encontro();
            }

            encontroEmEdicao.setDataEncontro(data);
            encontroEmEdicao.setCancelado(false);

            encontroEmEdicao.setServicos(servicoTableModel.getServicosParaSalvar());

            if (encontroEmEdicao.getIdEncontro() == 0) {
                encontroDAO.inserir(encontroEmEdicao);
                sucesso = true;
            } else {
                sucesso = encontroDAO.atualizar(encontroEmEdicao);
            }

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Encontro salvo com sucesso!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar encontro (DAO retornou false).", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (java.time.format.DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(this, "Erro: Formato de data inválido. Use YYYY-MM-DD.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "ERRO CRÍTICO DE BANCO: Falha ao salvar ou atualizar. Verifique a integridade dos dados (chaves estrangeiras).",
                    "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado ao salvar: " + ex.getMessage(), "Erro Geral", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private class ServicoTableModel extends AbstractTableModel {
        private final String[] COLUNAS = {"Serviço", "Responsável"};
        private List<Servico> listaServicos;
        private List<Mae> maesDisponiveis;

        public ServicoTableModel(Encontro encontro, ServicoDAO servicoDAO, List<Mae> maes) {
            this.maesDisponiveis = maes;

            // --- TRATAMENTO DE ERRO AQUI (dentro do construtor) ---
            try {
                if (encontro != null && encontro.getIdEncontro() != 0) {
                    List<Servico> todosTipos = servicoDAO.listar();
                    List<Servico> servicosAtribuidos = encontro.getServicos();

                    this.listaServicos = todosTipos.stream().map(tipo -> {
                        servicosAtribuidos.stream()
                                .filter(sa -> sa.getTipo().equals(tipo.getTipo()))
                                .findFirst()
                                .ifPresent(atribuido -> {
                                    tipo.setIdMae(atribuido.getIdMae());
                                });
                        return tipo;
                    }).collect(Collectors.toList());

                } else {
                    this.listaServicos = servicoDAO.listar();
                    this.listaServicos.forEach(s -> s.setIdMae(0));
                }
            } catch (SQLException e) {
                // AQUI O ERRO É TRATADO E EVITA A COMPILAÇÃO DO ERRO EXTERNO
                JOptionPane.showMessageDialog(null, "Erro ao carregar lista de serviços: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
                this.listaServicos = new ArrayList<>();
            }
        }

        public List<Servico> getServicosParaSalvar() { return listaServicos.stream().filter(s -> s.getIdMae() != 0).collect(Collectors.toList()); }
        @Override public int getRowCount() { return listaServicos.size(); }
        @Override public int getColumnCount() { return COLUNAS.length; }
        @Override public String getColumnName(int column) { return COLUNAS[column]; }
        @Override public boolean isCellEditable(int rowIndex, int columnIndex) { return columnIndex == 1; }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            Servico servico = listaServicos.get(rowIndex);
            switch (columnIndex) {
                case 0: return servico.getTipo();
                case 1:
                    if (servico.getIdMae() != 0) {
                        return maesDisponiveis.stream()
                                .filter(m -> m.getIdMae() == servico.getIdMae())
                                .findFirst().orElse(null);
                    }
                    return null;
                default: return null;
            }
        }

        @Override public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 1) {
                Servico servico = listaServicos.get(rowIndex);
                Mae mae = (Mae) aValue;

                if (mae != null && mae.getIdMae() != 0) {
                    servico.setIdMae(mae.getIdMae());
                } else {
                    servico.setIdMae(0);
                }
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }

        @Override public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 1) { return Mae.class; }
            return String.class;
        }
    }
}