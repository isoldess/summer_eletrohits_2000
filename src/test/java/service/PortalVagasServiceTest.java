package service;

import dao.PortalVagasDAO;
import model.Administrador;
import model.Candidatura;
import model.Empresa;
import model.Estudante;
import model.Modalidade;
import model.Perfil;
import model.Usuario;
import model.Vaga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortalVagasServiceTest {
    private PortalVagasService portal;
    private Usuario administrador;
    private Usuario empresa;
    private Usuario estudante;
    private Vaga vaga;

    @BeforeEach
    void configurarPortal() {
        portal = new PortalVagasService(new PortalVagasDAO());
        administrador = portal.cadastrarUsuario(
                "Admin", "admin@portal.com", Perfil.ADMINISTRADOR, null, null);
        empresa = portal.cadastrarUsuario(
                "Equipe", "empresa@portal.com", Perfil.EMPRESA, null, "Empresa Exemplo");
        estudante = portal.cadastrarUsuario(
                "Ana", "ana@portal.com", Perfil.ESTUDANTE, "Computação", null);
        var area = portal.cadastrarArea(administrador, "Tecnologia");
        vaga = portal.publicarVaga(empresa, "Estágio Java", "Descrição", "Remoto",
                Modalidade.REMOTA, 20, area.getId());
    }

    @Test
    void impedeMaisDeUmaCandidaturaDoEstudanteNaMesmaVaga() {
        portal.candidatar(estudante, vaga);

        assertThrows(IllegalStateException.class, () -> portal.candidatar(estudante, vaga));
        assertEquals(1, portal.consultarMinhasCandidaturas(estudante).size());
    }

    @Test
    void representaPerfisComoEspecializacoesECompoeCandidaturasNaVaga() {
        assertInstanceOf(Administrador.class, administrador);
        assertInstanceOf(Empresa.class, empresa);
        assertInstanceOf(Estudante.class, estudante);

        Candidatura candidatura = portal.candidatar(estudante, vaga);

        assertEquals(1, vaga.getCandidaturas().size());
        assertSame(candidatura, vaga.getCandidaturas().get(0));
    }

    @Test
    void vagaEncerradaNaoAceitaCandidaturasEContinuaComHistorico() {
        portal.candidatar(estudante, vaga);
        portal.encerrarVaga(empresa, vaga.getId());

        assertThrows(IllegalStateException.class, () -> portal.candidatar(
                portal.cadastrarUsuario("Bia", "bia@portal.com", Perfil.ESTUDANTE, "Design", null), vaga));
        assertEquals(1, portal.consultarCandidatos(empresa, vaga).size());
        assertEquals(0, portal.listarVagasAbertas().size());
    }

    @Test
    void somenteAdministradorPodeGerenciarAreasEVagaVinculadaImpedeExclusao() {
        assertThrows(SecurityException.class, () -> portal.cadastrarArea(empresa, "Design"));
        assertThrows(IllegalStateException.class, () -> portal.excluirArea(administrador, vaga.getArea().getId()));
    }

    @Test
    void exigeCursoParaEstudanteENomeDaEmpresaParaEmpresa() {
        assertThrows(IllegalArgumentException.class, () -> portal.cadastrarUsuario(
                "Sem curso", "semcurso@portal.com", Perfil.ESTUDANTE, null, null));
        assertThrows(IllegalArgumentException.class, () -> portal.cadastrarUsuario(
                "Sem empresa", "semempresa@portal.com", Perfil.EMPRESA, null, " "));
    }

    @Test
    void eMailEhUnicoSemDiferenciarMaiusculas() {
        assertThrows(IllegalArgumentException.class, () -> portal.cadastrarUsuario(
                "Outra estudante", "ANA@PORTAL.COM", Perfil.ESTUDANTE, "Computação", null));
    }

    @Test
    void nomeDaAreaEhUnicoEDataDaCandidaturaEhObrigatoria() {
        assertThrows(IllegalArgumentException.class, () -> portal.cadastrarArea(administrador, "tecnologia"));
        assertThrows(NullPointerException.class, () -> new Candidatura(
                1, null, estudante, vaga));
        assertEquals(LocalDate.now(), portal.candidatar(estudante, vaga).getDataInscricao());
    }

    @Test
    void apenasEmpresaResponsavelPodeEditarOuEncerrarVaga() {
        Usuario outraEmpresa = portal.cadastrarUsuario(
                "Outra equipe", "outra@portal.com", Perfil.EMPRESA, null, "Outra Empresa");

        assertTrue(portal.verificarPropriedadeVaga(empresa, vaga));
        assertFalse(portal.verificarPropriedadeVaga(outraEmpresa, vaga));
        assertThrows(SecurityException.class, () -> portal.editarVaga(
                outraEmpresa, vaga.getId(), "Novo título", "Descrição", "Local",
                Modalidade.PRESENCIAL, 20, vaga.getArea().getId()));
        assertThrows(SecurityException.class, () -> portal.encerrarVaga(outraEmpresa, vaga.getId()));
    }

    @Test
    void rejeitaCargaHorariaInvalidaEPerfilNaoEstudanteAoSeCandidatar() {
        assertThrows(IllegalArgumentException.class, () -> portal.publicarVaga(
                empresa, "Estágio", "Descrição", "Local", Modalidade.REMOTA,
                0, vaga.getArea().getId()));
        assertThrows(SecurityException.class, () -> portal.candidatar(empresa, vaga));
        assertTrue(portal.verificarVagaAberta(vaga));
    }

    @Test
    void filtraVagasAbertasECalculaIndicadoresAdministrativos() {
        portal.publicarVaga(empresa, "Estágio presencial", "Descrição", "Local",
                Modalidade.PRESENCIAL, 20, vaga.getArea().getId());
        portal.candidatar(estudante, vaga);

        assertEquals(1, portal.filtrarVagas(vaga.getArea(), Modalidade.REMOTA).size());
        assertEquals(1, portal.filtrarVagas(null, Modalidade.PRESENCIAL).size());
        assertEquals(2, portal.filtrarVagas(null, null).size());

        var indicadores = portal.consultarIndicadores(administrador);
        assertEquals(1, indicadores.totalEstudantes());
        assertEquals(1, indicadores.totalEmpresas());
        assertEquals(2, indicadores.vagasAbertas());
        assertEquals(0, indicadores.vagasEncerradas());
        assertEquals(1, indicadores.totalCandidaturas());
    }
}