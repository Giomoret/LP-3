package dao;

import factory.ConnectionFactory;
import modelo.Servico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {

    // ================== MÉTODO INSERIR COM CONEXÃO COMPARTILHADA (CRÍTICO) ==================
    /**
     * Insere a atribuição de um serviço utilizando uma conexão externa (compartilhada).
     * ESSENCIAL para transações aninhadas (EncontroDAO -> ServicoDAO).
     * @param conn Conexão externa do EncontroDAO.
     * @param s O objeto Servico.
     * @param idEncontro O ID do encontro ao qual pertence.
     */
    public void inserir(Connection conn, Servico s, int idEncontro) throws SQLException {
        // O SQL está correto para salvar nome, descrição, id_mae e id_encontro
        String sql = "INSERT INTO servico (nome_servico, descricao, id_mae, id_encontro) VALUES (?, ?, ?, ?)";

        // Usa a conexão passada como argumento (conn)
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Tratamento de Nulidade em String (nome_servico)
            if (s.getTipo() != null && !s.getTipo().isEmpty()) {
                stmt.setString(1, s.getTipo());
            } else {
                stmt.setNull(1, Types.VARCHAR);
            }

            // Tratamento de Nulidade em String (descricao)
            if (s.getDescricao() != null && !s.getDescricao().isEmpty()) {
                stmt.setString(2, s.getDescricao());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }

            // CRÍTICO: Seta o ID da Mae (Responsável). Se for 0, seta como SQL NULL.
            if (s.getIdMae() != 0) {
                stmt.setInt(3, s.getIdMae());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setInt(4, idEncontro);
            stmt.executeUpdate();

        }
    }

    // ================== MÉTODO LISTAR POR ENCONTRO (CARREGA RESPONSÁVEL) ==================
    public List<Servico> listarPorEncontro(int idEncontro) throws SQLException {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT id_servico, nome_servico, descricao, id_mae FROM servico WHERE id_encontro = ?";

        // Usa uma nova conexão, pois é uma operação de leitura (não transacional)
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEncontro);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Servico s = new Servico();
                    s.setTipo(rs.getString("nome_servico"));
                    s.setDescricao(rs.getString("descricao"));
                    s.setIdMae(rs.getInt("id_mae"));

                    servicos.add(s);
                }
            }
        }
        return servicos;
    }

    // ================== MÉTODO LISTAR TODOS OS SERVIÇOS FIXOS (PARA TABELA/COMBOBOX) ==================
    public List<Servico> listar() throws SQLException {
        List<Servico> servicos = new ArrayList<>();

        String sql = "SELECT DISTINCT nome_servico, descricao FROM servico WHERE id_encontro IS NULL ORDER BY nome_servico";

        // Usa uma nova conexão
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Servico s = new Servico();
                s.setTipo(rs.getString("nome_servico"));
                s.setDescricao(rs.getString("descricao"));

                servicos.add(s);
            }
        }
        return servicos;
    }
}