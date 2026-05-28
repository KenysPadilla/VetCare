package dao.impl;

import dao.IDAO;
import model.Usuario;
import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;

public class UsuarioDAO implements IDAO<Usuario> {

    private Connection conexion;

    public UsuarioDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO USUARIO (cedula, nombre, apellido, telefono, email, "
                   + "nombre_usuario, contrasena, rol, activo) VALUES (?,?,?,?,?,?,?,?,1)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario.getCedula());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getEmail());
            ps.setString(6, usuario.getNombreUsuario());
            ps.setString(7, usuario.getContrasena());
            ps.setString(8, usuario.getRol());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE USUARIO SET cedula=?, nombre=?, apellido=?, telefono=?, "
                   + "email=?, contrasena=?, rol=?, activo=? WHERE nombre_usuario=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario.getCedula());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getEmail());
            ps.setString(6, usuario.getContrasena());
            ps.setString(7, usuario.getRol());
            ps.setInt(8, usuario.isActivo() ? 1 : 0);
            ps.setString(9, usuario.getNombreUsuario());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        return false;
    }

    @Override
    public Usuario buscarPorId(int id) throws SQLException {
        return null;
    }

    @Override
    public ArrayList<Usuario> listarTodos() throws SQLException {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM USUARIO ORDER BY nombre_usuario";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Usuario autenticar(String nombreUsuario, String contrasena) throws SQLException {
        String sql = "SELECT * FROM USUARIO WHERE nombre_usuario=? AND contrasena=? AND activo=1";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public Usuario buscarPorNombreUsuario(String nombreUsuario) throws SQLException {
        String sql = "SELECT * FROM USUARIO WHERE nombre_usuario=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public void guardarCodigoRecuperacion(String nombreUsuario, String codigo, java.time.LocalDateTime expiracion)
            throws SQLException {
        String sql = "UPDATE USUARIO SET codigo_recuperacion=?, expiracion_codigo=? "
                   + "WHERE nombre_usuario=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(expiracion));
            ps.setString(3, nombreUsuario);
            ps.executeUpdate();
        }
    }

    public void actualizarPassword(String nombreUsuario, String nuevaPassword)
            throws SQLException {
        String sql = "UPDATE USUARIO SET contrasena=? WHERE nombre_usuario=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevaPassword);
            ps.setString(2, nombreUsuario);
            ps.executeUpdate();
        }
    }

    public void limpiarCodigoRecuperacion(String nombreUsuario) throws SQLException {
        String sql = "UPDATE USUARIO SET codigo_recuperacion=NULL, "
                   + "expiracion_codigo=NULL WHERE nombre_usuario=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.executeUpdate();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setCedula(rs.getString("cedula"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setEmail(rs.getString("email"));
        usuario.setNombreUsuario(rs.getString("nombre_usuario"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setRol(rs.getString("rol"));
        usuario.setActivo(rs.getInt("activo") == 1);
        String cod = rs.getString("codigo_recuperacion");
        usuario.setCodigoRecuperacion(cod);
        java.sql.Timestamp exp = rs.getTimestamp("expiracion_codigo");
        usuario.setExpiracionCodigo(exp != null ? exp.toLocalDateTime() : null);

        return usuario;
    }
}
