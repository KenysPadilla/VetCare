package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Factura {

    public static final double TASA_IVA = 0.19;
    private int id;
    private Propietario propietario;
    private Paciente paciente;
    private LocalDateTime fechaHora;
    private double subtotal;
    private double total;
    private String estadoFactura;
    private String metodoPago;
    private ArrayList<DetalleFactura> detalles;

    public Factura() {
        this.detalles = new ArrayList<>();
    }

    public Factura(int id, Propietario propietario, Paciente paciente,
            LocalDateTime fechaHora, double subtotal, double total,
            String estadoFactura, String metodoPago) {
        this.id = id;
        this.propietario = propietario;
        this.paciente = paciente;
        this.fechaHora = fechaHora;
        this.subtotal = subtotal;
        this.total = total;
        this.estadoFactura = estadoFactura;
        this.metodoPago = metodoPago;
        this.detalles = new ArrayList<>();
    }

    public void agregarDetalle(DetalleFactura detalleFactura) {
        detalleFactura.setSubtotal(detalleFactura.calcularSubtotal());
        detalles.add(detalleFactura);
        this.subtotal += detalleFactura.getSubtotal();
    }

    public double calcularTotal() {
        return subtotal * (1 + TASA_IVA);
    }

    public void anular() {
        if ("ANULADA".equals(estadoFactura)) {
            throw new IllegalStateException("La factura ya esta anulada.");
        }
        if ("PAGADA".equals(estadoFactura)) {
            throw new IllegalStateException("No se puede anular una factura pagada.");
        }
        this.estadoFactura = "ANULADA";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstadoFactura() {
        return estadoFactura;
    }

    public void setEstadoFactura(String estadoFactura) {
        this.estadoFactura = estadoFactura;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public ArrayList<DetalleFactura> getDetalles() {
        return detalles;
    }

    public void setDetalles(ArrayList<DetalleFactura> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        String nom = (propietario != null) ? propietario.getNombreCompleto() : "?";
        return "Factura #" + id + " - " + nom;
    }
}
