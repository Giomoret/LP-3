package modelo;

import java.time.LocalDate;
import java.util.List;

public class Encontro {
    private int idEncontro;
    private LocalDate dataEncontro;
    private List<Servico> servicos;
    private boolean cancelado;

    public Encontro() {}

    public Encontro(int idEncontro, LocalDate dataEncontro, List<Servico> servicos, boolean cancelado) {
        this.idEncontro = idEncontro;
        this.dataEncontro = dataEncontro;
        this.servicos = servicos;
        this.cancelado = cancelado;
    }

    public int getIdEncontro() {
        return idEncontro;
    }

    public void setIdEncontro(int idEncontro) {
        this.idEncontro = idEncontro;
    }

    public LocalDate getDataEncontro() {
        return dataEncontro;
    }

    public void setDataEncontro(LocalDate dataEncontro) {
        this.dataEncontro = dataEncontro;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    @Override
    public String toString() {
        return "Encontro em " + dataEncontro + (cancelado ? " (Cancelado)" : "");
    }
}
