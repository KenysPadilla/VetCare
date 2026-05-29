package service;

import dao.IDAO;
import dao.impl.MedicamentoDAO;
import model.Medicamento;
import java.sql.SQLException;
import java.util.ArrayList;

public class MedicamentoService {

    private IDAO<Medicamento> dao;

    public MedicamentoService() {
        this.dao = new MedicamentoDAO();
    }

    public void guardar(Medicamento medicamento) throws SQLException {
        if (medicamento.getNombre() == null || medicamento.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del medicamento es obligatorio.");
        }
        dao.guardar(medicamento);
    }

    public ArrayList<Medicamento> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Medicamento buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public ArrayList<Medicamento> buscarPorNombre(String texto) throws SQLException {
        return ((MedicamentoDAO) dao).buscarPorNombre(texto);
    }

    public void actualizar(Medicamento medicamento) throws SQLException {
        dao.actualizar(medicamento);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }
}
