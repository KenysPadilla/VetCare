package model;

import java.time.LocalDateTime;

public class Consulta {

    private int id;
    private Cita cita;
    private Paciente paciente;
    private Veterinario veterinario;
    private LocalDateTime fechaHora;
    private String sintomas;
    private String diagnostico;
    private String tratamiento;
    private double costo;

    public Consulta() {
    }

    public Consulta(int id, Cita cita, Paciente paciente, Veterinario veterinario,
            LocalDateTime fechaHora, String sintomas, String diagnostico,
            String tratamiento, double costo) {
        this.id = id;
        this.cita = cita;
        this.paciente = paciente;
        this.veterinario = veterinario;
        this.fechaHora = fechaHora;
        this.sintomas = sintomas;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.costo = costo;
    }

    @Override
    public String toString() {
        return "Consulta #" + id + " - "
                + (paciente != null ? paciente.getNombre() : "?")
                + " (" + (fechaHora != null ? fechaHora.toLocalDate() : "?") + ")";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }
}
