package dao.impl;

import dao.IDAO;
import model.DetalleFactura;
import model.Factura;
import util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;

public class DetalleFacturaDAO implements IDAO<DetalleFactura> {

    private Connection conexion;

    public DetalleFacturaDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    private void refrescarConexion() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(DetalleFactura detalleFactura) throws SQLException {
        refrescarConexion();
        String sql = "INSERT INTO DETALLE_FACTURA (id_factura, descripcion, tipo_concepto, "
                   + "cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (detalleFactura.getFactura() != null) {
                ps.setInt(1, detalleFactura.getFactura().getId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, detalleFactura.getDescripcion());
            ps.setString(3, detalleFactura.getTipoConcepto());
            ps.setInt(4, detalleFactura.getCantidad());
            ps.setDouble(5, detalleFactura.getPrecioUnitario());
            ps.setDouble(6, detalleFactura.getSubtotal());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(DetalleFactura detalleFactura) throws SQLException {
        refrescarConexion();
        String sql = "UPDATE DETALLE_FACTURA SET id_factura=?, descripcion=?, tipo_concepto=?, "
                   + "cantidad=?, precio_unitario=?, subtotal=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (detalleFactura.getFactura() != null) {
                ps.setInt(1, detalleFactura.getFactura().getId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, detalleFactura.getDescripcion());
            ps.setString(3, detalleFactura.getTipoConcepto());
            ps.setInt(4, detalleFactura.getCantidad());
            ps.setDouble(5, detalleFactura.getPrecioUnitario());
            ps.setDouble(6, detalleFactura.getSubtotal());
            ps.setInt(7, detalleFactura.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        refrescarConexion();
        String sql = "DELETE FROM DETALLE_FACTURA WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public DetalleFactura buscarPorId(int id) throws SQLException {
        refrescarConexion();
        String sql = "SELECT * FROM DETALLE_FACTURA WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public ArrayList<DetalleFactura> listarTodos() throws SQLException {
        refrescarConexion();
        ArrayList<DetalleFactura> lista = new ArrayList<>();
        String sql = "SELECT * FROM DETALLE_FACTURA";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public ArrayList<DetalleFactura> listarPorFactura(int idFactura) throws SQLException {
        refrescarConexion();
        ArrayList<DetalleFactura> lista = new ArrayList<>();
        String sql = "SELECT * FROM DETALLE_FACTURA WHERE id_factura=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private DetalleFactura mapear(ResultSet rs) throws SQLException {
        DetalleFactura detalleFactura = new DetalleFactura();
        detalleFactura.setId(rs.getInt("id"));

        int idFac = rs.getInt("id_factura");
        if (!rs.wasNull()) {
            Factura f = new Factura();
            f.setId(idFac);
            detalleFactura.setFactura(f);
        }

        detalleFactura.setDescripcion(rs.getString("descripcion"));
        detalleFactura.setTipoConcepto(rs.getString("tipo_concepto"));
        detalleFactura.setCantidad(rs.getInt("cantidad"));
        detalleFactura.setPrecioUnitario(rs.getDouble("precio_unitario"));
        detalleFactura.setSubtotal(rs.getDouble("subtotal"));
        return detalleFactura;
    }
}
