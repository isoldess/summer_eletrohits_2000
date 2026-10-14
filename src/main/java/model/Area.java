package model;

import java.util.Objects;

public class Area {
    private int id;
    private String nome;

    public Area(String nome) {
        this(0, nome);
    }

    public Area(int id, String nome) {
        this.id = id;
        this.nome = validarNome(nome);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id != 0 && this.id != id) {
            throw new IllegalStateException("O identificador da área já foi definido.");
        }
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void renomear(String nome) {
        this.nome = validarNome(nome);
    }

    private String validarNome(String nome) {
        Objects.requireNonNull(nome, "Nome da área é obrigatório.");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Nome da área é obrigatório.");
        }
        return nome.trim();
    }
}