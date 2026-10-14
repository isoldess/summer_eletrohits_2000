package dao;

import model.Area;
import model.Candidatura;
import model.Usuario;
import model.Vaga;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PortalVagasDAO {
    private final Map<Integer, Usuario> usuarios = new LinkedHashMap<>();
    private final Map<Integer, Area> areas = new LinkedHashMap<>();
    private final Map<Integer, Vaga> vagas = new LinkedHashMap<>();
    private final Map<Integer, Candidatura> candidaturas = new LinkedHashMap<>();
    private int proximoUsuarioId = 1;
    private int proximaAreaId = 1;
    private int proximaVagaId = 1;
    private int proximaCandidaturaId = 1;

    public synchronized Usuario salvarUsuario(Usuario usuario) {
        boolean emailEmUso = usuarios.values().stream()
                .anyMatch(cadastrado -> cadastrado.getId() != usuario.getId()
                        && cadastrado.getEmail().equalsIgnoreCase(usuario.getEmail()));
        if (emailEmUso) {
            throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
        }
        if (usuario.getId() == 0) {
            usuario.setId(proximoUsuarioId++);
        }
        usuarios.put(usuario.getId(), usuario);
        return usuario;
    }

    public synchronized Area salvarArea(Area area) {
        boolean nomeEmUso = areas.values().stream()
                .anyMatch(cadastrada -> cadastrada.getId() != area.getId()
                        && cadastrada.getNome().equalsIgnoreCase(area.getNome()));
        if (nomeEmUso) {
            throw new IllegalArgumentException("Já existe uma área com este nome.");
        }
        if (area.getId() == 0) {
            area.setId(proximaAreaId++);
        }
        areas.put(area.getId(), area);
        return area;
    }

    public synchronized Vaga salvarVaga(Vaga vaga) {
        if (vaga.getId() == 0) {
            vaga.setId(proximaVagaId++);
        }
        vagas.put(vaga.getId(), vaga);
        return vaga;
    }

    public synchronized Candidatura salvarCandidatura(Candidatura candidatura) {
        boolean duplicada = candidaturas.values().stream()
                .anyMatch(cadastrada -> cadastrada.getEstudante().getId()
                        == candidatura.getEstudante().getId()
                        && cadastrada.getVaga().getId() == candidatura.getVaga().getId());
        if (duplicada) {
            throw new IllegalStateException("O estudante já se candidatou a esta vaga.");
        }
        if (candidatura.getId() == 0) {
            candidatura.setId(proximaCandidaturaId++);
        }
        candidatura.registrar();
        candidaturas.put(candidatura.getId(), candidatura);
        return candidatura;
    }

    public synchronized Optional<Usuario> buscarUsuario(int id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    public synchronized Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return usuarios.values().stream()
                .filter(usuario -> usuario.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public synchronized Optional<Area> buscarArea(int id) {
        return Optional.ofNullable(areas.get(id));
    }

    public synchronized Optional<Vaga> buscarVaga(int id) {
        return Optional.ofNullable(vagas.get(id));
    }

    public synchronized List<Usuario> listarUsuarios() {
        return List.copyOf(usuarios.values());
    }

    public synchronized List<Area> listarAreas() {
        return List.copyOf(areas.values());
    }

    public synchronized List<Vaga> listarVagas() {
        return List.copyOf(vagas.values());
    }

    public synchronized List<Candidatura> listarCandidaturas() {
        return List.copyOf(candidaturas.values());
    }

    public synchronized List<Candidatura> candidaturasDoEstudante(int estudanteId) {
        return candidaturas.values().stream()
                .filter(candidatura -> candidatura.getEstudante().getId() == estudanteId)
                .toList();
    }

    public synchronized List<Candidatura> candidaturasDaVaga(int vagaId) {
        return candidaturas.values().stream()
                .filter(candidatura -> candidatura.getVaga().getId() == vagaId)
                .toList();
    }

    public synchronized void excluirArea(int id) {
        areas.remove(id);
    }

    public synchronized boolean areaEmUso(int areaId) {
        return vagas.values().stream().anyMatch(vaga -> vaga.getArea().getId() == areaId);
    }
}