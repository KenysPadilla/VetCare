package service;

import dao.IDAO;
import dao.impl.VacunaDAO;
import model.Vacuna;

import java.sql.SQLException;
import java.util.ArrayList;

public class VacunaService {

    private IDAO<Vacuna> dao;

    public VacunaService() {
        this.dao = new VacunaDAO();
    }

    public void guardar(Vacuna vacuna) throws SQLException {
        if (vacuna.getNombre() == null || vacuna.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la vacuna es obligatorio.");
        }
        dao.guardar(vacuna);
    }

    public ArrayList<Vacuna> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Vacuna buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public ArrayList<Vacuna> buscarPorNombre(String texto) throws SQLException {
        return ((VacunaDAO) dao).buscarPorNombre(texto);
    }

    public void actualizar(Vacuna vacuna) throws SQLException {
        dao.actualizar(vacuna);
    }

    public void agregarStock(int id, int cantidad) throws SQLException {
        ((VacunaDAO) dao).agregarStock(id, cantidad);
    }

    public void reponerStock(int id, int cantidad) throws SQLException {
        ((VacunaDAO) dao).reponerStock(id, cantidad);
    }

    public boolean esStockCritico(int id, int umbral) throws SQLException {
        return ((VacunaDAO) dao).esStockCritico(id, umbral);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }
}
