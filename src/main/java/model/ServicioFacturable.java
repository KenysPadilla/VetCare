package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ServicioFacturable {

    private final BooleanProperty seleccionado = new SimpleBooleanProperty(false);
    private final String tipo;
    private final String descripcion;
    private final String fecha;
    private final double costo;

    public ServicioFacturable(String tipo, String descripcion, String fecha, double costo) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.costo = costo;
    }

    public BooleanProperty seleccionadoProperty() {
        return seleccionado;
    }

    public boolean isSeleccionado() {
        return seleccionado.get();
    }

    public void setSeleccionado(boolean v) {
        seleccionado.set(v);
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public double getCosto() {
        return costo;
    }
}
