package app;

import dao.PortalVagasDAO;
import model.Modalidade;
import model.Perfil;
import model.Usuario;
import model.Vaga;
import service.PortalVagas;
import service.PortalVagasService;

public class Main {
    public static void main(String[] args) {
    PortalVagasDAO dao = new PortalVagasDAO();
    PortalVagasService portal = new PortalVagasService(dao);
    PortalVagas consultaPublica = new PortalVagas(portal);

    Usuario administrador = portal.cadastrarUsuario(
        "Admin", "admin@portal.com", Perfil.ADMINISTRADOR, null, null);
    Usuario empresa = portal.cadastrarUsuario(
        "Equipe Tech", "contato@tech.com", Perfil.EMPRESA, null, "Tech Brasil");
    Usuario estudante = portal.cadastrarUsuario(
        "Ana Silva", "ana@universidade.com", Perfil.ESTUDANTE, "Sistemas de Informação", null);

    var tecnologia = portal.cadastrarArea(administrador, "Tecnologia");
    Vaga vaga = portal.publicarVaga(empresa, "Estágio em desenvolvimento",
        "Desenvolvimento de aplicações Java.", "São Paulo", Modalidade.HIBRIDA,
        30, tecnologia.getId());
    portal.candidatar(estudante, vaga);

    System.out.printf("Portal iniciado: %d vaga aberta, %d candidatura(s).%n",
        consultaPublica.listarVagasAbertas().size(),
        portal.consultarCandidatos(empresa, vaga).size());
    }
}