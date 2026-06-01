package dao.impl;

import dao.IDAO;
import model.Estilista;
import model.Paciente;
import model.Propietario;
import model.ServicioEstetico;
import util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;

public class ServicioEsteticoDAO implements IDAO<ServicioEstetico> {

    private Connection conexion;

    public ServicioEsteticoDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    private void refrescarConexion() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(ServicioEstetico servicioEstetico) throws SQLException {
        refrescarConexion();
        String sql = "INSERT INTO SERVICIO_ESTETICO (tipo_servicio, id_paciente, cedula_estilista, "
                   + "fecha_hora, precio, estado_servicio, observaciones, tipo_bano, "
                   + "incluye_secado, incluye_perfume, estilo_corte, largo_corte, "
                   + "incluye_unas, incluye_limpieza) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, servicioEstetico.getTipoServicio());
            ps.setInt(2, servicioEstetico.getPaciente().getId());
            ps.setString(3, servicioEstetico.getEstilista().getCedula());
            ps.setTimestamp(4, servicioEstetico.getFechaHora() != null ? java.sql.Timestamp.valueOf(servicioEstetico.getFechaHora()) : null);
            ps.setDouble(5, servicioEstetico.getPrecio());
            ps.setString(6, servicioEstetico.getEstadoServicio() != null ? servicioEstetico.getEstadoServicio() : "PROGRAMADO");
            ps.setString(7, servicioEstetico.getObservaciones());

            if ("MOTILADA".equals(servicioEstetico.getTipoServicio())) {
                ps.setNull(8,  Types.VARCHAR);
                ps.setNull(9,  Types.INTEGER);
                ps.setNull(10, Types.INTEGER);
            } else {
                ps.setString(8, servicioEstetico.getTipoBano());
                ps.setInt(9,  servicioEstetico.isIncluyeSecado()  ? 1 : 0);
                ps.setInt(10, servicioEstetico.isIncluyePerfume() ? 1 : 0);
            }

            if ("BANO".equals(servicioEstetico.getTipoServicio())) {
                ps.setNull(11, Types.VARCHAR);
                ps.setNull(12, Types.VARCHAR);
                ps.setNull(13, Types.INTEGER);
                ps.setNull(14, Types.INTEGER);
            } else {
                ps.setString(11, servicioEstetico.getEstiloCorte());
                ps.setString(12, servicioEstetico.getLargoCorte());
                ps.setInt(13, servicioEstetico.isIncluyeUnas()     ? 1 : 0);
                ps.setInt(14, servicioEstetico.isIncluyeLimpieza() ? 1 : 0);
            }
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(ServicioEstetico servicioEstetico) throws SQLException {
        refrescarConexion();
        String sql = "UPDATE SERVICIO_ESTETICO SET tipo_servicio=?, id_paciente=?, cedula_estilista=?, "
                   + "fecha_hora=?, precio=?, estado_servicio=?, observaciones=?, tipo_bano=?, "
                   + "incluye_secado=?, incluye_perfume=?, estilo_corte=?, largo_corte=?, "
                   + "incluye_unas=?, incluye_limpieza=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, servicioEstetico.getTipoServicio());
            ps.setInt(2, servicioEstetico.getPaciente().getId());
            ps.setString(3, servicioEstetico.getEstilista().getCedula());
            ps.setTimestamp(4, servicioEstetico.getFechaHora() != null ? java.sql.Timestamp.valueOf(servicioEstetico.getFechaHora()) : null);
            ps.setDouble(5, servicioEstetico.getPrecio());
            ps.setString(6, servicioEstetico.getEstadoServicio());
            ps.setString(7, servicioEstetico.getObservaciones());

            if ("MOTILADA".equals(servicioEstetico.getTipoServicio())) {
                ps.setNull(8,  Types.VARCHAR);
                ps.setNull(9,  Types.INTEGER);
                ps.setNull(10, Types.INTEGER);
            } else {
                ps.setString(8, servicioEstetico.getTipoBano());
                ps.setInt(9,  servicioEstetico.isIncluyeSecado()  ? 1 : 0);
                ps.setInt(10, servicioEstetico.isIncluyePerfume() ? 1 : 0);
            }

            if ("BANO".equals(servicioEstetico.getTipoServicio())) {
                ps.setNull(11, Types.VARCHAR);
                ps.setNull(12, Types.VARCHAR);
                ps.setNull(13, Types.INTEGER);
                ps.setNull(14, Types.INTEGER);
            } else {
                ps.setString(11, servicioEstetico.getEstiloCorte());
                ps.setString(12, servicioEstetico.getLargoCorte());
                ps.setInt(13, servicioEstetico.isIncluyeUnas()     ? 1 : 0);
                ps.setInt(14, servicioEstetico.isIncluyeLimpieza() ? 1 : 0);
            }
            ps.setInt(15, servicioEstetico.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        refrescarConexion();
        String sql = "DELETE FROM SERVICIO_ESTETICO WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public ServicioEstetico buscarPorId(int id) throws SQLException {
        refrescarConexion();
        String sql = SQL_JOIN + "WHERE SE.id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    private static final String SQL_JOIN =
        "SELECT SE.*, P.nombre AS pac_nombre, P.especie AS pac_especie, "
        + "PR.cedula AS prop_cedula, PR.nombre AS prop_nombre, PR.apellido AS prop_apellido, "
        + "E.nombre AS est_nombre, E.apellido AS est_apellido "
        + "FROM SERVICIO_ESTETICO SE "
        + "LEFT JOIN PACIENTE P  ON SE.id_paciente       = P.id "
        + "LEFT JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula "
        + "LEFT JOIN ESTILISTA E  ON SE.cedula_estilista  = E.cedula ";

    @Override
    public ArrayList<ServicioEstetico> listarTodos() throws SQLException {
        refrescarConexion();
        ArrayList<ServicioEstetico> lista = new ArrayList<>();
        String sql = SQL_JOIN + "ORDER BY SE.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public ArrayList<ServicioEstetico> listarPorPaciente(int idPaciente) throws SQLException {
        refrescarConexion();
        ArrayList<ServicioEstetico> lista = new ArrayList<>();
        String sql = SQL_JOIN + "WHERE SE.id_paciente=? ORDER BY SE.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<ServicioEstetico> listarPorTipo(String tipo) throws SQLException {
        refrescarConexion();
        ArrayList<ServicioEstetico> lista = new ArrayList<>();
        String sql = SQL_JOIN + "WHERE SE.tipo_servicio=? ORDER BY SE.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void actualizarEstado(int id, String estado) throws SQLException {
        refrescarConexion();
        String sql = "UPDATE SERVICIO_ESTETICO SET estado_servicio=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private ServicioEstetico mapear(ResultSet rs) throws SQLException {
        ServicioEstetico servicioEstetico = new ServicioEstetico();
        servicioEstetico.setId(rs.getInt("id"));
        servicioEstetico.setTipoServicio(rs.getString("tipo_servicio"));

        Propietario prop = new Propietario();
        prop.setCedula(rs.getString("prop_cedula"));
        prop.setNombre(rs.getString("prop_nombre"));
        prop.setApellido(rs.getString("prop_apellido"));

        Paciente pac = new Paciente();
        pac.setId(rs.getInt("id_paciente"));
        String pacNombre = rs.getString("pac_nombre");
        if (pacNombre != null) pac.setNombre(pacNombre);
        String pacEspecie = rs.getString("pac_especie");
        if (pacEspecie != null) pac.setEspecie(pacEspecie);
        pac.setPropietario(prop);
        servicioEstetico.setPaciente(pac);

        Estilista est = new Estilista();
        est.setCedula(rs.getString("cedula_estilista"));
        String estNombre = rs.getString("est_nombre");
        if (estNombre != null) est.setNombre(estNombre);
        String estApellido = rs.getString("est_apellido");
        if (estApellido != null) est.setApellido(estApellido);
        servicioEstetico.setEstilista(est);

        java.sql.Timestamp fechaHora = rs.getTimestamp("fecha_hora");
        servicioEstetico.setFechaHora(fechaHora != null ? fechaHora.toLocalDateTime() : null);
        servicioEstetico.setPrecio(rs.getDouble("precio"));
        servicioEstetico.setEstadoServicio(rs.getString("estado_servicio"));
        servicioEstetico.setObservaciones(rs.getString("observaciones"));

        servicioEstetico.setTipoBano(rs.getString("tipo_bano"));
        servicioEstetico.setIncluyeSecado(rs.getInt("incluye_secado")   == 1);
        servicioEstetico.setIncluyePerfume(rs.getInt("incluye_perfume") == 1);

        servicioEstetico.setEstiloCorte(rs.getString("estilo_corte"));
        servicioEstetico.setLargoCorte(rs.getString("largo_corte"));
        servicioEstetico.setIncluyeUnas(rs.getInt("incluye_unas")         == 1);
        servicioEstetico.setIncluyeLimpieza(rs.getInt("incluye_limpieza") == 1);

        return servicioEstetico;
    }
}
