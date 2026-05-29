package dao.impl;

import dao.IDAO;
import model.Medicamento;
import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;

public class MedicamentoDAO implements IDAO<Medicamento> {

    private Connection conexion;

    public MedicamentoDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Medicamento medicamento) throws SQLException {
        String sql = "INSERT INTO MEDICAMENTO (nombre, descripcion, fabricante, precio, "
                + "stock_disponible, fecha_vencimiento, concentracion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, medicamento.getNombre());
            ps.setString(2, medicamento.getDescripcion());
            ps.setString(3, medicamento.getFabricante());
            ps.setDouble(4, medicamento.getPrecio());
            ps.setInt(5, medicamento.getStockDisponible());
            if (medicamento.getFechaVencimiento() != null) {
                ps.setDate(6, java.sql.Date.valueOf(medicamento.getFechaVencimiento()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.setString(7, medicamento.getConcentracion());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Medicamento medicamento) throws SQLException {
        String sql = "UPDATE MEDICAMENTO SET nombre=?, descripcion=?, fabricante=?, precio=?, "
                + "stock_disponible=?, fecha_vencimiento=?, concentracion=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, medicamento.getNombre());
            ps.setString(2, medicamento.getDescripcion());
            ps.setString(3, medicamento.getFabricante());
            ps.setDouble(4, medicamento.getPrecio());
            ps.setInt(5, medicamento.getStockDisponible());
            if (medicamento.getFechaVencimiento() != null) {
                ps.setDate(6, java.sql.Date.valueOf(medicamento.getFechaVencimiento()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.setString(7, medicamento.getConcentracion());
            ps.setInt(8, medicamento.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM MEDICAMENTO WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Medicamento buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM MEDICAMENTO WHERE id=?";
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
    public ArrayList<Medicamento> listarTodos() throws SQLException {
        ArrayList<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM MEDICAMENTO ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Medicamento> buscarPorNombre(String texto) throws SQLException {
        ArrayList<Medicamento> lista = new ArrayList<>();
        String filtro = "%" + texto.toUpperCase() + "%";
        String sql = "SELECT * FROM MEDICAMENTO WHERE UPPER(nombre) LIKE ? ORDER BY nombre";
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

    private Medicamento mapear(ResultSet rs) throws SQLException {
        Medicamento medicamento = new Medicamento();
        medicamento.setId(rs.getInt("id"));
        medicamento.setNombre(rs.getString("nombre"));
        medicamento.setDescripcion(rs.getString("descripcion"));
        medicamento.setFabricante(rs.getString("fabricante"));
        medicamento.setPrecio(rs.getDouble("precio"));
        medicamento.setStockDisponible(rs.getInt("stock_disponible"));
        java.sql.Date fecha = rs.getDate("fecha_vencimiento");
        medicamento.setFechaVencimiento(fecha != null ? fecha.toLocalDate() : null);
        medicamento.setConcentracion(rs.getString("concentracion"));
        return medicamento;
    }
}
