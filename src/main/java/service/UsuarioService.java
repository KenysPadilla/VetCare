package service;

import dao.IDAO;
import dao.impl.UsuarioDAO;
import model.Usuario;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsuarioService {

    private IDAO<Usuario> dao;

    public UsuarioService() {
        this.dao = new UsuarioDAO();
    }

    public Usuario autenticar(String nombreUsuario, String contrasena) throws SQLException {
        if (nombreUsuario == null || nombreUsuario.isBlank()
                || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("Credenciales vacias");
        }
        return ((UsuarioDAO) dao).autenticar(nombreUsuario, contrasena);
    }

    public void guardar(Usuario usuario) throws SQLException {
        if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        dao.guardar(usuario);
    }

    public ArrayList<Usuario> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public void actualizar(Usuario usuario) throws SQLException {
        dao.actualizar(usuario);
    }
}
