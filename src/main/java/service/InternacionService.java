package service;

import dao.IDAO;
import dao.impl.InternacionDAO;
import dao.impl.InternacionMedicamentoDAO;
import model.Internacion;
import model.InternacionMedicamento;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class InternacionService {

    private IDAO<Internacion> dao;

    public InternacionService() {
        this.dao = new InternacionDAO();
    }

    public void guardar(Internacion internacion) throws SQLException {
        if (internacion.getPaciente() == null) {
            throw new IllegalArgumentException("La internacion debe tener un paciente asignado.");
        }
        if (internacion.getVeterinario() == null) {
            throw new IllegalArgumentException("La internacion debe tener un veterinario asignado.");
        }
        if (internacion.getFechaHoraIngreso() == null) {
            throw new IllegalArgumentException("La fecha de ingreso es obligatoria.");
        }
        dao.guardar(internacion);
    }

    public ArrayList<Internacion> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Internacion buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public ArrayList<Internacion> listarPorPaciente(int idPaciente) throws SQLException {
        return ((InternacionDAO) dao).listarPorPaciente(idPaciente);
    }

    public void actualizar(Internacion internacion) throws SQLException {
        dao.actualizar(internacion);
    }

    public void darEgreso(int id, LocalDateTime fecha) throws SQLException {
        ((InternacionDAO) dao).actualizarEgreso(id, fecha);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }

    public ArrayList<InternacionMedicamento> listarMedicamentosPorInternacion(int idInternacion) throws SQLException {
        return new InternacionMedicamentoDAO().listarPorInternacion(idInternacion);
    }

    public void guardarMedicamentosInternacion(int idInternacion, ArrayList<InternacionMedicamento> medicamentos) throws SQLException {
        InternacionMedicamentoDAO internacionMedicamentoDao = new InternacionMedicamentoDAO();

        ArrayList<InternacionMedicamento> actuales = internacionMedicamentoDao.listarPorInternacion(idInternacion);

        java.util.Map<Integer, Integer> cantidadesActuales = new java.util.HashMap<>();
        for (InternacionMedicamento internacionMedicamento : actuales) {
            if (internacionMedicamento.getMedicamento() != null) {
                cantidadesActuales.put(internacionMedicamento.getMedicamento().getId(), internacionMedicamento.getCantidad());
            }
        }

        MedicamentoService medicamentoService = new MedicamentoService();
        for (InternacionMedicamento internacionMedicamento : medicamentos) {
            if (internacionMedicamento.getMedicamento() == null) continue;
            int idMedicamento      = internacionMedicamento.getMedicamento().getId();
            int cantidadNueva  = internacionMedicamento.getCantidad();
            int cantidadActual = cantidadesActuales.getOrDefault(idMedicamento, 0);
            int diferencia = cantidadNueva - cantidadActual;
            if (diferencia > 0) {
                medicamentoService.descontarStock(idMedicamento, diferencia);
            }
        }

        internacionMedicamentoDao.guardarTodos(idInternacion, medicamentos);
    }
}
