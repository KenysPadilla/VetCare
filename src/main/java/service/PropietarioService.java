package service;

import dao.IDAO;
import dao.impl.PropietarioDAO;
import model.Propietario;
import java.sql.SQLException;
import java.util.ArrayList;

public class PropietarioService {

    private IDAO<Propietario> dao;

    public PropietarioService() {
        this.dao = new PropietarioDAO();
    }

    public void guardar(Propietario propietario) throws SQLException {
        if (propietario.getCedula() == null || propietario.getCedula().isBlank()) {
            throw new IllegalArgumentException("La cedula del propietario es obligatoria.");
        }
        if (propietario.getNombre() == null || propietario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del propietario es obligatorio.");
        }
        dao.guardar(propietario);
    }

    public ArrayList<Propietario> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Propietario buscarPorCedula(String cedula) throws SQLException {
        return ((PropietarioDAO) dao).buscarPorCedula(cedula);
    }

    public ArrayList<Propietario> buscarPorNombre(String texto) throws SQLException {
        return ((PropietarioDAO) dao).buscarPorNombre(texto);
    }

    public void actualizar(Propietario propietario) throws SQLException {
        dao.actualizar(propietario);
    }

    public boolean eliminar(String cedula) throws SQLException {
        return ((PropietarioDAO) dao).eliminarPorCedula(cedula);
    }
}
