package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Estudante extends Usuario {
    private final String curso;
    private final List<Candidatura> candidaturas = new ArrayList<>();

    public Estudante(String nome, String email, String curso) {
        this(0, nome, email, curso);
    }

    public Estudante(int id, String nome, String email, String curso) {
        super(id, nome, email);
        this.curso = validarCurso(curso);
    }

    @Override
    public Perfil getPerfil() {
        return Perfil.ESTUDANTE;
    }

    public String getCurso() {
        return curso;
    }

    public synchronized Candidatura candidatar(Vaga vaga) {
        Objects.requireNonNull(vaga, "Vaga é obrigatória.");
        if (vaga.getSituacao() != SituacaoVaga.ABERTA) {
            throw new IllegalStateException("Só é possível se candidatar a vagas abertas.");
        }

        Candidatura candidatura = new Candidatura(this, vaga);
        candidatura.registrar();
        return candidatura;
    }

    public synchronized List<Candidatura> consultarMinhasCandidaturas() {
        return List.copyOf(candidaturas);
    }

    synchronized void adicionarCandidatura(Candidatura candidatura) {
        if (candidatura.getEstudante() != this) {
            throw new IllegalArgumentException("A candidatura pertence a outro estudante.");
        }
        if (candidaturas.stream().anyMatch(existente ->
                Candidatura.mesmaVaga(existente.getVaga(), candidatura.getVaga()))) {
            throw new IllegalStateException("O estudante já se candidatou a esta vaga.");
        }
        candidaturas.add(candidatura);
    }

    private String validarCurso(String curso) {
        Objects.requireNonNull(curso, "Curso é obrigatório para estudantes.");
        if (curso.isBlank()) {
            throw new IllegalArgumentException("Curso é obrigatório para estudantes.");
        }
        return curso.trim();
    }
}
