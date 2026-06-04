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
        String sql = "INSERT INTO USUARIO (nombre_usuario, contrasena, rol, activo, cedula_empleado) "
                   + "VALUES (?, ?, ?, 1, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getContrasena());
            ps.setString(3, usuario.getRol());
            ps.setString(4, usuario.getCedulaEmpleado());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE USUARIO SET contrasena=?, rol=?, activo=?, cedula_empleado=? "
                   + "WHERE nombre_usuario=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario.getContrasena());
            ps.setString(2, usuario.getRol());
            ps.setInt(3, usuario.isActivo() ? 1 : 0);
            ps.setString(4, usuario.getCedulaEmpleado());
            ps.setString(5, usuario.getNombreUsuario());
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
        String sql =
            "SELECT U.*, "
          + "CASE U.rol "
          + "  WHEN 'VETERINARIO' THEN (SELECT V.nombre||' '||V.apellido FROM VETERINARIO V WHERE V.cedula = U.cedula_empleado AND ROWNUM = 1) "
          + "  WHEN 'ESTILISTA'   THEN (SELECT E.nombre||' '||E.apellido FROM ESTILISTA   E WHERE E.cedula = U.cedula_empleado AND ROWNUM = 1) "
          + "  ELSE NULL END AS nombre_empleado, "
          + "CASE U.rol "
          + "  WHEN 'VETERINARIO' THEN NVL((SELECT V.activo FROM VETERINARIO V WHERE V.cedula = U.cedula_empleado AND ROWNUM = 1), U.activo) "
          + "  WHEN 'ESTILISTA'   THEN NVL((SELECT E.activo FROM ESTILISTA   E WHERE E.cedula = U.cedula_empleado AND ROWNUM = 1), U.activo) "
          + "  ELSE U.activo END AS activo_efectivo, "
          + "CASE U.rol "
          + "  WHEN 'VETERINARIO' THEN (SELECT V.nombre   FROM VETERINARIO V WHERE V.cedula = U.cedula_empleado AND ROWNUM = 1) "
          + "  WHEN 'ESTILISTA'   THEN (SELECT E.nombre   FROM ESTILISTA   E WHERE E.cedula = U.cedula_empleado AND ROWNUM = 1) "
          + "  ELSE NULL END AS nombre_display, "
          + "CASE U.rol "
          + "  WHEN 'VETERINARIO' THEN (SELECT V.apellido FROM VETERINARIO V WHERE V.cedula = U.cedula_empleado AND ROWNUM = 1) "
          + "  WHEN 'ESTILISTA'   THEN (SELECT E.apellido FROM ESTILISTA   E WHERE E.cedula = U.cedula_empleado AND ROWNUM = 1) "
          + "  ELSE NULL END AS apellido_display, "
          + "CASE U.rol "
          + "  WHEN 'VETERINARIO' THEN (SELECT V.email    FROM VETERINARIO V WHERE V.cedula = U.cedula_empleado AND ROWNUM = 1) "
          + "  WHEN 'ESTILISTA'   THEN (SELECT E.email    FROM ESTILISTA   E WHERE E.cedula = U.cedula_empleado AND ROWNUM = 1) "
          + "  WHEN 'ADMIN'       THEN 'vetcare.veterinaria01@gmail.com' "
          + "  ELSE NULL END AS email_display, "
          + "CASE U.rol "
          + "  WHEN 'VETERINARIO' THEN (SELECT V.telefono FROM VETERINARIO V WHERE V.cedula = U.cedula_empleado AND ROWNUM = 1) "
          + "  WHEN 'ESTILISTA'   THEN (SELECT E.telefono FROM ESTILISTA   E WHERE E.cedula = U.cedula_empleado AND ROWNUM = 1) "
          + "  ELSE NULL END AS telefono_display "
          + "FROM USUARIO U ORDER BY U.nombre_usuario";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario usuario = mapear(rs);
                usuario.setNombreEmpleado(rs.getString("nombre_empleado"));
                usuario.setNombre(rs.getString("nombre_display"));
                usuario.setApellido(rs.getString("apellido_display"));
                usuario.setEmail(rs.getString("email_display"));
                usuario.setTelefono(rs.getString("telefono_display"));
                usuario.setCedula(rs.getString("cedula_empleado"));
                try {
                    usuario.setActivo(rs.getInt("activo_efectivo") == 1);
                } catch (SQLException ignorado) {}
                lista.add(usuario);
            }
        }
        return lista;
    }

    public Usuario autenticar(String nombreUsuario, String contrasena) throws SQLException {
        String sql =
            "SELECT U.* FROM USUARIO U "
          + "WHERE U.nombre_usuario = ? AND U.contrasena = ? AND U.activo = 1 "
          + "AND NOT EXISTS ( "
          + "  SELECT 1 FROM VETERINARIO V "
          + "  WHERE V.cedula = U.cedula_empleado AND V.activo = 0 AND U.rol = 'VETERINARIO' "
          + ") "
          + "AND NOT EXISTS ( "
          + "  SELECT 1 FROM ESTILISTA E "
          + "  WHERE E.cedula = U.cedula_empleado AND E.activo = 0 AND U.rol = 'ESTILISTA' "
          + ")";
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

    public void guardarCodigoRecuperacion(String nombreUsuario,
                                          String codigo,
                                          java.time.LocalDateTime expiracion)
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

    public void desactivarPorEmpleado(String cedulaEmpleado) throws SQLException {
        String sql = "UPDATE USUARIO SET activo = 0 WHERE cedula_empleado = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedulaEmpleado);
            ps.executeUpdate();
        }
    }

    public void reactivarPorEmpleado(String cedulaEmpleado) throws SQLException {
        String sql = "UPDATE USUARIO SET activo = 1 WHERE cedula_empleado = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedulaEmpleado);
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
        usuario.setNombreUsuario(rs.getString("nombre_usuario"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setRol(rs.getString("rol"));
        usuario.setActivo(rs.getInt("activo") == 1);

        String codigo = rs.getString("codigo_recuperacion");
        usuario.setCodigoRecuperacion(codigo);
        java.sql.Timestamp expiracion = rs.getTimestamp("expiracion_codigo");
        usuario.setExpiracionCodigo(expiracion != null ? expiracion.toLocalDateTime() : null);

        try {
            usuario.setCedulaEmpleado(rs.getString("cedula_empleado"));
        } catch (SQLException ignorado) {}

        return usuario;
    }
}
