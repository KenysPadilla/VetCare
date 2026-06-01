package dao.impl;

import model.SolicitudCita;
import util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;

public class SolicitudCitaDAO {

    private final Connection conexion;

    public SolicitudCitaDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    public void guardar(SolicitudCita sol) throws SQLException {
        String sql = "INSERT INTO SOLICITUD_CITA "
                   + "(nombre_propietario, telefono, correo, nombre_mascota, especie, raza, "
                   + " motivo, fecha, hora, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDIENTE')";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, sol.getNombrePropietario());
            ps.setString(2, sol.getTelefono());
            ps.setString(3, sol.getCorreo());
            ps.setString(4, sol.getNombreMascota());
            ps.setString(5, sol.getEspecie());
            ps.setString(6, sol.getRaza() != null ? sol.getRaza() : "Mestizo");
            ps.setString(7, sol.getMotivo());
            ps.setDate(8, sol.getFecha() != null ? Date.valueOf(sol.getFecha()) : null);
            ps.setString(9, sol.getHora());
            ps.executeUpdate();
        }
    }

    public ArrayList<SolicitudCita> listarPendientes() throws SQLException {
        ArrayList<SolicitudCita> lista = new ArrayList<>();
        String sql = "SELECT * FROM SOLICITUD_CITA WHERE estado = 'PENDIENTE' "
                   + "ORDER BY fecha_solicitud DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<SolicitudCita> listarTodas() throws SQLException {
        ArrayList<SolicitudCita> lista = new ArrayList<>();
        String sql = "SELECT * FROM SOLICITUD_CITA ORDER BY fecha_solicitud DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void actualizarEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE SOLICITUD_CITA SET estado = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void actualizarNombrePropietario(int id, String nombreCompleto) throws SQLException {
        String sql = "UPDATE SOLICITUD_CITA SET nombre_propietario = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreCompleto);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public int aceptarSolicitudChatbot(int idSolicitud, String cedulaVet) throws SQLException {
        String sql = "{ call sp_aceptar_solicitud_cita(?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idSolicitud);
            cs.setString(2, cedulaVet);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();
            return cs.getInt(3);
        }
    }

    private SolicitudCita mapear(ResultSet rs) throws SQLException {
        SolicitudCita s = new SolicitudCita();
        s.setId(rs.getInt("id"));
        s.setNombrePropietario(rs.getString("nombre_propietario"));
        s.setTelefono(rs.getString("telefono"));
        s.setCorreo(rs.getString("correo"));
        s.setNombreMascota(rs.getString("nombre_mascota"));
        s.setEspecie(rs.getString("especie"));
        s.setRaza(rs.getString("raza"));
        s.setMotivo(rs.getString("motivo"));

        java.sql.Date fechaSql = rs.getDate("fecha");
        s.setFecha(fechaSql != null ? fechaSql.toLocalDate() : null);

        s.setHora(rs.getString("hora"));
        s.setEstado(rs.getString("estado"));

        Timestamp ts = rs.getTimestamp("fecha_solicitud");
        s.setFechaSolicitud(ts != null ? ts.toLocalDateTime() : null);

        return s;
    }
}
