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

    public void guardar(Estilista e) throws SQLException {
        if (e.getCedula() == null || e.getCedula().isBlank()) {
            throw new IllegalArgumentException("La cedula del estilista es obligatoria.");
        }
        if (e.getNombre() == null || e.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del estilista es obligatorio.");
        }
        dao.guardar(e);
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

    public void actualizar(Estilista e) throws SQLException {
        dao.actualizar(e);
    }

    public boolean eliminar(String cedula) throws SQLException {
        return ((EstilistaDAO) dao).eliminarPorCedula(cedula);
    }

    public ArrayList<Estilista> listarActivos() throws SQLException {
        return ((EstilistaDAO) dao).listarActivos();
    }

    public void desactivar(String cedula) throws SQLException {
        ((EstilistaDAO) dao).desactivar(cedula);
        new dao.impl.UsuarioDAO().desactivarPorEmpleado(cedula);
    }

    public void reactivar(String cedula) throws SQLException {
        ((EstilistaDAO) dao).reactivar(cedula);
        new dao.impl.UsuarioDAO().reactivarPorEmpleado(cedula);
    }
}
