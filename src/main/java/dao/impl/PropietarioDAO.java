package dao.impl;

import dao.IDAO;
import model.Propietario;
import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;

public class PropietarioDAO implements IDAO<Propietario> {

    private Connection conexion;

    public PropietarioDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Propietario p) throws SQLException {
        String sql = "INSERT INTO PROPIETARIO (cedula, nombre, apellido, telefono, email, direccion) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, p.getCedula());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getApellido());
            ps.setString(4, p.getTelefono());
            ps.setString(5, p.getEmail());
            ps.setString(6, p.getDireccion());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Propietario p) throws SQLException {
        String sql = "UPDATE PROPIETARIO SET nombre=?, apellido=?, telefono=?, email=?, direccion=? "
                + "WHERE cedula=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getApellido());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getEmail());
            ps.setString(5, p.getDireccion());
            ps.setString(6, p.getCedula());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        return false;
    }

    public boolean eliminarPorCedula(String cedula) throws SQLException {
        String sql = "DELETE FROM PROPIETARIO WHERE cedula=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedula);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Propietario buscarPorId(int id) throws SQLException {
        return null;
    }

    public Propietario buscarPorCedula(String cedula) throws SQLException {
        String sql = "SELECT * FROM PROPIETARIO WHERE cedula=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    @Override
    public ArrayList<Propietario> listarTodos() throws SQLException {
        ArrayList<Propietario> lista = new ArrayList<>();
        String sql = "SELECT * FROM PROPIETARIO ORDER BY apellido, nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Propietario> buscarPorNombre(String texto) throws SQLException {
        ArrayList<Propietario> lista = new ArrayList<>();
        String filtro = "%" + texto.toUpperCase() + "%";
        String sql = "SELECT * FROM PROPIETARIO WHERE UPPER(nombre) LIKE ? OR UPPER(apellido) LIKE ? "
                + "ORDER BY apellido, nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public String obtenerNombreCompleto(String cedula) throws SQLException {
        String sql = "{ ? = call fn_nombre_completo_propietario(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.VARCHAR);
            cs.setString(2, cedula);
            cs.execute();
            return cs.getString(1);
        }
    }

    private Propietario mapear(ResultSet rs) throws SQLException {
        return new Propietario(
                rs.getString("cedula"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("direccion")
        );
    }
}
