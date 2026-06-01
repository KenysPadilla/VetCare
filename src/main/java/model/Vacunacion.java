package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Vacunacion {

    private int id;
    private Paciente paciente;
    private Veterinario veterinario;
    private Vacuna vacuna;
    private LocalDateTime fechaHoraAplicacion;
    private LocalDate fechaProxima;
    private String observaciones;

    public Vacunacion() {
    }

    public Vacunacion(int id, Paciente paciente, Veterinario veterinario, Vacuna vacuna,
            LocalDateTime fechaHoraAplicacion, LocalDate fechaProxima, String observaciones) {
        this.id = id;
        this.paciente = paciente;
        this.veterinario = veterinario;
        this.vacuna = vacuna;
        this.fechaHoraAplicacion = fechaHoraAplicacion;
        this.fechaProxima = fechaProxima;
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return (vacuna != null ? vacuna.getNombre() : "?")
                + " - "
                + (paciente != null ? paciente.getNombre() : "?");
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

    public Vacuna getVacuna() {
        return vacuna;
    }

    public void setVacuna(Vacuna vacuna) {
        this.vacuna = vacuna;
    }

    public LocalDateTime getFechaHoraAplicacion() {
        return fechaHoraAplicacion;
    }

    public void setFechaHoraAplicacion(LocalDateTime fechaHoraAplicacion) {
        this.fechaHoraAplicacion = fechaHoraAplicacion;
    }

    public LocalDate getFechaProxima() {
        return fechaProxima;
    }

    public void setFechaProxima(LocalDate fechaProxima) {
        this.fechaProxima = fechaProxima;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
