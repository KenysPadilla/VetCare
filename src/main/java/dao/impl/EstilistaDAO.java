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
    public void guardar(Estilista e) throws SQLException {
        String sql = "INSERT INTO ESTILISTA (cedula, nombre, apellido, telefono, email, "
                + "especialidad_estetica) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, e.getCedula());
            ps.setString(2, e.getNombre());
            ps.setString(3, e.getApellido());
            ps.setString(4, e.getTelefono());
            ps.setString(5, e.getEmail());
            ps.setString(6, e.getEspecialidadEstetica());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Estilista e) throws SQLException {
        String sql = "UPDATE ESTILISTA SET nombre=?, apellido=?, telefono=?, email=?, "
                + "especialidad_estetica=? WHERE cedula=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellido());
            ps.setString(3, e.getTelefono());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getEspecialidadEstetica());
            ps.setString(6, e.getCedula());
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
        String sql = "SELECT * FROM ESTILISTA ORDER BY apellido, nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Estilista> buscarPorNombre(String texto) throws SQLException {
        ArrayList<Estilista> lista = new ArrayList<>();
        String filtro = "%" + texto.toUpperCase() + "%";
        String sql = "SELECT * FROM ESTILISTA WHERE UPPER(nombre) LIKE ? OR UPPER(apellido) LIKE ? "
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
        String sql = "SELECT * FROM ESTILISTA " + "WHERE UPPER(especialidad_estetica) = UPPER(?) "
                + "OR UPPER(especialidad_estetica) = 'AMBOS' " + "ORDER BY nombre";
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

    private Estilista mapear(ResultSet rs) throws SQLException {
        return new Estilista(
                rs.getString("cedula"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("especialidad_estetica")
        );
    }
}
