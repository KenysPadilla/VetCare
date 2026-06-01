package model;

import java.time.LocalDateTime;

public class Cita {

    private int id;
    private Paciente paciente;
    private Veterinario veterinario;
    private LocalDateTime fechaHora;
    private String tipoCita;
    private String estadoCita;
    private String motivo;
    private String observaciones;

    public Cita() {
    }

    public Cita(int id, Paciente paciente, Veterinario veterinario,
            LocalDateTime fechaHora, String tipoCita, String estadoCita,
            String motivo, String observaciones) {
        this.id = id;
        this.paciente = paciente;
        this.veterinario = veterinario;
        this.fechaHora = fechaHora;
        this.tipoCita = tipoCita;
        this.estadoCita = estadoCita;
        this.motivo = motivo;
        this.observaciones = observaciones;
    }

    public void cancelar() {
        this.estadoCita = "CANCELADA";
    }

    public void completar() {
        this.estadoCita = "REALIZADA";
    }

    public boolean estaVigente() {
        return "PROGRAMADA".equals(estadoCita) && fechaHora != null
                && fechaHora.isAfter(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Cita #" + id + " - "
                + (paciente != null ? paciente.getNombre() : "?")
                + " (" + (fechaHora != null ? fechaHora.toLocalDate() + " "
                        + fechaHora.toLocalTime() : "?") + ")";
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTipoCita() {
        return tipoCita;
    }

    public void setTipoCita(String tipoCita) {
        this.tipoCita = tipoCita;
    }

    public String getEstadoCita() {
        return estadoCita;
    }

    public void setEstadoCita(String estadoCita) {
        this.estadoCita = estadoCita;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
