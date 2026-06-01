package service;

import dao.IDAO;
import dao.impl.CirugiaDAO;
import model.Cirugia;

import java.sql.SQLException;
import java.util.ArrayList;

public class CirugiaService {

    private IDAO<Cirugia> dao;

    public CirugiaService() {
        this.dao = new CirugiaDAO();
    }

    public void guardar(Cirugia cirugia) throws SQLException {
        if (cirugia.getPaciente() == null) {
            throw new IllegalArgumentException("La cirugia debe tener un paciente asignado.");
        }
        if (cirugia.getVeterinario() == null) {
            throw new IllegalArgumentException("La cirugia debe tener un veterinario asignado.");
        }
        dao.guardar(cirugia);
    }

    public ArrayList<Cirugia> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Cirugia buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public void actualizarResultado(int id, String resultado) throws SQLException {
        ((CirugiaDAO) dao).actualizarResultado(id, resultado);
    }

    public ArrayList<Cirugia> listarPorPaciente(int idPaciente) throws SQLException {
        return ((CirugiaDAO) dao).listarPorPaciente(idPaciente);
    }

    public void actualizar(Cirugia cirugia) throws SQLException {
        dao.actualizar(cirugia);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }
}
