package service;

import dao.IDAO;
import dao.impl.VacunacionDAO;
import model.Vacunacion;

import java.sql.SQLException;
import java.util.ArrayList;

public class VacunacionService {

    private IDAO<Vacunacion> dao;

    public VacunacionService() {
        this.dao = new VacunacionDAO();
    }

    public void guardar(Vacunacion vacunacion) throws SQLException {
        if (vacunacion.getPaciente() == null) {
            throw new IllegalArgumentException("La vacunacion debe tener un paciente asignado.");
        }
        if (vacunacion.getVacuna() == null) {
            throw new IllegalArgumentException("Debe seleccionar una vacuna.");
        }
        if (vacunacion.getFechaHoraAplicacion() == null) {
            throw new IllegalArgumentException("La fecha de aplicacion es obligatoria.");
        }
        dao.guardar(vacunacion);
    }

    public ArrayList<Vacunacion> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Vacunacion buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public ArrayList<Vacunacion> listarPorPaciente(int idPaciente) throws SQLException {
        return ((VacunacionDAO) dao).listarPorPaciente(idPaciente);
    }

    public void actualizar(Vacunacion vacunacion) throws SQLException {
        dao.actualizar(vacunacion);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }

    public void marcarAplicada(int id) throws SQLException {
        ((VacunacionDAO) dao).marcarAplicada(id);
    }
}
