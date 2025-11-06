package dao;

import factory.ConnectionFactory;
import modelo.Servico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {

    public void inserir(Servico servico, int idEncontro) {
        // Colunas: nome_servico, descricao, id_mae, id_encontro
        String sql = "INSERT INTO servico (nome_servico, descricao, id_mae, id_encontro) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, servico.getTipo());
            stmt.setString(2, servico.getDescricao());

            // AGORA FUNCIONA: servico.getIdMae() retorna um int.
            if (servico.getIdMae() > 0) {
                stmt.setInt(3, servico.getIdMae());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }

            stmt.setInt(4, idEncontro);

            stmt.execute();

        } catch (SQLException e) {
            System.out.println("Erro ao inserir serviço: " + e.getMessage());
        }
    }

    public List<Servico> listarPorEncontro(int idEncontro) {
        List<Servico> servicos = new ArrayList<>();

        // CORREÇÃO CRUCIAL: Uso do LEFT JOIN com a tabela 'mae' para buscar o nome da mãe (m.nome)
        String sql = "SELECT s.*, m.nome AS nome_mae " +
                "FROM servico s " +
                "LEFT JOIN mae m ON s.id_mae = m.id_mae " + // Junta as tabelas pelo ID da mãe
                "WHERE s.id_encontro = ?";
        // Usamos 's.*' para pegar todas as colunas de servico e 'm.nome AS nome_mae' para pegar o nome
        // 'LEFT JOIN' garante que serviços sem mãe responsável (id_mae = NULL) ainda sejam listados.

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEncontro);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Servico s = new Servico();
                s.setIdServico(rs.getInt("id_servico"));
                s.setTipo(rs.getString("nome_servico"));

                // Lemos o ID da mãe (padrão)
                s.setIdMae(rs.getInt("id_mae"));

                // NOVIDADE: Lemos o nome da mãe usando o alias 'nome_mae' do SQL
                s.setNomeMae(rs.getString("nome_mae"));

                s.setDescricao(rs.getString("descricao"));
                s.setIdEncontro(rs.getInt("id_encontro"));

                servicos.add(s);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar serviços: " + e.getMessage());
        }

        return servicos;
    }
}