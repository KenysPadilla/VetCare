package model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Internacion {

    private int id;
    private Paciente paciente;
    private Veterinario veterinario;
    private Consulta consulta;
    private LocalDateTime fechaHoraIngreso;
    private LocalDateTime fechaHoraEgreso;
    private String motivo;
    private String diagnostico;
    private double costoDia;
    private String observaciones;
    private List<InternacionMedicamento> medicamentos = new ArrayList<>();
    private double costoMedicamentosTotal = 0.0;

    public Internacion() {
    }

    public Internacion(int id, Paciente paciente, Veterinario veterinario, Consulta consulta,
            LocalDateTime fechaHoraIngreso, LocalDateTime fechaHoraEgreso,
            String motivo, String diagnostico, double costoDia, String observaciones) {
        this.id = id;
        this.paciente = paciente;
        this.veterinario = veterinario;
        this.consulta = consulta;
        this.fechaHoraIngreso = fechaHoraIngreso;
        this.fechaHoraEgreso = fechaHoraEgreso;
        this.motivo = motivo;
        this.diagnostico = diagnostico;
        this.costoDia = costoDia;
        this.observaciones = observaciones;
    }

    public double calcularCostoDias() {
        if (fechaHoraEgreso == null) {
            return 0.0;
        }
        return (double) ChronoUnit.DAYS.between(
                fechaHoraIngreso.toLocalDate(), fechaHoraEgreso.toLocalDate()) * costoDia;
    }

    public double calcularCostoMedicamentos() {
        if (!medicamentos.isEmpty()) {
            double total = 0.0;
            for (InternacionMedicamento im : medicamentos) {
                if (im.getMedicamento() != null) {
                    total += im.getMedicamento().getPrecio() * im.getCantidad();
                }
            }
            return total;
        }
        return costoMedicamentosTotal;
    }

    public void setCostoMedicamentosTotal(double v) {
        this.costoMedicamentosTotal = v;
    }

    public double calcularCostoTotal() {
        return calcularCostoDias() + calcularCostoMedicamentos();
    }

    @Override
    public String toString() {
        return "Internacion #" + id + " - "
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

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public LocalDateTime getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }

    public void setFechaHoraIngreso(LocalDateTime fechaHoraIngreso) {
        this.fechaHoraIngreso = fechaHoraIngreso;
    }

    public LocalDateTime getFechaHoraEgreso() {
        return fechaHoraEgreso;
    }

    public void setFechaHoraEgreso(LocalDateTime fechaHoraEgreso) {
        this.fechaHoraEgreso = fechaHoraEgreso;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public double getCostoDia() {
        return costoDia;
    }

    public void setCostoDia(double costoDia) {
        this.costoDia = costoDia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<InternacionMedicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<InternacionMedicamento> medicamentos) {
        this.medicamentos = medicamentos != null ? medicamentos : new ArrayList<>();
    }
}
