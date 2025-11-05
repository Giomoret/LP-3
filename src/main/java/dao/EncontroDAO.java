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

    // ================== INSERIR ENCONTRO ==================
    public void inserir(Encontro encontro) {
        String sql = "INSERT INTO encontro (data_encontro, cancelado) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(encontro.getDataEncontro()));
            stmt.setBoolean(2, encontro.isCancelado());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idEncontro = rs.getInt(1);

                // Insere os serviços vinculados a este encontro
                if (encontro.getServicos() != null) {
                    for (Servico s : encontro.getServicos()) {
                        servicoDAO.inserir(s, idEncontro);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao inserir encontro: " + e.getMessage());
        }
    }

    // ================== LISTAR ENCONTROS ==================
    public List<Encontro> listar() {
        List<Encontro> encontros = new ArrayList<>();
        String sql = "SELECT * FROM encontro ORDER BY data_encontro DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Encontro e = new Encontro();
                e.setIdEncontro(rs.getInt("id_encontro"));
                e.setDataEncontro(rs.getDate("data_encontro").toLocalDate());
                e.setCancelado(rs.getBoolean("cancelado"));

                // Carrega serviços associados
                e.setServicos(servicoDAO.listarPorEncontro(e.getIdEncontro()));

                encontros.add(e);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar encontros: " + e.getMessage());
        }

        return encontros;
    }

    // ================== CANCELAMENTO LÓGICO (exclusão) ==================
    public void cancelar(int idEncontro) {
        String sql = "UPDATE encontro SET cancelado = TRUE WHERE id_encontro = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEncontro);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao cancelar encontro: " + e.getMessage());
        }
    }

    // ================== EXCLUIR SE FOR FUTURO ==================
    public void excluirFuturo(int idEncontro, LocalDate dataEncontro) {
        if (dataEncontro.isAfter(LocalDate.now())) {
            String sql = "DELETE FROM encontro WHERE id_encontro = ?";
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, idEncontro);
                stmt.executeUpdate();

            } catch (SQLException e) {
                System.out.println("Erro ao excluir encontro futuro: " + e.getMessage());
            }
        } else {
            cancelar(idEncontro);
        }
    }

    // ================== USADO PELO RELATÓRIO ==================
    public List<Servico> buscarServicosPorData(String data) {
        List<Servico> servicos = new ArrayList<>();

        String sql = """
            SELECT s.tipo, s.mae
            FROM servico s
            INNER JOIN encontro e ON s.encontro_id = e.id_encontro
            WHERE e.data_encontro = ?
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, data);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Servico s = new Servico();
                s.setTipo(rs.getString("tipo"));
                s.setMae(rs.getString("mae"));
                servicos.add(s);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar serviços para relatório: " + e.getMessage());
        }

        return servicos;
    }
}
