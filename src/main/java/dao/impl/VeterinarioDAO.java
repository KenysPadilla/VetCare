package dao.impl;

import dao.IDAO;
import model.Veterinario;
import util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;

public class VeterinarioDAO implements IDAO<Veterinario> {

    private Connection conexion;

    public VeterinarioDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Veterinario veterinario) throws SQLException {
        String sql = "INSERT INTO VETERINARIO (cedula, nombre, apellido, telefono, email, "
                   + "especialidad, numero_licencia) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, veterinario.getCedula());
            ps.setString(2, veterinario.getNombre());
            ps.setString(3, veterinario.getApellido());
            ps.setString(4, veterinario.getTelefono());
            ps.setString(5, veterinario.getEmail());
            ps.setString(6, veterinario.getEspecialidad());
            ps.setString(7, veterinario.getNumeroLicencia());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Veterinario veterinario) throws SQLException {
        String sql = "UPDATE VETERINARIO SET nombre=?, apellido=?, telefono=?, email=?, "
                   + "especialidad=?, numero_licencia=? WHERE cedula=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, veterinario.getNombre());
            ps.setString(2, veterinario.getApellido());
            ps.setString(3, veterinario.getTelefono());
            ps.setString(4, veterinario.getEmail());
            ps.setString(5, veterinario.getEspecialidad());
            ps.setString(6, veterinario.getNumeroLicencia());
            ps.setString(7, veterinario.getCedula());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        return false;
    }

    public boolean eliminarPorCedula(String cedula) throws SQLException {
        String sql = "DELETE FROM VETERINARIO WHERE cedula=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedula);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Veterinario buscarPorId(int id) throws SQLException {
        return null;
    }

    public Veterinario buscarPorCedula(String cedula) throws SQLException {
        String sql = "SELECT * FROM VETERINARIO WHERE cedula=?";
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
    public ArrayList<Veterinario> listarTodos() throws SQLException {
        ArrayList<Veterinario> lista = new ArrayList<>();
        String sql = "SELECT * FROM VETERINARIO ORDER BY activo DESC, apellido, nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Veterinario> listarActivos() throws SQLException {
        ArrayList<Veterinario> lista = new ArrayList<>();
        String sql = "SELECT * FROM VETERINARIO WHERE activo = 1 ORDER BY apellido, nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Veterinario> buscarPorNombre(String texto) throws SQLException {
        ArrayList<Veterinario> lista = new ArrayList<>();
        String filtro = "%" + texto.toUpperCase() + "%";
        String sql = "SELECT * FROM VETERINARIO WHERE activo = 1 "
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

    public void desactivar(String cedula) throws SQLException {
        String sql = "UPDATE VETERINARIO SET activo = 0 WHERE cedula = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.executeUpdate();
        }
    }

    public void reactivar(String cedula) throws SQLException {
        String sql = "UPDATE VETERINARIO SET activo = 1 WHERE cedula = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.executeUpdate();
        }
    }

    private Veterinario mapear(ResultSet rs) throws SQLException {
        return new Veterinario(
                rs.getString("cedula"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("especialidad"),
                rs.getString("numero_licencia"),
                rs.getInt("activo") == 1
        );
    }
}
