package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vaga {
    private int id;
    private String titulo;
    private String descricao;
    private String localizacao;
    private Modalidade modalidade;
    private int cargaHorariaSemanal;
    private SituacaoVaga situacao;
    private Area area;
    private final Empresa empresa;
    private final List<Candidatura> candidaturas = new ArrayList<>();

    public Vaga(String titulo, String descricao, String localizacao, Modalidade modalidade,
                int cargaHorariaSemanal, Area area, Usuario empresa) {
        this(0, titulo, descricao, localizacao, modalidade, cargaHorariaSemanal,
                SituacaoVaga.ABERTA, area, empresa);
    }

    public Vaga(int id, String titulo, String descricao, String localizacao, Modalidade modalidade,
                int cargaHorariaSemanal, SituacaoVaga situacao, Area area, Usuario empresa) {
        this.id = id;
        validarDados(titulo, descricao, localizacao, modalidade, cargaHorariaSemanal, area, empresa);
        this.titulo = titulo.trim();
        this.descricao = descricao.trim();
        this.localizacao = localizacao.trim();
        this.modalidade = modalidade;
        this.cargaHorariaSemanal = cargaHorariaSemanal;
        this.situacao = Objects.requireNonNull(situacao, "Situação da vaga é obrigatória.");
        this.area = area;
        this.empresa = (Empresa) empresa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id != 0 && this.id != id) {
            throw new IllegalStateException("O identificador da vaga já foi definido.");
        }
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public Modalidade getModalidade() {
        return modalidade;
    }

    public int getCargaHorariaSemanal() {
        return cargaHorariaSemanal;
    }

    public SituacaoVaga getSituacao() {
        return situacao;
    }

    public Area getArea() {
        return area;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public synchronized List<Candidatura> getCandidaturas() {
        return List.copyOf(candidaturas);
    }

    synchronized void adicionarCandidatura(Candidatura candidatura) {
        Objects.requireNonNull(candidatura, "Candidatura é obrigatória.");
        if (candidatura.getVaga() != this) {
            throw new IllegalArgumentException("A candidatura pertence a outra vaga.");
        }
        if (candidaturas.contains(candidatura)) {
            return;
        }
        boolean duplicada = candidaturas.stream()
                .anyMatch(existente -> Candidatura.mesmoEstudante(
                        existente.getEstudante(), candidatura.getEstudante()));
        if (duplicada) {
            throw new IllegalStateException("O estudante já se candidatou a esta vaga.");
        }
        candidaturas.add(candidatura);
    }

    public void editar(String titulo, String descricao, String localizacao, Modalidade modalidade,
                       int cargaHorariaSemanal, Area area) {
        validarDados(titulo, descricao, localizacao, modalidade, cargaHorariaSemanal, area, empresa);
        this.titulo = titulo.trim();
        this.descricao = descricao.trim();
        this.localizacao = localizacao.trim();
        this.modalidade = modalidade;
        this.cargaHorariaSemanal = cargaHorariaSemanal;
        this.area = area;
    }

    public void publicar() {
        situacao = SituacaoVaga.ABERTA;
    }

    public void encerrar() {
        situacao = SituacaoVaga.ENCERRADA;
    }

    private void validarDados(String titulo, String descricao, String localizacao, Modalidade modalidade,
                              int cargaHorariaSemanal, Area area, Usuario empresa) {
        validarTexto(titulo, "Título");
        validarTexto(descricao, "Descrição");
        validarTexto(localizacao, "Localização");
        Objects.requireNonNull(modalidade, "Modalidade é obrigatória.");
        Objects.requireNonNull(area, "Área é obrigatória.");
        Objects.requireNonNull(empresa, "Empresa responsável é obrigatória.");
        if (!(empresa instanceof Empresa)) {
            throw new IllegalArgumentException("A responsável pela vaga deve ser uma empresa.");
        }
        if (cargaHorariaSemanal <= 0) {
            throw new IllegalArgumentException("A carga horária semanal deve ser maior que zero.");
        }
    }

    private void validarTexto(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " é obrigatório.");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
    }
}