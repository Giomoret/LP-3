package dao;

import factory.ConnectionFactory;
import modelo.Encontro;
import modelo.Servico;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EncontroDAO {

    private final ServicoDAO servicoDAO = new ServicoDAO();

    // ================== INSERIR ENCONTRO (COMPARTILHA CONEXÃO) ==================
    public void inserir(Encontro encontro) throws SQLException {
        String sql = "INSERT INTO encontro (data_encontro, cancelado) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setDate(1, Date.valueOf(encontro.getDataEncontro()));
                stmt.setBoolean(2, encontro.isCancelado());
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int idEncontro = rs.getInt(1);

                    if (encontro.getServicos() != null) {
                        for (Servico s : encontro.getServicos()) {
                            // MUDANÇA CRÍTICA: Passa a conexão (conn) para ServicoDAO.inserir()
                            servicoDAO.inserir(conn, s, idEncontro);
                        }
                    }
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Erro na transação de inserção: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
    }

    // ================== ATUALIZAR ENCONTRO (COMPARTILHA CONEXÃO) ==================
    public boolean atualizar(Encontro encontro) throws SQLException {
        String sqlEncontro = "UPDATE encontro SET data_encontro = ?, cancelado = ? WHERE id_encontro = ?";

        boolean sucesso = false;

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtEncontro = conn.prepareStatement(sqlEncontro)) {
                stmtEncontro.setDate(1, Date.valueOf(encontro.getDataEncontro()));
                stmtEncontro.setBoolean(2, encontro.isCancelado());
                stmtEncontro.setInt(3, encontro.getIdEncontro());

                if (stmtEncontro.executeUpdate() > 0) {
                    deletarServicosDoEncontro(conn, encontro.getIdEncontro()); // Auxiliar já usa a conexão

                    if (encontro.getServicos() != null) {
                        for (Servico s : encontro.getServicos()) {
                            // MUDANÇA CRÍTICA: Passa a conexão (conn) para ServicoDAO.inserir()
                            servicoDAO.inserir(conn, s, encontro.getIdEncontro());
                        }
                    }

                    conn.commit();
                    sucesso = true;
                } else {
                    conn.rollback();
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        return sucesso;
    }

    // ================== MÉTODO AUXILIAR PARA ATUALIZAR (DELETAR SERVIÇOS ANTIGOS) ==================
    private void deletarServicosDoEncontro(Connection conn, int idEncontro) throws SQLException {
        String sql = "DELETE FROM servico WHERE id_encontro = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEncontro);
            stmt.executeUpdate();
        }
    }

    // ================== BUSCAR ENCONTRO POR ID ==================
    public Encontro buscarPorId(int idEncontro) throws SQLException {
        Encontro encontro = null;
        String sql = "SELECT * FROM encontro WHERE id_encontro = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEncontro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    encontro = new Encontro();
                    encontro.setIdEncontro(rs.getInt("id_encontro"));
                    encontro.setDataEncontro(rs.getDate("data_encontro").toLocalDate());
                    encontro.setCancelado(rs.getBoolean("cancelado"));

                    encontro.setServicos(servicoDAO.listarPorEncontro(encontro.getIdEncontro()));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar encontro por ID: " + e.getMessage());
            throw e;
        }
        return encontro;
    }

    // ================== LISTAR ENCONTROS (CORRIGIDO PARA INCLUIR CANCELADOS) ==================
    public List<Encontro> listar() throws SQLException {
        List<Encontro> encontros = new ArrayList<>();
        // CORREÇÃO: Remove 'WHERE cancelado = 0' para listar TODOS os encontros.
        String sql = "SELECT * FROM encontro ORDER BY data_encontro DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Encontro encontro = new Encontro();
                encontro.setIdEncontro(rs.getInt("id_encontro"));
                encontro.setDataEncontro(rs.getDate("data_encontro").toLocalDate());
                encontro.setCancelado(rs.getBoolean("cancelado"));

                encontro.setServicos(servicoDAO.listarPorEncontro(encontro.getIdEncontro()));

                encontros.add(encontro);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar encontros: " + e.getMessage());
            throw e;
        }

        return encontros;
    }

    // ================== EXCLUIR ENCONTRO SELECIONADO ==================
    public void excluir(int idEncontro) throws SQLException {
        String sql = "DELETE FROM encontro WHERE id_encontro = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEncontro);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao excluir encontro: " + e.getMessage());
            throw e;
        }
    }

    // ================== CANCELAR ENCONTRO ==================
    // Este método faz o UPDATE necessário para a funcionalidade de "cancelar"
    public void cancelar(int idEncontro) throws SQLException {
        String sql = "UPDATE encontro SET cancelado = true WHERE id_encontro = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEncontro);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao cancelar encontro: " + e.getMessage());
            throw e;
        }
    }

    // ================== EXCLUIR ENCONTROS FUTUROS ==================
    public void excluirFuturo() throws SQLException {
        String sql = "DELETE FROM encontro WHERE data_encontro > CURRENT_DATE AND cancelado = false";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Número de encontros futuros excluídos: " + linhasAfetadas);

        } catch (SQLException e) {
            System.out.println("Erro ao excluir encontros futuros: " + e.getMessage());
            throw e;
        }
    }

    // ================== BUSCAR SERVIÇOS POR DATA ==================
    public List<Servico> buscarServicosPorData(LocalDate data) throws SQLException {
        List<Servico> servicos = new ArrayList<>();

        String sql = "SELECT s.*, m.nome as nomeMae FROM servico s " +
                "JOIN encontro e ON s.id_encontro = e.id_encontro " +
                "LEFT JOIN mae m ON s.id_mae = m.id_mae " +
                "WHERE e.data_encontro = ? AND e.cancelado = false";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(data));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Servico s = new Servico();
                    s.setTipo(rs.getString("nome_servico"));
                    s.setNomeMae(rs.getString("nomeMae"));

                    servicos.add(s);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar serviços por data: " + e.getMessage());
            throw e;
        }
        return servicos;
    }
}