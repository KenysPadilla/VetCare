package service;

import dao.IDAO;
import dao.impl.EstilistaDAO;
import model.Estilista;
import java.sql.SQLException;
import java.util.ArrayList;

public class EstilistaService {

    private IDAO<Estilista> dao;

    public EstilistaService() {
        this.dao = new EstilistaDAO();
    }

    public void guardar(Estilista estilista) throws SQLException {
        if (estilista.getCedula() == null || estilista.getCedula().isBlank()) {
            throw new IllegalArgumentException("La cedula del estilista es obligatoria.");
        }
        if (estilista.getNombre() == null || estilista.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del estilista es obligatorio.");
        }
        dao.guardar(estilista);
    }

    public ArrayList<Estilista> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Estilista buscarPorCedula(String cedula) throws SQLException {
        return ((EstilistaDAO) dao).buscarPorCedula(cedula);
    }

    public ArrayList<Estilista> buscarPorNombre(String texto) throws SQLException {
        return ((EstilistaDAO) dao).buscarPorNombre(texto);
    }

    public ArrayList<Estilista> listarParaBano() throws SQLException {
        return ((EstilistaDAO) dao).buscarPorEspecialidad("Baño");
    }

    public ArrayList<Estilista> listarParaMotilada() throws SQLException {
        return ((EstilistaDAO) dao).buscarPorEspecialidad("Motilada");
    }

    public void actualizar(Estilista estilista) throws SQLException {
        dao.actualizar(estilista);
    }

    public boolean eliminar(String cedula) throws SQLException {
        return ((EstilistaDAO) dao).eliminarPorCedula(cedula);
    }
}
