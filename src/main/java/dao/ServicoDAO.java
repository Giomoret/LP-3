package dao;

import factory.ConnectionFactory;
import modelo.Servico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {

    // INSERE UM SERVIÇO VINCULADO A UM ENCONTRO
    public void inserir(Servico servico, int idEncontro) {
        String sql = "INSERT INTO servico (tipo, mae, descricao, encontro_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, servico.getTipo());     // ALTERADO AQUI ✅
            stmt.setString(2, servico.getMae());
            stmt.setString(3, servico.getDescricao());
            stmt.setInt(4, idEncontro);

            stmt.execute();

        } catch (SQLException e) {
            System.out.println("Erro ao inserir serviço: " + e.getMessage());
        }
    }

    // LISTA OS SERVIÇOS DE UM ENCONTRO
    public List<Servico> listarPorEncontro(int idEncontro) {
        List<Servico> servicos = new ArrayList<>();

        String sql = "SELECT * FROM servico WHERE encontro_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEncontro);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Servico s = new Servico();
                s.setIdServico(rs.getInt("id_servico"));
                s.setTipo(rs.getString("tipo"));      // ALTERADO AQUI ✅
                s.setMae(rs.getString("mae"));
                s.setDescricao(rs.getString("descricao"));
                s.setIdEncontro(rs.getInt("encontro_id"));

                servicos.add(s);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar serviços: " + e.getMessage());
        }

        return servicos;
    }
}
