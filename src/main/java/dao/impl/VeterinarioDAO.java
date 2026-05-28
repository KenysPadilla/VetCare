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
    public void guardar(Veterinario v) throws SQLException {
        String sql = "INSERT INTO VETERINARIO (cedula, nombre, apellido, telefono, email, "
                + "especialidad, numero_licencia) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, v.getCedula());
            ps.setString(2, v.getNombre());
            ps.setString(3, v.getApellido());
            ps.setString(4, v.getTelefono());
            ps.setString(5, v.getEmail());
            ps.setString(6, v.getEspecialidad());
            ps.setString(7, v.getNumeroLicencia());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Veterinario v) throws SQLException {
        String sql = "UPDATE VETERINARIO SET nombre=?, apellido=?, telefono=?, email=?, "
                + "especialidad=?, numero_licencia=? WHERE cedula=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, v.getNombre());
            ps.setString(2, v.getApellido());
            ps.setString(3, v.getTelefono());
            ps.setString(4, v.getEmail());
            ps.setString(5, v.getEspecialidad());
            ps.setString(6, v.getNumeroLicencia());
            ps.setString(7, v.getCedula());
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
        String sql = "SELECT * FROM VETERINARIO ORDER BY apellido, nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Veterinario> buscarPorNombre(String texto) throws SQLException {
        ArrayList<Veterinario> lista = new ArrayList<>();
        String filtro = "%" + texto.toUpperCase() + "%";
        String sql = "SELECT * FROM VETERINARIO WHERE UPPER(nombre) LIKE ? OR UPPER(apellido) LIKE ? "
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

    private Veterinario mapear(ResultSet rs) throws SQLException {
        return new Veterinario(
                rs.getString("cedula"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("especialidad"),
                rs.getString("numero_licencia")
        );
    }
}
