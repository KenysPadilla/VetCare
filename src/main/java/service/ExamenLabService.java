package service;

import dao.IDAO;
import dao.impl.ExamenLabDAO;
import model.ExamenLab;
import java.sql.SQLException;
import java.util.ArrayList;

public class ExamenLabService {

    private IDAO<ExamenLab> dao;

    public ExamenLabService() {
        this.dao = new ExamenLabDAO();
    }

    public void guardar(ExamenLab examenLab) throws SQLException {
        if (examenLab.getPaciente() == null) {
            throw new IllegalArgumentException("El examen debe tener un paciente asignado.");
        }
        if (examenLab.getVeterinario() == null) {
            throw new IllegalArgumentException("El examen debe tener un veterinario asignado.");
        }
        if (examenLab.getFechaHora() == null) {
            throw new IllegalArgumentException("La fecha del examen es obligatoria.");
        }
        if (examenLab.getTipoExamen() == null || examenLab.getTipoExamen().isBlank()) {
            throw new IllegalArgumentException("El tipo de examen es obligatorio.");
        }
        dao.guardar(examenLab);
    }

    public ArrayList<ExamenLab> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public ExamenLab buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public ArrayList<ExamenLab> listarPorPaciente(int idPaciente) throws SQLException {
        return ((ExamenLabDAO) dao).listarPorPaciente(idPaciente);
    }

    public void actualizar(ExamenLab examenLab) throws SQLException {
        dao.actualizar(examenLab);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }
}
