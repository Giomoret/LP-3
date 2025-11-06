package gui;

import dao.EncontroDAO;
import modelo.Servico;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.util.List;

public class TelaRelatorio extends JFrame {

    private JTextField txtData = new JTextField(); // Data no formato yyyy-MM-dd

    public TelaRelatorio() {
        setTitle("Gerar Relatório do Encontro");
        setSize(350, 150);
        setLayout(new GridLayout(2, 2));

        // CORREÇÃO: Adicionando esta linha para centralizar a janela na tela
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
        String dataDigitada = txtData.getText();

        EncontroDAO dao = new EncontroDAO();
        List<Servico> servicos = dao.buscarServicosPorData(dataDigitada);

        if (servicos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum encontro encontrado nessa data.");
            return;
        }

        try {
            FileWriter fw = new FileWriter("Relatorio_" + dataDigitada + ".txt");

            fw.write("Data do Encontro: " + dataDigitada + "\n\n");
            fw.write("Serviços:\n\n");

            for (Servico s : servicos) {
                String nomeMae = s.getNomeMae();
                String responsavel = (nomeMae == null || nomeMae.isEmpty()) ? "" : nomeMae;

                StringBuilder linha = new StringBuilder();
                linha.append(s.getTipo()).append(": ").append(responsavel);

                if (s.getDescricao() != null && !s.getDescricao().isBlank())
                    linha.append("  ->  ").append(s.getDescricao());

                fw.write(linha.toString() + "\n");
            }

            fw.close();

            JOptionPane.showMessageDialog(this,
                    "Relatório gerado com sucesso!\nArquivo: Relatorio_" + dataDigitada + ".txt");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao gerar relatório: " + e.getMessage());
        }
    }
}