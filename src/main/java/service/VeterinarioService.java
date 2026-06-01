package service;

import dao.IDAO;
import dao.impl.VeterinarioDAO;
import model.Veterinario;

import java.sql.SQLException;
import java.util.ArrayList;

public class VeterinarioService {

    private IDAO<Veterinario> dao;

    public VeterinarioService() {
        this.dao = new VeterinarioDAO();
    }

    public void guardar(Veterinario v) throws SQLException {
        if (v.getCedula() == null || v.getCedula().isBlank()) {
            throw new IllegalArgumentException("La cedula del veterinario es obligatoria.");
        }
        if (v.getNombre() == null || v.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del veterinario es obligatorio.");
        }
        dao.guardar(v);
    }

    public ArrayList<Veterinario> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Veterinario buscarPorCedula(String cedula) throws SQLException {
        return ((VeterinarioDAO) dao).buscarPorCedula(cedula);
    }

    public ArrayList<Veterinario> buscarPorNombre(String texto) throws SQLException {
        return ((VeterinarioDAO) dao).buscarPorNombre(texto);
    }

    public void actualizar(Veterinario v) throws SQLException {
        dao.actualizar(v);
    }

    public boolean eliminar(String cedula) throws SQLException {
        return ((VeterinarioDAO) dao).eliminarPorCedula(cedula);
    }

    public ArrayList<Veterinario> listarActivos() throws SQLException {
        return ((VeterinarioDAO) dao).listarActivos();
    }

    public void desactivar(String cedula) throws SQLException {
        ((VeterinarioDAO) dao).desactivar(cedula);
        new dao.impl.UsuarioDAO().desactivarPorEmpleado(cedula);
    }

    public void reactivar(String cedula) throws SQLException {
        ((VeterinarioDAO) dao).reactivar(cedula);
        new dao.impl.UsuarioDAO().reactivarPorEmpleado(cedula);
    }
}
