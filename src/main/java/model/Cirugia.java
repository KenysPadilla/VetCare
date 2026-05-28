package model;

import java.time.LocalDateTime;

public class Cirugia {

    private int id;
    private Paciente paciente;
    private Veterinario veterinario;
    private LocalDateTime fechaHora;
    private String tipoCirugia;
    private String anestesia;
    private String descripcion;
    private String resultado;
    private double costo;

    public Cirugia() {
    }

    public Cirugia(int id, Paciente paciente, Veterinario veterinario, LocalDateTime fechaHora,
            String tipoCirugia, String anestesia, String descripcion,
            String resultado, double costo) {
        this.id = id;
        this.paciente = paciente;
        this.veterinario = veterinario;
        this.fechaHora = fechaHora;
        this.tipoCirugia = tipoCirugia;
        this.anestesia = anestesia;
        this.descripcion = descripcion;
        this.resultado = resultado;
        this.costo = costo;
    }

    @Override
    public String toString() {
        return (tipoCirugia != null ? tipoCirugia : "?")
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTipoCirugia() {
        return tipoCirugia;
    }

    public void setTipoCirugia(String tipoCirugia) {
        this.tipoCirugia = tipoCirugia;
    }

    public String getAnestesia() {
        return anestesia;
    }

    public void setAnestesia(String anestesia) {
        this.anestesia = anestesia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }
}
