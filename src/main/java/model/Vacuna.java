package model;

import java.time.LocalDate;

public class Vacuna {

    private int id;
    private String nombre;
    private String laboratorio;
    private String lote;
    private double precio;
    private int stockDisponible;
    private LocalDate fechaVencimiento;

    public Vacuna() {
    }

    public Vacuna(int id, String nombre, String laboratorio, String lote,
            double precio, int stockDisponible, LocalDate fechaVencimiento) {
        this.id = id;
        this.nombre = nombre;
        this.laboratorio = laboratorio;
        this.lote = lote;
        this.precio = precio;
        this.stockDisponible = stockDisponible;
        this.fechaVencimiento = fechaVencimiento;
    }

    public String calcularEstado() {
        if (fechaVencimiento != null && fechaVencimiento.isBefore(java.time.LocalDate.now())) {
            return "Vencido";
        }
        if (stockDisponible == 0) {
            return "Sin stock";
        }
        if (stockDisponible <= 5) {
            return "Stock bajo";
        }
        return "Disponible";
    }

    @Override
    public String toString() {
        return nombre + " - Lab: " + laboratorio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(int stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}
