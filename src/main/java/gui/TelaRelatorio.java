package gui;

import dao.EncontroDAO;
import modelo.Servico;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException; // NECESSÁRIO para FileWriter
import java.net.URL;
import java.util.List;
import java.sql.SQLException; // NECESSÁRIO para o DAO
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TelaRelatorio extends JFrame {

    private JTextField txtData = new JTextField(); // Data no formato yyyy-MM-dd

    public TelaRelatorio() {
        setTitle("Gerar Relatório do Encontro");
        try {
            URL resource = TelaPrincipal.class.getResource("/Church_white.png");
            Image icon = new ImageIcon(resource).getImage();
            setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Erro ao carregar o ícone: " + e.getMessage());
        }
        setSize(350, 150);
        setLayout(new GridLayout(2, 2));

        this.setLocationRelativeTo(null);

        add(new JLabel("Data do Encontro (yyyy-MM-dd):"));
        add(txtData);

        JButton btnGerar = new JButton("Gerar Relatório");
        add(btnGerar);

        btnGerar.addActionListener(e -> gerarRelatorio());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void gerarRelatorio() {
        String dataDigitadaStr = txtData.getText();
        LocalDate dataFormatada;
        List<Servico> servicos;

        // O bloco try-catch principal engloba TODA a lógica que pode lançar exceções.
        try {
            // 1. TRATAMENTO DA DATA (DateTimeParseException)
            dataFormatada = LocalDate.parse(dataDigitadaStr);

            EncontroDAO dao = new EncontroDAO();

            // 2. ACESSO AO BANCO DE DADOS (SQLException) - LINHA 62 (aproximadamente)
            servicos = dao.buscarServicosPorData(dataFormatada);

            if (servicos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhum encontro encontrado nessa data.");
                return;
            }

            // 3. OPERAÇÃO DE ARQUIVO (FileWriter / IOException)
            try (FileWriter fw = new FileWriter("Relatorio_" + dataDigitadaStr + ".txt")) {

                fw.write("Data do Encontro: " + dataDigitadaStr + "\n\n");
                fw.write("Serviços:\n\n");

                for (Servico s : servicos) {
                    String nomeMae = s.getNomeMae();
                    String responsavelStr = (nomeMae == null || nomeMae.isEmpty()) ? "SEM RESPONSÁVEL" : nomeMae;

                    StringBuilder linha = new StringBuilder();
                    linha.append(s.getTipo()).append(": ").append(responsavelStr);

                    if (s.getDescricao() != null && !s.getDescricao().isBlank())
                        linha.append("  ->  ").append(s.getDescricao());

                    fw.write(linha.toString() + "\n");
                }

                JOptionPane.showMessageDialog(this,
                        "Relatório gerado com sucesso!\nArquivo: Relatorio_" + dataDigitadaStr + ".txt");

            } catch (IOException ioe) {
                // Captura especificamente erros de arquivo/escrita
                JOptionPane.showMessageDialog(this, "Erro de Arquivo ao gerar relatório: " + ioe.getMessage(), "Erro de IO", JOptionPane.ERROR_MESSAGE);
            }

        } catch (DateTimeParseException dtpe) {
            // Captura erro de formato de data
            JOptionPane.showMessageDialog(this, "Erro: Verifique o formato da data. Use YYYY-MM-DD.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException sqle) {
            // Captura erro do DAO (buscarServicosPorData)
            JOptionPane.showMessageDialog(this, "Erro de Banco de Dados: Falha ao buscar serviços.", "Erro SQL", JOptionPane.ERROR_MESSAGE);
            sqle.printStackTrace();
        }
        // Não precisamos de um catch geral (Exception e) se cobrimos os principais
    }
}