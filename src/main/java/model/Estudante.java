package model;

import java.util.Objects;

public class Estudante extends Usuario {
    private final String curso;

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

    private String validarCurso(String curso) {
        Objects.requireNonNull(curso, "Curso é obrigatório para estudantes.");
        if (curso.isBlank()) {
            throw new IllegalArgumentException("Curso é obrigatório para estudantes.");
        }
        return curso.trim();
    }
}
