package service;

import dao.impl.ReporteDAO;

import java.sql.SQLException;
import java.time.LocalDate;

public class ReporteService {

    private final ReporteDAO dao;

    public ReporteService() {
        this.dao = new ReporteDAO();
    }

    public int consultasPorVeterinario(String cedulaVet, LocalDate desde, LocalDate hasta)
            throws SQLException {
        if (cedulaVet == null || cedulaVet.isBlank())
            throw new IllegalArgumentException("La cedula del veterinario es obligatoria.");
        if (desde == null || hasta == null)
            throw new IllegalArgumentException("Las fechas del rango son obligatorias.");
        if (desde.isAfter(hasta))
            throw new IllegalArgumentException("La fecha inicio no puede ser posterior a la fecha fin.");
        return dao.consultasPorVeterinario(cedulaVet, desde, hasta);
    }

    public double ingresosPorTipoConcepto(String tipoConcepto, LocalDate desde, LocalDate hasta)
            throws SQLException {
        if (tipoConcepto == null || tipoConcepto.isBlank())
            throw new IllegalArgumentException("El tipo de concepto es obligatorio.");
        if (desde == null || hasta == null)
            throw new IllegalArgumentException("Las fechas del rango son obligatorias.");
        if (desde.isAfter(hasta))
            throw new IllegalArgumentException("La fecha inicio no puede ser posterior a la fecha fin.");
        return dao.ingresosPorTipoConcepto(tipoConcepto, desde, hasta);
    }

    public Integer idPacienteMasActivo(LocalDate desde, LocalDate hasta)
            throws SQLException {
        if (desde == null || hasta == null)
            throw new IllegalArgumentException("Las fechas del rango son obligatorias.");
        if (desde.isAfter(hasta))
            throw new IllegalArgumentException("La fecha inicio no puede ser posterior a la fecha fin.");
        return dao.idPacienteMasActivo(desde, hasta);
    }

    public double tasaCancelacion(LocalDate desde, LocalDate hasta)
            throws SQLException {
        if (desde == null || hasta == null)
            throw new IllegalArgumentException("Las fechas del rango son obligatorias.");
        if (desde.isAfter(hasta))
            throw new IllegalArgumentException("La fecha inicio no puede ser posterior a la fecha fin.");
        return dao.tasaCancelacion(desde, hasta);
    }
}
