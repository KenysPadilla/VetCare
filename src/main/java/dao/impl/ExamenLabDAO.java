package dao.impl;

import dao.IDAO;
import model.Consulta;
import model.ExamenLab;
import model.Paciente;
import model.Propietario;
import model.Veterinario;
import util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;

public class ExamenLabDAO implements IDAO<ExamenLab> {

    private Connection conexion;

    public ExamenLabDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(ExamenLab examenLab) throws SQLException {
        String sql = "INSERT INTO EXAMEN_LAB (id_paciente, cedula_veterinario, id_consulta, "
                   + "fecha_hora, tipo_examen, resultado, observaciones, costo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, examenLab.getPaciente().getId());
            ps.setString(2, examenLab.getVeterinario().getCedula());
            if (examenLab.getConsulta() != null) {
                ps.setInt(3, examenLab.getConsulta().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setTimestamp(4, examenLab.getFechaHora() != null ? java.sql.Timestamp.valueOf(examenLab.getFechaHora()) : null);
            ps.setString(5, examenLab.getTipoExamen());
            ps.setString(6, examenLab.getResultado());
            ps.setString(7, examenLab.getObservaciones());
            ps.setDouble(8, examenLab.getCosto());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(ExamenLab examenLab) throws SQLException {
        String sql = "UPDATE EXAMEN_LAB SET id_paciente=?, cedula_veterinario=?, id_consulta=?, "
                   + "fecha_hora=?, tipo_examen=?, resultado=?, observaciones=?, costo=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, examenLab.getPaciente().getId());
            ps.setString(2, examenLab.getVeterinario().getCedula());
            if (examenLab.getConsulta() != null) {
                ps.setInt(3, examenLab.getConsulta().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setTimestamp(4, examenLab.getFechaHora() != null ? java.sql.Timestamp.valueOf(examenLab.getFechaHora()) : null);
            ps.setString(5, examenLab.getTipoExamen());
            ps.setString(6, examenLab.getResultado());
            ps.setString(7, examenLab.getObservaciones());
            ps.setDouble(8, examenLab.getCosto());
            ps.setInt(9, examenLab.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM EXAMEN_LAB WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public ExamenLab buscarPorId(int id) throws SQLException {
        String sql = "SELECT E.*,"
                   + "       P.nombre       AS pac_nombre,"
                   + "       P.especie      AS pac_especie,"
                   + "       PR.nombre      AS prop_nombre,"
                   + "       PR.apellido    AS prop_apellido,"
                   + "       V.nombre       AS vet_nombre,"
                   + "       V.apellido     AS vet_apellido,"
                   + "       V.especialidad AS vet_especialidad"
                   + " FROM EXAMEN_LAB E"
                   + " LEFT JOIN PACIENTE P     ON E.id_paciente        = P.id"
                   + " LEFT JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula"
                   + " LEFT JOIN VETERINARIO V  ON E.cedula_veterinario = V.cedula"
                   + " WHERE E.id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public ArrayList<ExamenLab> listarTodos() throws SQLException {
        ArrayList<ExamenLab> lista = new ArrayList<>();
        String sql = "SELECT E.*,"
                   + "       P.nombre       AS pac_nombre,"
                   + "       P.especie      AS pac_especie,"
                   + "       PR.nombre      AS prop_nombre,"
                   + "       PR.apellido    AS prop_apellido,"
                   + "       V.nombre       AS vet_nombre,"
                   + "       V.apellido     AS vet_apellido,"
                   + "       V.especialidad AS vet_especialidad"
                   + " FROM EXAMEN_LAB E"
                   + " LEFT JOIN PACIENTE P     ON E.id_paciente        = P.id"
                   + " LEFT JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula"
                   + " LEFT JOIN VETERINARIO V  ON E.cedula_veterinario = V.cedula"
                   + " ORDER BY E.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public ArrayList<ExamenLab> listarPorPaciente(int idPaciente) throws SQLException {
        ArrayList<ExamenLab> lista = new ArrayList<>();
        String sql = "SELECT E.*,"
                   + "       P.nombre       AS pac_nombre,"
                   + "       P.especie      AS pac_especie,"
                   + "       PR.nombre      AS prop_nombre,"
                   + "       PR.apellido    AS prop_apellido,"
                   + "       V.nombre       AS vet_nombre,"
                   + "       V.apellido     AS vet_apellido,"
                   + "       V.especialidad AS vet_especialidad"
                   + " FROM EXAMEN_LAB E"
                   + " LEFT JOIN PACIENTE P     ON E.id_paciente        = P.id"
                   + " LEFT JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula"
                   + " LEFT JOIN VETERINARIO V  ON E.cedula_veterinario = V.cedula"
                   + " WHERE E.id_paciente = ?"
                   + " ORDER BY E.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private ExamenLab mapear(ResultSet rs) throws SQLException {
        ExamenLab examenLab = new ExamenLab();
        examenLab.setId(rs.getInt("id"));

        Paciente pac = new Paciente();
        pac.setId(rs.getInt("id_paciente"));
        pac.setNombre(rs.getString("pac_nombre"));
        pac.setEspecie(rs.getString("pac_especie"));

        Propietario prop = new Propietario();
        prop.setNombre(rs.getString("prop_nombre"));
        prop.setApellido(rs.getString("prop_apellido"));
        pac.setPropietario(prop);

        examenLab.setPaciente(pac);

        Veterinario vet = new Veterinario();
        vet.setCedula(rs.getString("cedula_veterinario"));
        vet.setNombre(rs.getString("vet_nombre"));
        vet.setApellido(rs.getString("vet_apellido"));
        vet.setEspecialidad(rs.getString("vet_especialidad"));
        examenLab.setVeterinario(vet);

        int idCon = rs.getInt("id_consulta");
        if (!rs.wasNull()) {
            Consulta con = new Consulta();
            con.setId(idCon);
            examenLab.setConsulta(con);
        }

        java.sql.Timestamp fechaHora = rs.getTimestamp("fecha_hora");
        examenLab.setFechaHora(fechaHora != null ? fechaHora.toLocalDateTime() : null);

        examenLab.setTipoExamen(rs.getString("tipo_examen"));
        examenLab.setResultado(rs.getString("resultado"));
        examenLab.setObservaciones(rs.getString("observaciones"));
        examenLab.setCosto(rs.getDouble("costo"));
        return examenLab;
    }
}
