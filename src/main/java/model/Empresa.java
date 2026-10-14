package model;

import java.util.Objects;

public class Empresa extends Usuario {
    private final String nomeEmpresa;

    public Empresa(String nome, String email, String nomeEmpresa) {
        this(0, nome, email, nomeEmpresa);
    }

    public Empresa(int id, String nome, String email, String nomeEmpresa) {
        super(id, nome, email);
        this.nomeEmpresa = validarNomeEmpresa(nomeEmpresa);
    }

    @Override
    public Perfil getPerfil() {
        return Perfil.EMPRESA;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    private String validarNomeEmpresa(String nomeEmpresa) {
        Objects.requireNonNull(nomeEmpresa, "Nome da empresa é obrigatório.");
        if (nomeEmpresa.isBlank()) {
            throw new IllegalArgumentException("Nome da empresa é obrigatório.");
        }
        return nomeEmpresa.trim();
    }
}
