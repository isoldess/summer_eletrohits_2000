package service;

import dao.PortalVagasDAO;
import model.Area;
import model.Modalidade;
import model.Vaga;

import java.util.List;
import java.util.Objects;

public class PortalVagas {
    private final PortalVagasService service;

    public PortalVagas(PortalVagasDAO dao) {
        this(new PortalVagasService(dao));
    }

    public PortalVagas(PortalVagasService service) {
        this.service = Objects.requireNonNull(service, "Serviço do portal é obrigatório.");
    }

    public List<Vaga> listarVagasAbertas() {
        return service.listarVagasAbertas();
    }

    public List<Vaga> filtrarVagas(Area area, Modalidade modalidade) {
        return service.filtrarVagas(area, modalidade);
    }
}
