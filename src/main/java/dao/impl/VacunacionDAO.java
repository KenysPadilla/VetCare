package dao.impl;

import dao.IDAO;
import model.Paciente;
import model.Propietario;
import model.Vacuna;
import model.Vacunacion;
import model.Veterinario;
import util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;

public class VacunacionDAO implements IDAO<Vacunacion> {

    private Connection conexion;

    public VacunacionDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    private void refrescarConexion() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Vacunacion vacunacion) throws SQLException {
        refrescarConexion();
        String sql = "INSERT INTO VACUNACION (id_paciente, cedula_veterinario, id_vacuna, "
                   + "fecha_hora, proxima_fecha, observaciones) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, vacunacion.getPaciente().getId());
            ps.setString(2, vacunacion.getVeterinario().getCedula());
            ps.setInt(3, vacunacion.getVacuna().getId());
            ps.setTimestamp(4, vacunacion.getFechaHoraAplicacion() != null
                    ? java.sql.Timestamp.valueOf(vacunacion.getFechaHoraAplicacion()) : null);
            if (vacunacion.getFechaProxima() != null) {
                ps.setDate(5, java.sql.Date.valueOf(vacunacion.getFechaProxima()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setString(6, vacunacion.getObservaciones());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Vacunacion vacunacion) throws SQLException {
        refrescarConexion();
        String sql = "UPDATE VACUNACION SET id_paciente=?, cedula_veterinario=?, id_vacuna=?, "
                   + "fecha_hora=?, proxima_fecha=?, observaciones=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, vacunacion.getPaciente().getId());
            ps.setString(2, vacunacion.getVeterinario().getCedula());
            ps.setInt(3, vacunacion.getVacuna().getId());
            ps.setTimestamp(4, vacunacion.getFechaHoraAplicacion() != null
                    ? java.sql.Timestamp.valueOf(vacunacion.getFechaHoraAplicacion()) : null);
            if (vacunacion.getFechaProxima() != null) {
                ps.setDate(5, java.sql.Date.valueOf(vacunacion.getFechaProxima()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setString(6, vacunacion.getObservaciones());
            ps.setInt(7, vacunacion.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        refrescarConexion();
        String sql = "DELETE FROM VACUNACION WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Vacunacion buscarPorId(int id) throws SQLException {
        refrescarConexion();
        String sql = SQL_JOIN + "WHERE V.id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    private static final String SQL_JOIN =
        "SELECT V.*, "
        + "P.nombre AS pac_nombre, P.especie AS pac_especie, "
        + "PR.cedula AS prop_cedula, PR.nombre AS prop_nombre, PR.apellido AS prop_apellido, "
        + "VT.nombre AS vet_nombre, VT.apellido AS vet_apellido, "
        + "VA.nombre AS vac_nombre, VA.laboratorio AS vac_laboratorio, VA.precio AS vac_precio "
        + "FROM VACUNACION V "
        + "JOIN PACIENTE P ON V.id_paciente = P.id "
        + "JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula "
        + "JOIN VETERINARIO VT ON V.cedula_veterinario = VT.cedula "
        + "JOIN VACUNA VA ON V.id_vacuna = VA.id ";

    @Override
    public ArrayList<Vacunacion> listarTodos() throws SQLException {
        refrescarConexion();
        ArrayList<Vacunacion> lista = new ArrayList<>();
        String sql = SQL_JOIN + "ORDER BY V.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public ArrayList<Vacunacion> listarPorPaciente(int idPaciente) throws SQLException {
        refrescarConexion();
        ArrayList<Vacunacion> lista = new ArrayList<>();
        String sql = SQL_JOIN + "WHERE V.id_paciente=? ORDER BY V.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void descontarStock(int idVacuna) throws SQLException {
        String sql = "{ call sp_descontar_stock_vacuna(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idVacuna);
            cs.setInt(2, 1);
            cs.execute();
        }
    }

    private Vacunacion mapear(ResultSet rs) throws SQLException {
        Vacunacion vacunacion = new Vacunacion();
        vacunacion.setId(rs.getInt("id"));

        Propietario prop = new Propietario();
        prop.setCedula(rs.getString("prop_cedula"));
        prop.setNombre(rs.getString("prop_nombre"));
        prop.setApellido(rs.getString("prop_apellido"));

        Paciente pac = new Paciente();
        pac.setId(rs.getInt("id_paciente"));
        pac.setNombre(rs.getString("pac_nombre"));
        pac.setEspecie(rs.getString("pac_especie"));
        pac.setPropietario(prop);
        vacunacion.setPaciente(pac);

        Veterinario vet = new Veterinario();
        vet.setCedula(rs.getString("cedula_veterinario"));
        vet.setNombre(rs.getString("vet_nombre"));
        vet.setApellido(rs.getString("vet_apellido"));
        vacunacion.setVeterinario(vet);

        Vacuna vacuna = new Vacuna();
        vacuna.setId(rs.getInt("id_vacuna"));
        vacuna.setNombre(rs.getString("vac_nombre"));
        vacuna.setLaboratorio(rs.getString("vac_laboratorio"));
        vacuna.setPrecio(rs.getDouble("vac_precio"));
        vacunacion.setVacuna(vacuna);

        java.sql.Timestamp fechaHoraApl = rs.getTimestamp("fecha_hora");
        vacunacion.setFechaHoraAplicacion(fechaHoraApl != null ? fechaHoraApl.toLocalDateTime() : null);

        java.sql.Date proxima = rs.getDate("proxima_fecha");
        vacunacion.setFechaProxima(proxima != null ? proxima.toLocalDate() : null);

        vacunacion.setObservaciones(rs.getString("observaciones"));
        return vacunacion;
    }
}
