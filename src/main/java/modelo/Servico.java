package modelo;

public class Servico {

    // -----------------------------
    // Atributos
    // -----------------------------
    private int idServico;     // PK da tabela 'servico'
    private String tipo;       // Tipo do serviço (ex: Música, Terço, Formação)
    private int idMae;         // FK para tabela 'mae'
    private String nomeMae;    // Nome da mãe (opcional - via JOIN)
    private String descricao;  // Descrição opcional
    private int idEncontro;    // FK para tabela 'encontro'

    // -----------------------------
    // Construtores
    // -----------------------------
    public Servico() {
        // Construtor padrão necessário para frameworks e DAO
    }

    public Servico(int idServico, String tipo, int idMae, String descricao, int idEncontro) {
        this.idServico = idServico;
        this.tipo = tipo;
        this.idMae = idMae;
        this.descricao = descricao;
        this.idEncontro = idEncontro;
    }

    // -----------------------------
    // Getters e Setters
    // -----------------------------
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

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
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

    // -----------------------------
    // Métodos utilitários (opcionais)
    // -----------------------------
    @Override
    public String toString() {
        // Facilita o debug e a exibição em JComboBox
        return tipo + (nomeMae != null ? " - " + nomeMae : "");
    }
}
