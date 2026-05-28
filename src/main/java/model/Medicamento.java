package model;

import java.time.LocalDate;

public class Medicamento {

    private int id;
    private String nombre;
    private String descripcion;
    private String fabricante;
    private double precio;
    private int stockDisponible;
    private LocalDate fechaVencimiento;
    private String concentracion;

    public Medicamento() {
    }

    public Medicamento(int id, String nombre, String descripcion, String fabricante,
            double precio, int stockDisponible, LocalDate fechaVencimiento) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fabricante = fabricante;
        this.precio = precio;
        this.stockDisponible = stockDisponible;
        this.fechaVencimiento = fechaVencimiento;
    }

    public boolean estaDisponible() {
        return stockDisponible > 0;
    }

    @Override
    public String toString() {
        return nombre + " (" + fabricante + ")";
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
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

    public String getConcentracion() {
        return concentracion;
    }

    public void setConcentracion(String concentracion) {
        this.concentracion = concentracion;
    }
}
