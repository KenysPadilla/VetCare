package dao.impl;

import dao.IDAO;
import model.Vacuna;
import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;

public class VacunaDAO implements IDAO<Vacuna> {

    private Connection conexion;

    public VacunaDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Vacuna vacuna) throws SQLException {
        String sql = "INSERT INTO VACUNA (nombre, laboratorio, lote, precio, "
                + "stock_disponible, fecha_vencimiento) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, vacuna.getNombre());
            ps.setString(2, vacuna.getLaboratorio());
            ps.setString(3, vacuna.getLote());
            ps.setDouble(4, vacuna.getPrecio());
            ps.setInt(5, vacuna.getStockDisponible());
            if (vacuna.getFechaVencimiento() != null) {
                ps.setDate(6, java.sql.Date.valueOf(vacuna.getFechaVencimiento()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Vacuna vacuna) throws SQLException {
        String sql = "UPDATE VACUNA SET nombre=?, laboratorio=?, lote=?, precio=?, "
                + "stock_disponible=?, fecha_vencimiento=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, vacuna.getNombre());
            ps.setString(2, vacuna.getLaboratorio());
            ps.setString(3, vacuna.getLote());
            ps.setDouble(4, vacuna.getPrecio());
            ps.setInt(5, vacuna.getStockDisponible());
            if (vacuna.getFechaVencimiento() != null) {
                ps.setDate(6, java.sql.Date.valueOf(vacuna.getFechaVencimiento()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.setInt(7, vacuna.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM VACUNA WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Vacuna buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM VACUNA WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    @Override
    public ArrayList<Vacuna> listarTodos() throws SQLException {
        ArrayList<Vacuna> lista = new ArrayList<>();
        String sql = "SELECT * FROM VACUNA ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Vacuna> buscarPorNombre(String texto) throws SQLException {
        ArrayList<Vacuna> lista = new ArrayList<>();
        String filtro = "%" + texto.toUpperCase() + "%";
        String sql = "SELECT * FROM VACUNA WHERE UPPER(nombre) LIKE ? ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    private Vacuna mapear(ResultSet rs) throws SQLException {
        Vacuna vacuna = new Vacuna();
        vacuna.setId(rs.getInt("id"));
        vacuna.setNombre(rs.getString("nombre"));
        vacuna.setLaboratorio(rs.getString("laboratorio"));
        vacuna.setLote(rs.getString("lote"));
        vacuna.setPrecio(rs.getDouble("precio"));
        vacuna.setStockDisponible(rs.getInt("stock_disponible"));
        java.sql.Date fecha = rs.getDate("fecha_vencimiento");
        vacuna.setFechaVencimiento(fecha != null ? fecha.toLocalDate() : null);
        return vacuna;
    }
}
