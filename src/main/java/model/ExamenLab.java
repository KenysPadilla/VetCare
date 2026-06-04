package model;

import java.time.LocalDateTime;

public class ExamenLab {

    private int id;
    private Paciente paciente;
    private Veterinario veterinario;
    private Consulta consulta;
    private LocalDateTime fechaHora;
    private String tipoExamen;
    private String prioridad = "NORMAL";
    private String resultado;
    private String observaciones;
    private double costo;

    public ExamenLab() {
    }

    public ExamenLab(int id, Paciente paciente, Veterinario veterinario,
            Consulta consulta, LocalDateTime fechaHora, String tipoExamen,
            String resultado, String observaciones, double costo) {
        this.id = id;
        this.paciente = paciente;
        this.veterinario = veterinario;
        this.consulta = consulta;
        this.fechaHora = fechaHora;
        this.tipoExamen = tipoExamen;
        this.resultado = resultado;
        this.observaciones = observaciones;
        this.costo = costo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTipoExamen() {
        return tipoExamen;
    }

    public void setTipoExamen(String tipoExamen) {
        this.tipoExamen = tipoExamen;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad != null ? prioridad : "NORMAL";
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    @Override
    public String toString() {
        String nomPac = (paciente != null) ? paciente.getNombre() : "?";
        return tipoExamen + " - " + nomPac
                + " (" + (fechaHora != null ? fechaHora.toLocalDate() : "") + ")";
    }
}
