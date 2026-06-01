package dao.impl;

import dao.IDAO;
import model.Estilista;
import util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;

public class EstilistaDAO implements IDAO<Estilista> {

    private Connection conexion;

    public EstilistaDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Estilista estilista) throws SQLException {
        String sql = "INSERT INTO ESTILISTA (cedula, nombre, apellido, telefono, email, "
                   + "especialidad_estetica) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estilista.getCedula());
            ps.setString(2, estilista.getNombre());
            ps.setString(3, estilista.getApellido());
            ps.setString(4, estilista.getTelefono());
            ps.setString(5, estilista.getEmail());
            ps.setString(6, estilista.getEspecialidadEstetica());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Estilista estilista) throws SQLException {
        String sql = "UPDATE ESTILISTA SET nombre=?, apellido=?, telefono=?, email=?, "
                   + "especialidad_estetica=? WHERE cedula=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estilista.getNombre());
            ps.setString(2, estilista.getApellido());
            ps.setString(3, estilista.getTelefono());
            ps.setString(4, estilista.getEmail());
            ps.setString(5, estilista.getEspecialidadEstetica());
            ps.setString(6, estilista.getCedula());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        return false;
    }

    public boolean eliminarPorCedula(String cedula) throws SQLException {
        String sql = "DELETE FROM ESTILISTA WHERE cedula=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedula);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Estilista buscarPorId(int id) throws SQLException {
        return null;
    }

    public Estilista buscarPorCedula(String cedula) throws SQLException {
        String sql = "SELECT * FROM ESTILISTA WHERE cedula=?";
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
    public ArrayList<Estilista> listarTodos() throws SQLException {
        ArrayList<Estilista> lista = new ArrayList<>();
        String sql = "SELECT * FROM ESTILISTA ORDER BY activo DESC, apellido, nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Estilista> listarActivos() throws SQLException {
        ArrayList<Estilista> lista = new ArrayList<>();
        String sql = "SELECT * FROM ESTILISTA WHERE activo = 1 ORDER BY apellido, nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Estilista> buscarPorNombre(String texto) throws SQLException {
        ArrayList<Estilista> lista = new ArrayList<>();
        String filtro = "%" + texto.toUpperCase() + "%";
        String sql = "SELECT * FROM ESTILISTA WHERE activo = 1 "
                   + "AND (UPPER(nombre) LIKE ? OR UPPER(apellido) LIKE ?) "
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

    public ArrayList<Estilista> buscarPorEspecialidad(String especialidad) throws SQLException {
        ArrayList<Estilista> lista = new ArrayList<>();
        String sql = "SELECT * FROM ESTILISTA "
                   + "WHERE activo = 1 "
                   + "AND (UPPER(especialidad_estetica) = UPPER(?) "
                   + "OR UPPER(especialidad_estetica) = 'AMBOS') "
                   + "ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, especialidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public void desactivar(String cedula) throws SQLException {
        String sql = "UPDATE ESTILISTA SET activo = 0 WHERE cedula = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.executeUpdate();
        }
    }

    public void reactivar(String cedula) throws SQLException {
        String sql = "UPDATE ESTILISTA SET activo = 1 WHERE cedula = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.executeUpdate();
        }
    }

    private Estilista mapear(ResultSet rs) throws SQLException {
        return new Estilista(
                rs.getString("cedula"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("especialidad_estetica"),
                rs.getInt("activo") == 1
        );
    }
}
