package model;

import java.time.LocalDate;
import java.util.Objects;

public class Candidatura {
    private int id;
    private final LocalDate dataInscricao;
    private final Estudante estudante;
    private final Vaga vaga;

    public Candidatura(Usuario estudante, Vaga vaga) {
        this(0, LocalDate.now(), estudante, vaga);
    }

    public Candidatura(int id, LocalDate dataInscricao, Usuario estudante, Vaga vaga) {
        this.id = id;
        this.dataInscricao = Objects.requireNonNull(dataInscricao, "Data da inscrição é obrigatória.");
        Objects.requireNonNull(estudante, "Estudante é obrigatório.");
        if (!(estudante instanceof Estudante estudanteValido)) {
            throw new IllegalArgumentException("A candidatura deve pertencer a um estudante.");
        }
        this.estudante = estudanteValido;
        this.vaga = Objects.requireNonNull(vaga, "Vaga é obrigatória.");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id != 0 && this.id != id) {
            throw new IllegalStateException("O identificador da candidatura já foi definido.");
        }
        this.id = id;
    }

    public LocalDate getDataInscricao() {
        return dataInscricao;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public boolean validarDuplicidade() {
        return estudante.consultarMinhasCandidaturas().stream()
                .noneMatch(existente -> existente != this && mesmaVaga(existente.getVaga(), vaga))
                && vaga.getCandidaturas().stream()
                .noneMatch(existente -> existente != this
                        && mesmoEstudante(existente.getEstudante(), estudante));
    }

    public void registrar() {
        synchronized (estudante) {
            if (vaga.getSituacao() != SituacaoVaga.ABERTA) {
                throw new IllegalStateException("Só é possível se candidatar a vagas abertas.");
            }
            boolean jaRegistrada = estudante.consultarMinhasCandidaturas().contains(this)
                    && vaga.getCandidaturas().contains(this);
            if (jaRegistrada) {
                return;
            }
            if (!validarDuplicidade()) {
                throw new IllegalStateException("O estudante já se candidatou a esta vaga.");
            }

            estudante.adicionarCandidatura(this);
            vaga.adicionarCandidatura(this);
        }
    }

    static boolean mesmaVaga(Vaga primeira, Vaga segunda) {
        return primeira == segunda || primeira.getId() != 0 && primeira.getId() == segunda.getId();
    }

    static boolean mesmoEstudante(Estudante primeiro, Estudante segundo) {
        return primeiro == segundo || primeiro.getId() != 0 && primeiro.getId() == segundo.getId();
    }
}