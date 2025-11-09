package dao;

import factory.ConnectionFactory;
import modelo.Servico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {

    // ================== INSERIR COM CONEXÃO COMPARTILHADA ==================
    /**
     * Insere um serviço vinculado a um encontro (usado dentro do EncontroDAO).
     */
    public void inserir(Connection conn, Servico s, int idEncontro) throws SQLException {
        String sql = "INSERT INTO servico (nome_servico, descricao, id_mae, id_encontro) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            // nome_servico
            if (s.getTipo() != null && !s.getTipo().isEmpty()) {
                stmt.setString(1, s.getTipo());
            } else {
                stmt.setNull(1, Types.VARCHAR);
            }

            // descricao
            if (s.getDescricao() != null && !s.getDescricao().isEmpty()) {
                stmt.setString(2, s.getDescricao());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }

            // id_mae
            if (s.getIdMae() != 0) {
                stmt.setInt(3, s.getIdMae());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setInt(4, idEncontro);
            stmt.executeUpdate();
        }
    }

    // ================== LISTAR POR ENCONTRO (AGORA COM NOME DA MÃE) ==================
    /**
     * Retorna os serviços vinculados a um encontro, com nome da mãe (responsável).
     */
    public List<Servico> listarPorEncontro(int idEncontro) throws SQLException {
        List<Servico> servicos = new ArrayList<>();

        String sql = """
            SELECT s.id_servico, s.nome_servico, s.descricao, s.id_mae, m.nome AS nomeMae
            FROM servico s
            LEFT JOIN mae m ON s.id_mae = m.id_mae
            WHERE s.id_encontro = ?
            ORDER BY s.nome_servico
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEncontro);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Servico s = new Servico();
                    s.setIdServico(rs.getInt("id_servico"));
                    s.setTipo(rs.getString("nome_servico"));
                    s.setDescricao(rs.getString("descricao"));
                    s.setIdMae(rs.getInt("id_mae"));
                    s.setNomeMae(rs.getString("nomeMae")); // 👈 ESSENCIAL
                    servicos.add(s);
                }
            }
        }
        return servicos;
    }

    // ================== LISTAR SERVIÇOS FIXOS (BASE) ==================
    /**
     * Retorna todos os tipos de serviço base (usados na criação de encontros).
     * São registros sem vínculo com um encontro específico.
     */
    public List<Servico> listar() throws SQLException {
        List<Servico> servicos = new ArrayList<>();

        String sql = """
            SELECT DISTINCT nome_servico, descricao
            FROM servico
            WHERE id_encontro IS NULL
            ORDER BY nome_servico
        """;

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

    // ================== EXCLUSÃO FÍSICA POR ENCONTRO (CASO PRECISE LIMPAR) ==================
    /**
     * Remove todos os serviços associados a um encontro específico.
     * Usado em caso de exclusão total de encontro.
     */
    public void excluirPorEncontro(Connection conn, int idEncontro) throws SQLException {
        String sql = "DELETE FROM servico WHERE id_encontro = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEncontro);
            stmt.executeUpdate();
        }
    }
}
