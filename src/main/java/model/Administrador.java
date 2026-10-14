package model;

public class Administrador extends Usuario {
    public Administrador(String nome, String email) {
        this(0, nome, email);
    }

    public Administrador(int id, String nome, String email) {
        super(id, nome, email);
    }

    @Override
    public Perfil getPerfil() {
        return Perfil.ADMINISTRADOR;
    }
}
