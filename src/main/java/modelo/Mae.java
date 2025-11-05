package modelo;

import java.time.LocalDate;

public class Mae {
    private int idMae;
    private String nome;
    private String telefone;
    private String endereco;
    private LocalDate dataAniversario;

    public Mae() {}

    public Mae(int idMae, String nome, String telefone, String endereco, LocalDate dataAniversario) {
        this.idMae = idMae;
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.dataAniversario = dataAniversario;
    }

    public int getIdMae() {
        return idMae;
    }

    public void setIdMae(int idMae) {
        this.idMae = idMae;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public LocalDate getDataAniversario() {
        return dataAniversario;
    }

    public void setDataAniversario(LocalDate dataAniversario) {
        this.dataAniversario = dataAniversario;
    }

    @Override
    public String toString() {
        return nome;
    }
}
