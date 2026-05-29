package service;

import dao.IDAO;
import dao.impl.InternacionDAO;
import model.Internacion;
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
}
