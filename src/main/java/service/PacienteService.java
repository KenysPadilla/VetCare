package service;

import dao.IDAO;
import dao.impl.PacienteDAO;
import model.Paciente;
import java.sql.SQLException;
import java.util.ArrayList;

public class PacienteService {

    private IDAO<Paciente> dao;

    public PacienteService() {
        this.dao = new PacienteDAO();
    }

    public void guardar(Paciente pacienteaciente) throws SQLException {
        if (paciente.getNombre() == null || paciente.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del paciente es obligatorio.");
        }
        dao.guardar(paciente);
    }

    public ArrayList<Paciente> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Paciente buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public void actualizar(Paciente paciente) throws SQLException {
        dao.actualizar(p);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }

    public ArrayList<Paciente> buscarPorPropietario(String cedulaPropietario) throws SQLException {
        return ((PacienteDAO) dao).buscarPorPropietario(cedulaPropietario);
    }

    public ArrayList<Paciente> buscarPorNombre(String texto) throws SQLException {
        return ((PacienteDAO) dao).buscarPorNombre(texto);
    }
}
