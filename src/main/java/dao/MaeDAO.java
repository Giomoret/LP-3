package dao;

import modelo.Mae;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import factory.ConnectionFactory;

public class MaeDAO {

    // ================== INSERIR MÃE (CORRIGIDO: LANÇA EXCEÇÃO) ==================
    public void inserir(Mae mae) throws SQLException {
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

        } // <-- catch removido
    }

    // ================== ATUALIZAR MÃE (CORRIGIDO: LANÇA EXCEÇÃO) ==================
    public void atualizar(Mae mae) throws SQLException {
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

        } // <-- catch removido
    }

    // ================== EXCLUIR MÃE (CORRIGIDO: LANÇA EXCEÇÃO) ==================
    public void excluir(int idMae) throws SQLException {
        String sql = "DELETE FROM mae WHERE id_mae = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMae);
            stmt.executeUpdate();

        } // <-- catch removido
    }

    // ================== LISTAR TODAS AS MÃES (CORRIGIDO: LANÇA EXCEÇÃO) ==================
    public List<Mae> listar() throws SQLException {
        List<Mae> maes = new ArrayList<>();
        String sql = "SELECT * FROM mae ORDER BY nome";

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

        } // <-- catch removido

        return maes;
    }

    // ================== LISTAR ANIVERSARIANTES DO MÊS (CORRIGIDO: LANÇA EXCEÇÃO) ==================
    public List<Mae> listarAniversariantesDoMes() throws SQLException {
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

        } // <-- catch removido

        return lista;
    }

    // ================== BUSCAR POR NOME (CORRIGIDO: LANÇA EXCEÇÃO) ==================
    public Mae buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM mae WHERE nome = ? LIMIT 1";
        Mae mae = null; // Inicializa a variável fora do try

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                mae = new Mae();
                mae.setIdMae(rs.getInt("id_mae"));
                mae.setNome(rs.getString("nome"));

                // Preenche os outros campos se necessário, ou use apenas id e nome
            }

        } // <-- catch removido

        return mae;
    }
}