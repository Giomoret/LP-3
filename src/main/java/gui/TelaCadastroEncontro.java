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
import java.sql.SQLException;
import java.util.ArrayList;

public class TelaCadastroEncontro extends JDialog {

    private TelaListaEncontros owner;
    private Encontro encontroEmEdicao;

    private final EncontroDAO encontroDAO = new EncontroDAO();
    private final MaeDAO maeDAO = new MaeDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();

    // Componentes
    private JTextField txtData;
    private JButton btnSalvar;
    private JTable tabelaServicos;
    private ServicoTableModel servicoTableModel;

    private List<Mae> maesCadastradas;

    // ===================== CONSTRUTORES =====================
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

    // ===================== INICIALIZAÇÃO =====================
    private void inicializar(Frame owner) {
        try {
            this.maesCadastradas = maeDAO.listar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(owner,
                    "Erro ao carregar lista de Mães. A tabela não será funcional: " + e.getMessage(),
                    "Erro de Inicialização do Banco", JOptionPane.ERROR_MESSAGE);
            this.maesCadastradas = new ArrayList<>();
        }

        setSize(700, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel painelSuperior = criarPainelSuperior();
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
        servicoTableModel = new ServicoTableModel(encontroEmEdicao, servicoDAO, maesCadastradas);
        tabelaServicos = new JTable(servicoTableModel);

        // ComboBox de Mães
        Vector<Mae> itensMae = new Vector<>();
        Mae placeholder = new Mae();
        placeholder.setNome("--- Sem Responsável ---");
        itensMae.add(placeholder);
        itensMae.addAll(maesCadastradas);

        JComboBox<Mae> cmbMaes = new JComboBox<>(itensMae);
        tabelaServicos.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cmbMaes));

        // Larguras
        tabelaServicos.getColumnModel().getColumn(0).setPreferredWidth(180);
        tabelaServicos.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabelaServicos.getColumnModel().getColumn(2).setPreferredWidth(250);

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

    // ===================== SALVAR =====================
    private void salvarEncontro() {
        boolean sucesso = false;
        if (tabelaServicos.isEditing()) {
            tabelaServicos.getCellEditor().stopCellEditing();
        }

        try {
            LocalDate data = LocalDate.parse(txtData.getText());

            if (data.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this,
                        "Não é permitido cadastrar ou editar encontros em datas passadas!",
                        "Data Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (encontroEmEdicao != null && encontroEmEdicao.getIdEncontro() != 0) {
                if (encontroEmEdicao.getDataEncontro().isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this,
                            "Encontros passados não podem ser editados.",
                            "Edição Bloqueada", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

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
                JOptionPane.showMessageDialog(this,
                        "Erro ao salvar encontro (DAO retornou false).",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (java.time.format.DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(this,
                    "Erro: Formato de data inválido. Use YYYY-MM-DD.",
                    "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro de banco ao salvar ou atualizar o encontro: " + ex.getMessage(),
                    "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro inesperado: " + ex.getMessage(),
                    "Erro Geral", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ===================== TABELA DE SERVIÇOS =====================
    private class ServicoTableModel extends AbstractTableModel {
        private final String[] COLUNAS = {"Serviço", "Responsável", "Descrição"};
        private List<Servico> listaServicos;
        private final List<Mae> maesDisponiveis;

        public ServicoTableModel(Encontro encontro, ServicoDAO servicoDAO, List<Mae> maes) {
            this.maesDisponiveis = maes;

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
                                    tipo.setDescricao(atribuido.getDescricao());
                                });
                        return tipo;
                    }).collect(Collectors.toList());

                } else {
                    this.listaServicos = servicoDAO.listar();
                    this.listaServicos.forEach(s -> {
                        s.setIdMae(0);
                        s.setDescricao("");
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Erro ao carregar lista de serviços: " + e.getMessage(),
                        "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
                this.listaServicos = new ArrayList<>();
            }
        }

        // ✅ CORRIGIDO: tratamento seguro de null em getDescricao()
        public List<Servico> getServicosParaSalvar() {
            return listaServicos.stream()
                    .filter(s -> s.getIdMae() != 0 ||
                            (s.getDescricao() != null && !s.getDescricao().isEmpty()))
                    .collect(Collectors.toList());
        }

        @Override
        public int getRowCount() { return listaServicos.size(); }

        @Override
        public int getColumnCount() { return COLUNAS.length; }

        @Override
        public String getColumnName(int column) { return COLUNAS[column]; }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1 || columnIndex == 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
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
                case 2:
                    return servico.getDescricao() != null ? servico.getDescricao() : "";
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Servico servico = listaServicos.get(rowIndex);
            if (columnIndex == 1) {
                Mae mae = (Mae) aValue;
                if (mae != null && mae.getIdMae() != 0) {
                    servico.setIdMae(mae.getIdMae());
                } else {
                    servico.setIdMae(0);
                }
            } else if (columnIndex == 2) {
                servico.setDescricao(aValue != null ? aValue.toString() : "");
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 1) return Mae.class;
            return String.class;
        }
    }
}
