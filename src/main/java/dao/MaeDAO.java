package dao;

import modelo.Mae;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import factory.ConnectionFactory;

public class MaeDAO {

    public void inserir(Mae mae) {
        String sql = "INSERT INTO mae (nome, telefone, endereco, data_aniversario) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mae.getNome());
            stmt.setString(2, mae.getTelefone());
            stmt.setString(3, mae.getEndereco());

            if (mae.getDataAniversario() != null) {
                stmt.setDate(4, Date.valueOf(mae.getDataAniversario()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void atualizar(Mae mae) {
        String sql = "UPDATE mae SET nome = ?, telefone = ?, endereco = ?, data_aniversario = ? WHERE id_mae = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mae.getNome());
            stmt.setString(2, mae.getTelefone());
            stmt.setString(3, mae.getEndereco());

            if (mae.getDataAniversario() != null) {
                stmt.setDate(4, Date.valueOf(mae.getDataAniversario()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setInt(5, mae.getIdMae());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(int idMae) {
        String sql = "DELETE FROM mae WHERE id_mae = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMae);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Mae> listar() {
        List<Mae> maes = new ArrayList<>();
        String sql = "SELECT * FROM mae";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Mae mae = new Mae();
                mae.setIdMae(rs.getInt("id_mae"));
                mae.setNome(rs.getString("nome"));
                mae.setTelefone(rs.getString("telefone"));
                mae.setEndereco(rs.getString("endereco"));

                Date dataSql = rs.getDate("data_aniversario");
                if (dataSql != null) {
                    mae.setDataAniversario(dataSql.toLocalDate());
                }

                maes.add(mae);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return maes;
    }

    public List<Mae> listarAniversariantesDoMes() {
        List<Mae> lista = new ArrayList<>();
        String sql = "SELECT * FROM aniversariantes_mes";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Mae mae = new Mae();
                mae.setIdMae(rs.getInt("id_mae"));
                mae.setNome(rs.getString("nome"));
                mae.setTelefone(rs.getString("telefone"));
                mae.setEndereco(rs.getString("endereco"));

                Date dataSql = rs.getDate("data_aniversario");
                if (dataSql != null) {
                    mae.setDataAniversario(dataSql.toLocalDate());
                }

                lista.add(mae);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Mae buscarPorNome(String nome) {
        String sql = "SELECT * FROM mae WHERE nome = ? LIMIT 1";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Mae mae = new Mae();
                mae.setIdMae(rs.getInt("id_mae"));
                mae.setNome(rs.getString("nome"));
                return mae;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}