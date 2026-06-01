package dao.impl;

import model.InternacionMedicamento;
import model.Medicamento;
import util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class InternacionMedicamentoDAO {

    private static final String F = "|";
    private static final String R = "\n";

    private final Connection conexion;

    public InternacionMedicamentoDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    public void guardarTodos(int idInternacion, ArrayList<InternacionMedicamento> medicamentos)
            throws SQLException {
        double costoTotal = 0.0;
        StringBuilder stringBuilder = new StringBuilder();

        for (InternacionMedicamento internacionMedicamento : medicamentos) {
            if (internacionMedicamento.getMedicamento() == null) continue;
            Medicamento m = internacionMedicamento.getMedicamento();
            costoTotal += m.getPrecio() * internacionMedicamento.getCantidad();
            stringBuilder.append(m.getId()).append(F)
              .append(esc(m.getNombre())).append(F)
              .append(esc(m.getConcentracion())).append(F)
              .append(m.getPrecio()).append(F)
              .append(internacionMedicamento.getCantidad()).append(F)
              .append(esc(internacionMedicamento.getDosis())).append(F)
              .append(internacionMedicamento.getFechaAplicacion() != null ? internacionMedicamento.getFechaAplicacion() : "")
              .append(R);
        }

        String detalle = stringBuilder.isEmpty() ? null : stringBuilder.toString().stripTrailing();

        String sql = "UPDATE INTERNACION SET medicamentos_detalle=?, costo_medicamentos=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, detalle);
            ps.setDouble(2, costoTotal);
            ps.setInt(3, idInternacion);
            ps.executeUpdate();
        }
    }

    public ArrayList<InternacionMedicamento> listarPorInternacion(int idInternacion)
            throws SQLException {
        ArrayList<InternacionMedicamento> lista = new ArrayList<>();
        String sql = "SELECT medicamentos_detalle FROM INTERNACION WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idInternacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String detalle = rs.getString("medicamentos_detalle");
                    if (detalle != null && !detalle.isBlank()) {
                        for (String linea : detalle.split(R, -1)) {
                            InternacionMedicamento internacionMedicamento = parsear(linea.trim());
                            if (internacionMedicamento != null) lista.add(internacionMedicamento);
                        }
                    }
                }
            }
        }
        return lista;
    }

    public void eliminarPorInternacion(int idInternacion) throws SQLException {
        String sql = "UPDATE INTERNACION SET medicamentos_detalle=NULL, costo_medicamentos=0 WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idInternacion);
            ps.executeUpdate();
        }
    }

    private InternacionMedicamento parsear(String linea) {
        if (linea == null || linea.isEmpty()) return null;
        String[] p = linea.split("\\|", -1);
        if (p.length < 7) return null;
        try {
            Medicamento m = new Medicamento();
            m.setId(Integer.parseInt(p[0].trim()));
            m.setNombre(p[1].trim());
            String conc = p[2].trim();
            m.setConcentracion(conc.isEmpty() ? null : conc);
            m.setPrecio(Double.parseDouble(p[3].trim()));

            InternacionMedicamento internacionMedicamento = new InternacionMedicamento();
            internacionMedicamento.setMedicamento(m);
            internacionMedicamento.setCantidad(Integer.parseInt(p[4].trim()));
            String dosis = p[5].trim();
            internacionMedicamento.setDosis(dosis.isEmpty() ? null : dosis);
            String fecha = p[6].trim();
            if (!fecha.isEmpty()) internacionMedicamento.setFechaAplicacion(LocalDate.parse(fecha));
            return internacionMedicamento;
        } catch (Exception e) {
            return null;
        }
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace(F, "-").replace(R, " ").trim();
    }
}
