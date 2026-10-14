package service;

import dao.PortalVagasDAO;
import model.Area;
import model.Administrador;
import model.Candidatura;
import model.Empresa;
import model.Estudante;
import model.Modalidade;
import model.Perfil;
import model.SituacaoVaga;
import model.Usuario;
import model.Vaga;

import java.util.List;
import java.util.Objects;

public class PortalVagasService {
    private final PortalVagasDAO dao;

    public PortalVagasService(PortalVagasDAO dao) {
        this.dao = Objects.requireNonNull(dao, "DAO é obrigatório.");
    }

    public Usuario cadastrarUsuario(String nome, String email, Perfil perfil, String curso, String nomeEmpresa) {
        exigirTexto(nome, "Nome");
        exigirTexto(email, "E-mail");
        Objects.requireNonNull(perfil, "Perfil é obrigatório.");
        if (dao.buscarUsuarioPorEmail(email.trim()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
        }
        if (perfil == Perfil.ESTUDANTE) {
            exigirTexto(curso, "Curso");
        }
        if (perfil == Perfil.EMPRESA) {
            exigirTexto(nomeEmpresa, "Nome da empresa");
        }

        Usuario usuario = switch (perfil) {
            case ESTUDANTE -> new Estudante(nome.trim(), email.trim(), curso);
            case EMPRESA -> new Empresa(nome.trim(), email.trim(), nomeEmpresa);
            case ADMINISTRADOR -> new Administrador(nome.trim(), email.trim());
        };
        return dao.salvarUsuario(usuario);
    }

    public boolean validarPerfil(Usuario usuario, Perfil perfil) {
        return usuario != null && perfil != null && usuario.getPerfil() == perfil;
    }

    public boolean verificarPropriedadeVaga(Usuario empresa, Vaga vaga) {
        return empresa != null && vaga != null
                && empresa.getPerfil() == Perfil.EMPRESA
                && vaga.getEmpresa().getId() == empresa.getId();
    }

    public boolean verificarVagaAberta(Vaga vaga) {
        return vaga != null && vaga.getSituacao() == SituacaoVaga.ABERTA;
    }

    public Area cadastrarArea(Usuario administrador, String nome) {
        exigirPerfilCadastrado(administrador, Perfil.ADMINISTRADOR);
        exigirTexto(nome, "Nome da área");
        if (dao.listarAreas().stream().anyMatch(area -> area.getNome().equalsIgnoreCase(nome.trim()))) {
            throw new IllegalArgumentException("Já existe uma área com este nome.");
        }
        return dao.salvarArea(new Area(nome.trim()));
    }

    public Area editarArea(Usuario administrador, int areaId, String novoNome) {
        exigirPerfilCadastrado(administrador, Perfil.ADMINISTRADOR);
        exigirTexto(novoNome, "Nome da área");
        Area area = dao.buscarArea(areaId)
                .orElseThrow(() -> new IllegalArgumentException("Área não encontrada."));
        boolean nomeEmUso = dao.listarAreas().stream()
            .anyMatch(outra -> outra.getId() != areaId
                && outra.getNome().equalsIgnoreCase(novoNome.trim()));
        if (nomeEmUso) {
            throw new IllegalArgumentException("Já existe uma área com este nome.");
        }
        area.renomear(novoNome.trim());
        return area;
    }

    public void excluirArea(Usuario administrador, int areaId) {
        exigirPerfilCadastrado(administrador, Perfil.ADMINISTRADOR);
        dao.buscarArea(areaId).orElseThrow(() -> new IllegalArgumentException("Área não encontrada."));
        if (dao.areaEmUso(areaId)) {
            throw new IllegalStateException("Não é possível excluir uma área vinculada a uma vaga.");
        }
        dao.excluirArea(areaId);
    }

    public Vaga publicarVaga(Usuario empresa, String titulo, String descricao, String localizacao,
                             Modalidade modalidade, int cargaHorariaSemanal, int areaId) {
        exigirPerfilCadastrado(empresa, Perfil.EMPRESA);
        validarDadosVaga(titulo, descricao, localizacao, modalidade, cargaHorariaSemanal);
        Area area = dao.buscarArea(areaId)
                .orElseThrow(() -> new IllegalArgumentException("Área não encontrada."));
        Vaga vaga = new Vaga(titulo.trim(), descricao.trim(), localizacao.trim(), modalidade,
                cargaHorariaSemanal, area, empresa);
        vaga.publicar();
        return dao.salvarVaga(vaga);
    }

    public Vaga editarVaga(Usuario empresa, int vagaId, String titulo, String descricao, String localizacao,
                           Modalidade modalidade, int cargaHorariaSemanal, int areaId) {
        exigirPerfilCadastrado(empresa, Perfil.EMPRESA);
        validarDadosVaga(titulo, descricao, localizacao, modalidade, cargaHorariaSemanal);
        Vaga vaga = buscarVagaDaEmpresa(empresa, vagaId);
        Area area = dao.buscarArea(areaId)
                .orElseThrow(() -> new IllegalArgumentException("Área não encontrada."));
        vaga.editar(titulo.trim(), descricao.trim(), localizacao.trim(), modalidade,
                cargaHorariaSemanal, area);
        return vaga;
    }

    public void encerrarVaga(Usuario empresa, int vagaId) {
        exigirPerfilCadastrado(empresa, Perfil.EMPRESA);
        buscarVagaDaEmpresa(empresa, vagaId).encerrar();
    }

    public Candidatura candidatar(Usuario estudante, Vaga vaga) {
        exigirPerfilCadastrado(estudante, Perfil.ESTUDANTE);
        Objects.requireNonNull(vaga, "Vaga é obrigatória.");
        Vaga vagaCadastrada = dao.buscarVaga(vaga.getId())
                .filter(cadastrada -> cadastrada == vaga)
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada."));
        if (!verificarVagaAberta(vagaCadastrada)) {
            throw new IllegalStateException("Só é possível se candidatar a vagas abertas.");
        }
        Estudante estudanteCadastrado = (Estudante) estudante;
        if (verificarCandidaturaExistente(estudanteCadastrado, vagaCadastrada)) {
            throw new IllegalStateException("O estudante já se candidatou a esta vaga.");
        }
        return dao.salvarCandidatura(estudanteCadastrado.candidatar(vagaCadastrada));
    }

    public boolean verificarCandidaturaExistente(Usuario estudante, Vaga vaga) {
        if (estudante == null || vaga == null) {
            return false;
        }
        return dao.candidaturasDoEstudante(estudante.getId()).stream()
                .anyMatch(candidatura -> candidatura.getVaga().getId() == vaga.getId());
    }

    public List<Vaga> listarVagasAbertas() {
        return dao.listarVagas().stream()
                .filter(vaga -> vaga.getSituacao() == SituacaoVaga.ABERTA)
                .toList();
    }

    public List<Vaga> filtrarVagas(Area area, Modalidade modalidade) {
        return listarVagasAbertas().stream()
                .filter(vaga -> area == null || vaga.getArea().getId() == area.getId())
                .filter(vaga -> modalidade == null || vaga.getModalidade() == modalidade)
                .toList();
    }

    public List<Candidatura> consultarMinhasCandidaturas(Usuario estudante) {
        exigirPerfilCadastrado(estudante, Perfil.ESTUDANTE);
        return ((Estudante) estudante).consultarMinhasCandidaturas();
    }

    public List<Candidatura> consultarCandidatos(Usuario empresa, Vaga vaga) {
        exigirPerfilCadastrado(empresa, Perfil.EMPRESA);
        Objects.requireNonNull(vaga, "Vaga é obrigatória.");
        Vaga vagaDaEmpresa = buscarVagaDaEmpresa(empresa, vaga.getId());
        return dao.candidaturasDaVaga(vagaDaEmpresa.getId());
    }

    public Indicadores consultarIndicadores(Usuario administrador) {
        exigirPerfilCadastrado(administrador, Perfil.ADMINISTRADOR);
        long estudantes = dao.listarUsuarios().stream()
                .filter(usuario -> usuario.getPerfil() == Perfil.ESTUDANTE).count();
        long empresas = dao.listarUsuarios().stream()
                .filter(usuario -> usuario.getPerfil() == Perfil.EMPRESA).count();
        long abertas = dao.listarVagas().stream()
                .filter(vaga -> vaga.getSituacao() == SituacaoVaga.ABERTA).count();
        long encerradas = dao.listarVagas().stream()
                .filter(vaga -> vaga.getSituacao() == SituacaoVaga.ENCERRADA).count();
        return new Indicadores(estudantes, empresas, abertas, encerradas, dao.listarCandidaturas().size());
    }

    private Vaga buscarVagaDaEmpresa(Usuario empresa, int vagaId) {
        return dao.buscarVaga(vagaId)
                .filter(vaga -> verificarPropriedadeVaga(empresa, vaga))
                .orElseThrow(() -> new SecurityException("A vaga não existe ou não pertence a esta empresa."));
    }

    private void exigirPerfilCadastrado(Usuario usuario, Perfil perfil) {
        if (!validarPerfil(usuario, perfil)) {
            throw new SecurityException("Operação não permitida para este perfil.");
        }
        dao.buscarUsuario(usuario.getId())
                .filter(cadastrado -> cadastrado == usuario)
                .orElseThrow(() -> new SecurityException("Usuário não cadastrado neste portal."));
    }

    private void validarDadosVaga(String titulo, String descricao, String localizacao,
                                  Modalidade modalidade, int cargaHorariaSemanal) {
        exigirTexto(titulo, "Título");
        exigirTexto(descricao, "Descrição");
        exigirTexto(localizacao, "Localização");
        Objects.requireNonNull(modalidade, "Modalidade é obrigatória.");
        if (cargaHorariaSemanal <= 0) {
            throw new IllegalArgumentException("A carga horária semanal deve ser maior que zero.");
        }
    }

    private void exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
    }

}