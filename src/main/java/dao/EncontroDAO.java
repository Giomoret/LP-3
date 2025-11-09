package dao;

import factory.ConnectionFactory;
import modelo.Encontro;
import modelo.Servico;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EncontroDAO {

    // ===================== INSERIR ENCONTRO =====================
    public void inserir(Encontro encontro) throws SQLException {
        String sqlEncontro = "INSERT INTO encontro (data_encontro, cancelado) VALUES (?, FALSE)";
        String sqlServico = "INSERT INTO servico (nome_servico, descricao, id_mae, id_encontro) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtEncontro = conn.prepareStatement(sqlEncontro, Statement.RETURN_GENERATED_KEYS)) {
                stmtEncontro.setDate(1, Date.valueOf(encontro.getDataEncontro()));
                stmtEncontro.executeUpdate();

                ResultSet rs = stmtEncontro.getGeneratedKeys();
                int idEncontro = 0;
                if (rs.next()) idEncontro = rs.getInt(1);

                try (PreparedStatement stmtServico = conn.prepareStatement(sqlServico)) {
                    for (Servico s : encontro.getServicos()) {
                        stmtServico.setString(1, s.getTipo());
                        stmtServico.setString(2, s.getDescricao());
                        if (s.getIdMae() != 0) stmtServico.setInt(3, s.getIdMae());
                        else stmtServico.setNull(3, Types.INTEGER);
                        stmtServico.setInt(4, idEncontro);
                        stmtServico.addBatch();
                    }
                    stmtServico.executeBatch();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // ===================== ATUALIZAR ENCONTRO =====================
    public boolean atualizar(Encontro encontro) throws SQLException {
        String sqlEncontro = "UPDATE encontro SET data_encontro = ?, cancelado = ? WHERE id_encontro = ?";
        String sqlExcluirServicos = "DELETE FROM servico WHERE id_encontro = ?";
        String sqlInserirServico = "INSERT INTO servico (nome_servico, descricao, id_mae, id_encontro) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement stmtEncontro = conn.prepareStatement(sqlEncontro);
                    PreparedStatement stmtExcluir = conn.prepareStatement(sqlExcluirServicos);
                    PreparedStatement stmtInserir = conn.prepareStatement(sqlInserirServico)
            ) {
                // Atualiza dados do encontro
                stmtEncontro.setDate(1, Date.valueOf(encontro.getDataEncontro()));
                stmtEncontro.setBoolean(2, encontro.isCancelado());
                stmtEncontro.setInt(3, encontro.getIdEncontro());
                stmtEncontro.executeUpdate();

                // Remove serviços antigos
                stmtExcluir.setInt(1, encontro.getIdEncontro());
                stmtExcluir.executeUpdate();

                // Reinsere serviços atualizados
                for (Servico s : encontro.getServicos()) {
                    stmtInserir.setString(1, s.getTipo());
                    stmtInserir.setString(2, s.getDescricao());
                    if (s.getIdMae() != 0) stmtInserir.setInt(3, s.getIdMae());
                    else stmtInserir.setNull(3, Types.INTEGER);
                    stmtInserir.setInt(4, encontro.getIdEncontro());
                    stmtInserir.addBatch();
                }

                stmtInserir.executeBatch();
                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // ===================== LISTAR ENCONTROS =====================
    public List<Encontro> listar() throws SQLException {
        List<Encontro> encontros = new ArrayList<>();

        String sql = """
                SELECT e.id_encontro, e.data_encontro, e.cancelado,
                       s.nome_servico, s.descricao, s.id_mae, m.nome AS nomeMae
                FROM encontro e
                LEFT JOIN servico s ON e.id_encontro = s.id_encontro
                LEFT JOIN mae m ON s.id_mae = m.id_mae
                ORDER BY e.data_encontro DESC
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Encontro encontroAtual = null;
            int ultimoId = -1;

            while (rs.next()) {
                int idEncontro = rs.getInt("id_encontro");

                if (idEncontro != ultimoId) {
                    encontroAtual = new Encontro();
                    encontroAtual.setIdEncontro(idEncontro);
                    encontroAtual.setDataEncontro(rs.getDate("data_encontro").toLocalDate());
                    encontroAtual.setCancelado(rs.getBoolean("cancelado"));
                    encontroAtual.setServicos(new ArrayList<>());
                    encontros.add(encontroAtual);
                    ultimoId = idEncontro;
                }

                if (encontroAtual != null) {
                    Servico s = new Servico();
                    s.setTipo(rs.getString("nome_servico"));
                    s.setDescricao(rs.getString("descricao"));
                    s.setIdMae(rs.getInt("id_mae"));
                    s.setNomeMae(rs.getString("nomeMae"));
                    encontroAtual.getServicos().add(s);
                }
            }
        }

        return encontros;
    }

    // ===================== BUSCAR POR ID =====================
    public Encontro buscarPorId(int id) throws SQLException {
        Encontro encontro = null;

        String sql = """
                SELECT e.id_encontro, e.data_encontro, e.cancelado,
                       s.nome_servico, s.descricao, s.id_mae, m.nome AS nomeMae
                FROM encontro e
                LEFT JOIN servico s ON e.id_encontro = s.id_encontro
                LEFT JOIN mae m ON s.id_mae = m.id_mae
                WHERE e.id_encontro = ?
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (encontro == null) {
                        encontro = new Encontro();
                        encontro.setIdEncontro(rs.getInt("id_encontro"));
                        encontro.setDataEncontro(rs.getDate("data_encontro").toLocalDate());
                        encontro.setCancelado(rs.getBoolean("cancelado"));
                        encontro.setServicos(new ArrayList<>());
                    }

                    Servico s = new Servico();
                    s.setTipo(rs.getString("nome_servico"));
                    s.setDescricao(rs.getString("descricao"));
                    s.setIdMae(rs.getInt("id_mae"));
                    s.setNomeMae(rs.getString("nomeMae"));
                    encontro.getServicos().add(s);
                }
            }
        }

        return encontro;
    }

    // ===================== CANCELAR ENCONTRO (lógico) =====================
    public void cancelar(int id) throws SQLException {
        String sql = "UPDATE encontro SET cancelado = TRUE WHERE id_encontro = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // ===================== EXCLUIR ENCONTRO (físico) =====================
    public void excluir(int id) throws SQLException {
        String sqlServicos = "DELETE FROM servico WHERE id_encontro = ?";
        String sqlEncontro = "DELETE FROM encontro WHERE id_encontro = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtServ = conn.prepareStatement(sqlServicos);
                 PreparedStatement stmtEnc = conn.prepareStatement(sqlEncontro)) {

                stmtServ.setInt(1, id);
                stmtServ.executeUpdate();

                stmtEnc.setInt(1, id);
                stmtEnc.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // ===================== BUSCAR SERVIÇOS POR DATA (para relatório) =====================
    public List<Servico> buscarServicosPorData(LocalDate data) throws SQLException {
        List<Servico> servicos = new ArrayList<>();

        String sql = """
                SELECT s.nome_servico, s.descricao, m.nome AS nomeMae
                FROM servico s
                JOIN encontro e ON s.id_encontro = e.id_encontro
                LEFT JOIN mae m ON s.id_mae = m.id_mae
                WHERE e.data_encontro = ? AND e.cancelado = FALSE
                ORDER BY s.nome_servico
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(data));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Servico servico = new Servico();
                    servico.setTipo(rs.getString("nome_servico"));
                    servico.setDescricao(rs.getString("descricao"));
                    servico.setNomeMae(rs.getString("nomeMae"));
                    servicos.add(servico);
                }
            }
        }

        return servicos;
    }
}
