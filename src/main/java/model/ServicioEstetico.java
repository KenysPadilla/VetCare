package model;

import java.time.LocalDateTime;

public class ServicioEstetico {

    private int id;
    private String tipoServicio;
    private Paciente paciente;
    private Estilista estilista;
    private LocalDateTime fechaHora;
    private double precio;
    private String estadoServicio;
    private String observaciones;
    private String tipoBano;
    private boolean incluyeSecado;
    private boolean incluyePerfume;
    private String estiloCorte;
    private String largoCorte;
    private boolean incluyeUnas;
    private boolean incluyeLimpieza;

    public ServicioEstetico() {
    }

    public void cancelar() {
        this.estadoServicio = "CANCELADO";
    }

    public void completar() {
        this.estadoServicio = "REALIZADO";
    }

    public boolean esBano() {
        return "BANO".equals(tipoServicio);
    }

    public boolean esMotilada() {
        return "MOTILADA".equals(tipoServicio);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Estilista getEstilista() {
        return estilista;
    }

    public void setEstilista(Estilista estilista) {
        this.estilista = estilista;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getEstadoServicio() {
        return estadoServicio;
    }

    public void setEstadoServicio(String estadoServicio) {
        this.estadoServicio = estadoServicio;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getTipoBano() {
        return tipoBano;
    }

    public void setTipoBano(String tipoBano) {
        this.tipoBano = tipoBano;
    }

    public boolean isIncluyeSecado() {
        return incluyeSecado;
    }

    public void setIncluyeSecado(boolean incluyeSecado) {
        this.incluyeSecado = incluyeSecado;
    }

    public boolean isIncluyePerfume() {
        return incluyePerfume;
    }

    public void setIncluyePerfume(boolean incluyePerfume) {
        this.incluyePerfume = incluyePerfume;
    }

    public String getEstiloCorte() {
        return estiloCorte;
    }

    public void setEstiloCorte(String estiloCorte) {
        this.estiloCorte = estiloCorte;
    }

    public String getLargoCorte() {
        return largoCorte;
    }

    public void setLargoCorte(String largoCorte) {
        this.largoCorte = largoCorte;
    }

    public boolean isIncluyeUnas() {
        return incluyeUnas;
    }

    public void setIncluyeUnas(boolean incluyeUnas) {
        this.incluyeUnas = incluyeUnas;
    }

    public boolean isIncluyeLimpieza() {
        return incluyeLimpieza;
    }

    public void setIncluyeLimpieza(boolean incluyeLimpieza) {
        this.incluyeLimpieza = incluyeLimpieza;
    }

    @Override
    public String toString() {
        String nomPac = (paciente != null) ? paciente.getNombre() : "?";
        return tipoServicio + " - " + nomPac
                + " (" + (fechaHora != null ? fechaHora.toLocalDate() : "") + ")";
    }
}
