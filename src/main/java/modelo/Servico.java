package modelo;

public class Servico {

    private int idServico;         // equivale ao id da tabela `servico`
    private String tipo;           // MÚSICA / TERÇO / FORMAÇÃO... (nome_servico no SQL)

    // **MANTIDO:** ID da mãe (int) para comunicação com o banco de dados
    private int idMae;             // FK para a tabela mae (corresponde a 'id_mae' no SQL)

    // **NOVO:** Campo para armazenar o nome da mãe (obtido via JOIN no DAO)
    private String nomeMae;

    private String descricao;      // descrição opcional da atividade
    private int idEncontro;        // FK (chave estrangeira do encontro)

    public Servico() {}

    // Construtor ajustado para aceitar int para o ID da Mãe (mantendo a consistência)
    public Servico(int idServico, String tipo, int idMae, String descricao, int idEncontro) {
        this.idServico = idServico;
        this.tipo = tipo;
        this.idMae = idMae;
        // Não é necessário adicionar nomeMae ao construtor se ele for populado apenas pelo DAO/ResultSet
        this.descricao = descricao;
        this.idEncontro = idEncontro;
    }

    // -----------------------
    // GETTERS E SETTERS
    // -----------------------

    public int getIdServico() {
        return idServico;
    }

    public void setIdServico(int idServico) {
        this.idServico = idServico;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdMae() {
        return idMae;
    }

    public void setIdMae(int idMae) {
        this.idMae = idMae;
    }

    // **NOVOS MÉTODOS PARA O NOME DA MÃE**
    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }
    // ------------------------------------

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getIdEncontro() {
        return idEncontro;
    }

    public void setIdEncontro(int idEncontro) {
        this.idEncontro = idEncontro;
    }
}