package modelo;

public class Servico {

    private int idServico;         // equivale ao id da tabela `servico`
    private String tipo;           // MÚSICA / TERÇO / FORMAÇÃO...
    private String mae;            // nome da mãe responsável (string)
    private String descricao;      // descrição opcional da atividade
    private int idEncontro;        // FK (chave estrangeira do encontro)

    public Servico() {}

    public Servico(int idServico, String tipo, String mae, String descricao, int idEncontro) {
        this.idServico = idServico;
        this.tipo = tipo;
        this.mae = mae;
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

    public String getMae() {
        return mae;
    }

    public void setMae(String mae) {
        this.mae = mae;
    }

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
