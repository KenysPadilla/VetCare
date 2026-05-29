package dao.impl;

import dao.IDAO;
import model.Consulta;
import model.Internacion;
import model.Paciente;
import model.Propietario;
import model.Veterinario;
import util.ConexionBD;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class InternacionDAO implements IDAO<Internacion> {

    private Connection conexion;

    public InternacionDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Internacion internacion) throws SQLException {
        String sql = "INSERT INTO INTERNACION (id_paciente, cedula_veterinario, id_consulta, "
                + "fecha_hora_ingreso, fecha_hora_egreso, motivo, diagnostico, costo_diario, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, internacion.getPaciente().getId());
            ps.setString(2, internacion.getVeterinario().getCedula());
            if (internacion.getConsulta() != null) {
                ps.setInt(3, internacion.getConsulta().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setTimestamp(4, internacion.getFechaHoraIngreso() != null
                    ? java.sql.Timestamp.valueOf(internacion.getFechaHoraIngreso()) : null);
            if (internacion.getFechaHoraEgreso() != null) {
                ps.setTimestamp(5, java.sql.Timestamp.valueOf(internacion.getFechaHoraEgreso()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            ps.setString(6, internacion.getMotivo());
            ps.setString(7, internacion.getDiagnostico());
            ps.setDouble(8, internacion.getCostoDia());
            ps.setString(9, internacion.getObservaciones());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Internacion internacion) throws SQLException {
        String sql = "UPDATE INTERNACION SET id_paciente=?, cedula_veterinario=?, id_consulta=?, "
                + "fecha_hora_ingreso=?, fecha_hora_egreso=?, motivo=?, diagnostico=?, "
                + "costo_diario=?, observaciones=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, internacion.getPaciente().getId());
            ps.setString(2, internacion.getVeterinario().getCedula());
            if (internacion.getConsulta() != null) {
                ps.setInt(3, internacion.getConsulta().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setTimestamp(4, internacion.getFechaHoraIngreso() != null
                    ? java.sql.Timestamp.valueOf(internacion.getFechaHoraIngreso()) : null);
            if (internacion.getFechaHoraEgreso() != null) {
                ps.setTimestamp(5, java.sql.Timestamp.valueOf(internacion.getFechaHoraEgreso()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            ps.setString(6, internacion.getMotivo());
            ps.setString(7, internacion.getDiagnostico());
            ps.setDouble(8, internacion.getCostoDia());
            ps.setString(9, internacion.getObservaciones());
            ps.setInt(10, internacion.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM INTERNACION WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Internacion buscarPorId(int id) throws SQLException {
        String sql = "SELECT I.*,"
                + "       P.nombre       AS pac_nombre,"
                + "       P.especie      AS pac_especie,"
                + "       PR.nombre      AS prop_nombre,"
                + "       PR.apellido    AS prop_apellido,"
                + "       V.nombre       AS vet_nombre,"
                + "       V.apellido     AS vet_apellido,"
                + "       V.especialidad AS vet_especialidad"
                + " FROM INTERNACION I"
                + " LEFT JOIN PACIENTE P     ON I.id_paciente        = P.id"
                + " LEFT JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula"
                + " LEFT JOIN VETERINARIO V  ON I.cedula_veterinario = V.cedula"
                + " WHERE I.id = ?";
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
    public ArrayList<Internacion> listarTodos() throws SQLException {
        ArrayList<Internacion> lista = new ArrayList<>();
        String sql = "SELECT I.*,"
                + "       P.nombre       AS pac_nombre,"
                + "       P.especie      AS pac_especie,"
                + "       PR.nombre      AS prop_nombre,"
                + "       PR.apellido    AS prop_apellido,"
                + "       V.nombre       AS vet_nombre,"
                + "       V.apellido     AS vet_apellido,"
                + "       V.especialidad AS vet_especialidad"
                + " FROM INTERNACION I"
                + " LEFT JOIN PACIENTE P     ON I.id_paciente        = P.id"
                + " LEFT JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula"
                + " LEFT JOIN VETERINARIO V  ON I.cedula_veterinario = V.cedula"
                + " ORDER BY I.fecha_hora_ingreso DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Internacion> listarPorPaciente(int idPaciente) throws SQLException {
        ArrayList<Internacion> lista = new ArrayList<>();
        String sql = "SELECT I.*,"
                + "       P.nombre       AS pac_nombre,"
                + "       P.especie      AS pac_especie,"
                + "       PR.nombre      AS prop_nombre,"
                + "       PR.apellido    AS prop_apellido,"
                + "       V.nombre       AS vet_nombre,"
                + "       V.apellido     AS vet_apellido,"
                + "       V.especialidad AS vet_especialidad"
                + " FROM INTERNACION I"
                + " LEFT JOIN PACIENTE P     ON I.id_paciente        = P.id"
                + " LEFT JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula"
                + " LEFT JOIN VETERINARIO V  ON I.cedula_veterinario = V.cedula"
                + " WHERE I.id_paciente = ?"
                + " ORDER BY I.fecha_hora_ingreso DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public void actualizarEgreso(int id, LocalDateTime fecha) throws SQLException {
        String sql = "{ call sp_dar_egreso_internacion(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.setTimestamp(2, java.sql.Timestamp.valueOf(fecha));
            cs.execute();
        }
    }

    public double calcularCostoTotal(int idInternacion) throws SQLException {
        String sql = "{ ? = call fn_costo_internacion(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setInt(2, idInternacion);
            cs.execute();
            return cs.getDouble(1);
        }
    }

    private Internacion mapear(ResultSet rs) throws SQLException {
        Internacion internacion = new Internacion();
        internacion.setId(rs.getInt("id"));

        Paciente paciente = new Paciente();
        paciente.setId(rs.getInt("id_paciente"));
        paciente.setNombre(rs.getString("pac_nombre"));
        paciente.setEspecie(rs.getString("pac_especie"));

        Propietario propietario = new Propietario();
        propietario.setNombre(rs.getString("prop_nombre"));
        propietario.setApellido(rs.getString("prop_apellido"));
        paciente.setPropietario(propietario);

        internacion.setPaciente(paciente);

        Veterinario veterinario = new Veterinario();
        veterinario.setCedula(rs.getString("cedula_veterinario"));
        veterinario.setNombre(rs.getString("vet_nombre"));
        veterinario.setApellido(rs.getString("vet_apellido"));
        veterinario.setEspecialidad(rs.getString("vet_especialidad"));
        internacion.setVeterinario(veterinario);

        int idConsulta = rs.getInt("id_consulta");
        if (!rs.wasNull()) {
            Consulta con = new Consulta();
            con.setId(idConsulta);
            internacion.setConsulta(con);
        }

        java.sql.Timestamp ingreso = rs.getTimestamp("fecha_hora_ingreso");
        internacion.setFechaHoraIngreso(ingreso != null ? ingreso.toLocalDateTime() : null);

        java.sql.Timestamp egreso = rs.getTimestamp("fecha_hora_egreso");
        internacion.setFechaHoraEgreso(egreso != null ? egreso.toLocalDateTime() : null);

        internacion.setMotivo(rs.getString("motivo"));
        internacion.setDiagnostico(rs.getString("diagnostico"));
        internacion.setCostoDia(rs.getDouble("costo_diario"));
        internacion.setObservaciones(rs.getString("observaciones"));
        return internacion;
    }
}
