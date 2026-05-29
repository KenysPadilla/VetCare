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

    public void guardar(Veterinario veterinario) throws SQLException {
        if (veterinario.getCedula() == null || veterinario.getCedula().isBlank()) {
            throw new IllegalArgumentException("La cedula del veterinario es obligatoria.");
        }
        if (veterinario.getNombre() == null || veterinario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del veterinario es obligatorio.");
        }
        dao.guardar(veterinario);
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

    public void actualizar(Veterinario veterinario) throws SQLException {
        dao.actualizar(veterinario);
    }

    public boolean eliminar(String cedula) throws SQLException {
        return ((VeterinarioDAO) dao).eliminarPorCedula(cedula);
    }
}
