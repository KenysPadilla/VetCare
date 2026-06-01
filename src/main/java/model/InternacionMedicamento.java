package model;

import java.time.LocalDate;

public class InternacionMedicamento {

    private int id;
    private Internacion internacion;
    private Medicamento medicamento;
    private int cantidad;
    private LocalDate fechaAplicacion;
    private String dosis;

    public InternacionMedicamento() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Internacion getInternacion() {
        return internacion;
    }

    public void setInternacion(Internacion internacion) {
        this.internacion = internacion;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDate fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    @Override
    public String toString() {
        if (medicamento == null) {
            return "—";
        }
        String conc = (medicamento.getConcentracion() != null && !medicamento.getConcentracion().isBlank())
                ? " [" + medicamento.getConcentracion() + "]" : "";
        String dosisStr = (dosis != null && !dosis.isBlank()) ? " — " + dosis : "";
        String cantStr = cantidad > 1 ? " x" + cantidad : "";
        return medicamento.getNombre() + conc + cantStr + dosisStr;
    }
}
