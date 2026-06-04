package dao.impl;

import dao.IDAO;
import model.Cirugia;
import model.Paciente;
import model.Propietario;
import model.Veterinario;
import util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;

public class CirugiaDAO implements IDAO<Cirugia> {

    private Connection conexion;

    public CirugiaDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Cirugia cirugia) throws SQLException {
        String sql = "INSERT INTO CIRUGIA (id_paciente, cedula_veterinario, fecha_hora, tipo_cirugia, "
                   + "anestesia, descripcion, resultado, costo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cirugia.getPaciente().getId());
            ps.setString(2, cirugia.getVeterinario().getCedula());
            ps.setTimestamp(3, cirugia.getFechaHora() != null ? java.sql.Timestamp.valueOf(cirugia.getFechaHora()) : null);
            ps.setString(4, cirugia.getTipoCirugia());
            ps.setString(5, cirugia.getAnestesia());
            ps.setString(6, cirugia.getDescripcion());
            ps.setString(7, cirugia.getResultado());
            ps.setDouble(8, cirugia.getCosto());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Cirugia cirugia) throws SQLException {
        String sql = "UPDATE CIRUGIA SET id_paciente=?, cedula_veterinario=?, fecha_hora=?, "
                   + "tipo_cirugia=?, anestesia=?, descripcion=?, resultado=?, costo=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cirugia.getPaciente().getId());
            ps.setString(2, cirugia.getVeterinario().getCedula());
            ps.setTimestamp(3, cirugia.getFechaHora() != null ? java.sql.Timestamp.valueOf(cirugia.getFechaHora()) : null);
            ps.setString(4, cirugia.getTipoCirugia());
            ps.setString(5, cirugia.getAnestesia());
            ps.setString(6, cirugia.getDescripcion());
            ps.setString(7, cirugia.getResultado());
            ps.setDouble(8, cirugia.getCosto());
            ps.setInt(9, cirugia.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM CIRUGIA WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private static final String SQL_JOIN =
        "SELECT C.*, "
        + "P.nombre AS pac_nombre, P.especie AS pac_especie, "
        + "PR.cedula AS prop_cedula, PR.nombre AS prop_nombre, PR.apellido AS prop_apellido, "
        + "V.nombre AS vet_nombre, V.apellido AS vet_apellido, V.especialidad AS vet_especialidad "
        + "FROM CIRUGIA C "
        + "JOIN PACIENTE P ON C.id_paciente = P.id "
        + "JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula "
        + "JOIN VETERINARIO V ON C.cedula_veterinario = V.cedula ";

    @Override
    public Cirugia buscarPorId(int id) throws SQLException {
        String sql = SQL_JOIN + "WHERE C.id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public ArrayList<Cirugia> listarTodos() throws SQLException {
        ArrayList<Cirugia> lista = new ArrayList<>();
        String sql = SQL_JOIN + "ORDER BY C.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public void actualizarResultado(int id, String resultado) throws SQLException {
        String sql = "UPDATE CIRUGIA SET resultado=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, resultado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public ArrayList<Cirugia> listarPorPaciente(int idPaciente) throws SQLException {
        ArrayList<Cirugia> lista = new ArrayList<>();
        String sql = SQL_JOIN + "WHERE C.id_paciente=? ORDER BY C.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Cirugia mapear(ResultSet rs) throws SQLException {
        Cirugia c = new Cirugia();
        c.setId(rs.getInt("id"));

        Propietario prop = new Propietario();
        prop.setCedula(rs.getString("prop_cedula"));
        prop.setNombre(rs.getString("prop_nombre"));
        prop.setApellido(rs.getString("prop_apellido"));

        Paciente pac = new Paciente();
        pac.setId(rs.getInt("id_paciente"));
        pac.setNombre(rs.getString("pac_nombre"));
        pac.setEspecie(rs.getString("pac_especie"));
        pac.setPropietario(prop);
        c.setPaciente(pac);

        Veterinario vet = new Veterinario();
        vet.setCedula(rs.getString("cedula_veterinario"));
        vet.setNombre(rs.getString("vet_nombre"));
        vet.setApellido(rs.getString("vet_apellido"));
        vet.setEspecialidad(rs.getString("vet_especialidad"));
        c.setVeterinario(vet);

        java.sql.Timestamp fechaHora = rs.getTimestamp("fecha_hora");
        c.setFechaHora(fechaHora != null ? fechaHora.toLocalDateTime() : null);

        c.setTipoCirugia(rs.getString("tipo_cirugia"));
        c.setAnestesia(rs.getString("anestesia"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setResultado(rs.getString("resultado"));
        c.setDuracion(rs.getInt("duracion"));
        c.setCosto(rs.getDouble("costo"));
        try {
            String estadoCirugia = rs.getString("estado");
            c.setEstado(estadoCirugia != null ? estadoCirugia : "Programada");
            java.sql.Timestamp horaInicio = rs.getTimestamp("hora_inicio");
            c.setHoraInicio(horaInicio != null ? horaInicio.toLocalDateTime() : null);
            java.sql.Timestamp horaFin = rs.getTimestamp("hora_fin");
            c.setHoraFin(horaFin != null ? horaFin.toLocalDateTime() : null);
        } catch (SQLException ignorada) {
            c.setEstado("Programada");
        }
        return c;
    }

    public void iniciarCirugia(int id) throws SQLException {
        String sql = "UPDATE CIRUGIA SET estado=?, hora_inicio=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "En Curso");
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void finalizarCirugia(int id) throws SQLException {
        java.time.LocalDateTime horaInicio = null;
        String sqlSeleccion = "SELECT hora_inicio FROM CIRUGIA WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sqlSeleccion)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Timestamp ts = rs.getTimestamp("hora_inicio");
                    if (ts != null) horaInicio = ts.toLocalDateTime();
                }
            }
        }
        java.time.LocalDateTime horaFin = java.time.LocalDateTime.now();
        int duracion = horaInicio != null
                ? (int) java.time.Duration.between(horaInicio, horaFin).toMinutes()
                : 0;
        String sql = "UPDATE CIRUGIA SET estado=?, hora_fin=?, duracion=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "Finalizada");
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(horaFin));
            ps.setInt(3, duracion);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }
}
