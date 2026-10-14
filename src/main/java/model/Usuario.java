package model;

import java.util.Objects;

public abstract class Usuario {
    private int id;
    private final String nome;
    private final String email;

    protected Usuario(String nome, String email) {
        this(0, nome, email);
    }

    protected Usuario(int id, String nome, String email) {
        this.id = id;
        this.nome = validarTexto(nome, "Nome");
        this.email = validarTexto(email, "E-mail");
    }

    public abstract Perfil getPerfil();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id != 0 && this.id != id) {
            throw new IllegalStateException("O identificador do usuário já foi definido.");
        }
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    private String validarTexto(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " é obrigatório.");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        return valor.trim();
    }
}