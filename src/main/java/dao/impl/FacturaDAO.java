package dao.impl;

import dao.IDAO;
import model.Factura;
import model.Paciente;
import model.Propietario;
import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;

public class FacturaDAO implements IDAO<Factura> {

    private Connection conexion;

    public FacturaDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Factura factura) throws SQLException {
        String sql = "INSERT INTO FACTURA (cedula_propietario, id_paciente, fecha_hora, "
                + "subtotal, total, estado_factura, metodo_pago) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, new String[]{"id"})) {
            ps.setString(1, factura.getPropietario().getCedula());
            ps.setInt(2, factura.getPaciente().getId());
            ps.setTimestamp(3, factura.getFechaHora() != null ? java.sql.Timestamp.valueOf(factura.getFechaHora()) : null);
            ps.setDouble(4, factura.getSubtotal());
            ps.setDouble(5, factura.getTotal());
            ps.setString(6, factura.getEstadoFactura() != null ? factura.getEstadoFactura() : "PENDIENTE");
            ps.setString(7, factura.getMetodoPago());
            ps.executeUpdate();

            try (ResultSet rsKeys = ps.getGeneratedKeys()) {
                if (rsKeys.next()) {
                    factura.setId(rsKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Factura factura) throws SQLException {
        String sql = "UPDATE FACTURA SET cedula_propietario=?, id_paciente=?, fecha_hora=?, "
                + "subtotal=?, total=?, estado_factura=?, metodo_pago=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, factura.getPropietario().getCedula());
            ps.setInt(2, factura.getPaciente().getId());
            ps.setTimestamp(3, factura.getFechaHora() != null ? java.sql.Timestamp.valueOf(factura.getFechaHora()) : null);
            ps.setDouble(4, factura.getSubtotal());
            ps.setDouble(5, factura.getTotal());
            ps.setString(6, factura.getEstadoFactura());
            ps.setString(7, factura.getMetodoPago());
            ps.setInt(8, factura.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM FACTURA WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private static final String SELECT_CON_JOIN
            = "SELECT f.id, f.cedula_propietario, f.id_paciente, f.fecha_hora, "
            + "       f.subtotal, f.total, f.estado_factura, f.metodo_pago, "
            + "       pr.nombre AS prop_nombre, pr.apellido AS prop_apellido, "
            + "       pac.nombre AS pac_nombre "
            + "FROM FACTURA f "
            + "LEFT JOIN PROPIETARIO pr ON f.cedula_propietario = pr.cedula "
            + "LEFT JOIN PACIENTE    pac ON f.id_paciente       = pac.id ";

    @Override
    public Factura buscarPorId(int id) throws SQLException {
        String sql = SELECT_CON_JOIN + "WHERE f.id=?";
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
    public ArrayList<Factura> listarTodos() throws SQLException {
        ArrayList<Factura> lista = new ArrayList<>();
        String sql = SELECT_CON_JOIN + "ORDER BY f.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Factura> listarPorPropietario(String cedula) throws SQLException {
        ArrayList<Factura> lista = new ArrayList<>();
        String sql = SELECT_CON_JOIN + "WHERE f.cedula_propietario=? ORDER BY f.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public ArrayList<Factura> listarPorEstado(String estado) throws SQLException {
        ArrayList<Factura> lista = new ArrayList<>();
        String sql = SELECT_CON_JOIN + "WHERE f.estado_factura=? ORDER BY f.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public void actualizarEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE FACTURA SET estado_factura=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void actualizarTotales(int id, double subtotal, double total) throws SQLException {
        String sql = "UPDATE FACTURA SET subtotal=?, total=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDouble(1, subtotal);
            ps.setDouble(2, total);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public double calcularTotalConIva(double subtotal) throws SQLException {
        String sql = "{ ? = call fn_calcular_total_con_iva(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setDouble(2, subtotal);
            cs.execute();
            return cs.getDouble(1);
        }
    }

    public int generarFacturaRapida(String cedulaPropietario, Integer idPaciente, String descripcion, String tipoConcepto,
            double precio, String metodoPago) throws SQLException {
        String sql = "{ call sp_generar_factura_rapida(?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setString(1, cedulaPropietario);
            if (idPaciente != null) {
                cs.setInt(2, idPaciente);
            } else {
                cs.setNull(2, Types.INTEGER);
            }
            cs.setString(3, descripcion);
            cs.setString(4, tipoConcepto);
            cs.setDouble(5, precio);
            cs.setString(6, metodoPago);
            cs.registerOutParameter(7, Types.INTEGER);
            cs.execute();
            return cs.getInt(7);
        }
    }

    private Factura mapear(ResultSet rs) throws SQLException {
        Factura factura = new Factura();
        factura.setId(rs.getInt("id"));

        Propietario propietario = new Propietario();
        propietario.setCedula(rs.getString("cedula_propietario"));
        propietario.setNombre(rs.getString("prop_nombre"));
        propietario.setApellido(rs.getString("prop_apellido"));
        factura.setPropietario(propietario);

        Paciente paciente = new Paciente();
        paciente.setId(rs.getInt("id_paciente"));
        paciente.setNombre(rs.getString("pac_nombre"));
        factura.setPaciente(paciente);

        java.sql.Timestamp fechaHora = rs.getTimestamp("fecha_hora");
        factura.setFechaHora(fechaHora != null ? fechaHora.toLocalDateTime() : null);

        factura.setSubtotal(rs.getDouble("subtotal"));
        factura.setTotal(rs.getDouble("total"));
        factura.setEstadoFactura(rs.getString("estado_factura"));
        factura.setMetodoPago(rs.getString("metodo_pago"));
        return factura;
    }
}
