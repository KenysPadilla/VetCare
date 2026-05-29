package service;

import dao.IDAO;
import dao.impl.ConsultaDAO;
import model.Consulta;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConsultaService {

    private IDAO<Consulta> dao;

    public ConsultaService() {
        this.dao = new ConsultaDAO();
    }

    public void guardar(Consulta consulta) throws SQLException {
        if (consulta.getCita() == null) {
            throw new IllegalArgumentException("La consulta debe tener una cita asociada.");
        }
        if (consulta.getPaciente() == null) {
            throw new IllegalArgumentException("La consulta debe tener un paciente asignado.");
        }
        if (consulta.getVeterinario() == null) {
            throw new IllegalArgumentException("La consulta debe tener un veterinario asignado.");
        }
        ((ConsultaDAO) dao).completarConsulta(consulta);
    }

    public ArrayList<Consulta> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Consulta buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public ArrayList<Consulta> listarPorPaciente(int idPaciente) throws SQLException {
        return ((ConsultaDAO) dao).listarPorPaciente(idPaciente);
    }

    public Consulta buscarPorCita(int idCita) throws SQLException {
        return ((ConsultaDAO) dao).buscarPorCita(idCita);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }
}
