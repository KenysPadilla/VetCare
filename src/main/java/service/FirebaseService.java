package service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import dao.impl.SolicitudCitaDAO;
import model.SolicitudCita;
import util.ConexionBD;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class FirebaseService {

    private static final Logger LOG = Logger.getLogger(FirebaseService.class.getName());

    private static volatile boolean inicializado = false;
    private static final Object LOCK = new Object();
    private final SolicitudCitaDAO solicitudDAO = new SolicitudCitaDAO();

    public void inicializar() {
        synchronized (LOCK) {
            if (inicializado) {
                return;
            }
            try (InputStream configIn = getClass().getResourceAsStream("/config.properties")) {
                Properties config = new Properties();
                config.load(configIn);

                String databaseUrl = config.getProperty("firebase.database.url");
                String credentialsPath = config.getProperty("firebase.credentials.path");

                InputStream credStream = resolverCredenciales(credentialsPath);
                if (credStream == null) {
                    LOG.warning("Firebase: credenciales no encontradas en '" + credentialsPath
                            + "'. Actualiza firebase.credentials.path en config.properties.");
                    return;
                }

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(credStream))
                            .setDatabaseUrl(databaseUrl)
                            .build();
                    FirebaseApp.initializeApp(options);
                }
                inicializado = true;
                LOG.info("Firebase inicializado — proyecto: " + databaseUrl);

            } catch (Exception e) {
                LOG.warning("Firebase: error al inicializar — " + e.getMessage());
            }
        }
    }

    public void publicarHorasOcupadas(LocalDate fecha) {
        if (!inicializado) {
            return;
        }
        try {
            List<String> horas = consultarHorasOcupadas(fecha);
            String clave = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE);
            DatabaseReference referencia = FirebaseDatabase.getInstance()
                    .getReference("horas_ocupadas/" + clave);
            referencia.setValueAsync(horas).get();
        } catch (Exception e) {
            LOG.warning("Firebase: error al publicar horas de " + fecha + " — " + e.getMessage());
        }
    }

    public void publicarProximos30Dias() {
        if (!inicializado) {
            return;
        }
        LocalDate hoy = LocalDate.now();
        for (int i = 0; i <= 30; i++) {
            LocalDate fecha = hoy.plusDays(i);
            if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }
            publicarHorasOcupadas(fecha);
        }
    }

    public void importarSolicitudesNuevas() {
        if (!inicializado) {
            return;
        }
        try {
            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            final DataSnapshot[] resultado = {null};
            final Exception[] error = {null};

            FirebaseDatabase.getInstance()
                    .getReference("solicitudes")
                    .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            resultado[0] = snapshot;
                            latch.countDown();
                        }

                        @Override
                        public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                            error[0] = databaseError.toException();
                            latch.countDown();
                        }
                    });

            latch.await(10, java.util.concurrent.TimeUnit.SECONDS);

            if (error[0] != null) {
                throw error[0];
            }
            if (resultado[0] == null) {
                return;
            }

            for (DataSnapshot child : resultado[0].getChildren()) {
                String estado = child.child("estado").getValue(String.class);
                if (!"PENDIENTE".equals(estado)) {
                    continue;
                }
                try {
                    procesarSolicitud(child);
                } catch (Exception e) {
                    LOG.warning("Firebase: error al procesar solicitud '"
                            + child.getKey() + "' — " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOG.warning("Firebase: error al leer solicitudes — " + e.getMessage());
        }
    }

    public void actualizarEstadoSolicitud(String idFirebase, String estado) {
        if (!inicializado) {
            return;
        }
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("solicitudes/" + idFirebase + "/estado");
            ref.setValueAsync(estado).get();
        } catch (Exception e) {
            LOG.warning("Firebase: error al actualizar estado '" + idFirebase + "' — " + e.getMessage());
        }
    }

    private InputStream resolverCredenciales(String path) {
        InputStream is = getClass().getResourceAsStream("/" + path);
        if (is != null) {
            return is;
        }
        try {
            File archivo = new File(path);
            if (archivo.exists()) {
                return new FileInputStream(archivo);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private List<String> consultarHorasOcupadas(LocalDate fecha) {
        java.util.Set<String> horas = new java.util.LinkedHashSet<>();
        String sqlCita = "SELECT TO_CHAR(fecha_hora, 'HH24:MI') AS hora FROM CITA "
                + "WHERE TRUNC(fecha_hora) = ? "
                + "AND estado_cita IN ('PROGRAMADA', 'EN_CURSO') "
                + "ORDER BY fecha_hora";
        String sqlSolicitud = "SELECT hora FROM SOLICITUD_CITA "
                + "WHERE fecha = ? AND estado IN ('PENDIENTE', 'ACEPTADA')";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (PreparedStatement ps = con.prepareStatement(sqlCita)) {
                ps.setDate(1, Date.valueOf(fecha));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        horas.add(rs.getString("hora"));
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sqlSolicitud)) {
                ps.setDate(1, Date.valueOf(fecha));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        horas.add(rs.getString("hora"));
                    }
                }
            }
        } catch (Exception e) {
            LOG.warning("Firebase: error al consultar Oracle para " + fecha + " — " + e.getMessage());
        }
        return new ArrayList<>(horas);
    }

    private void procesarSolicitud(DataSnapshot child) throws Exception {
        String nombre = valor(child, "nombre");
        String fechaStr = valor(child, "fecha");
        String hora = valor(child, "hora");

        if (nombre == null || fechaStr == null || hora == null) {
            return;
        }

        LocalDate fecha = LocalDate.parse(fechaStr);
        if (existeEnOracle(nombre, fecha, hora)) {
            return;
        }

        SolicitudCita solicitudCita = new SolicitudCita();
        solicitudCita.setNombrePropietario(nombre);
        solicitudCita.setTelefono(valor(child, "telefono"));
        solicitudCita.setCorreo(valor(child, "correo"));
        solicitudCita.setNombreMascota(valor(child, "nombreMascota"));
        solicitudCita.setEspecie(valor(child, "especie"));
        solicitudCita.setMotivo(valor(child, "motivo"));
        solicitudCita.setFecha(fecha);
        solicitudCita.setHora(hora);

        solicitudDAO.guardar(solicitudCita);
        LOG.info("Firebase: solicitud importada — " + nombre + " / " + fecha + " " + hora);
    }

    private boolean existeEnOracle(String nombre, LocalDate fecha, String hora) {
        String sql = "SELECT COUNT(*) FROM SOLICITUD_CITA "
                + "WHERE nombre_propietario = ? AND fecha = ? AND hora = ?";
        try {
            Connection conexion = ConexionBD.getInstancia().getConexion();
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setDate(2, Date.valueOf(fecha));
                ps.setString(3, hora);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    private String valor(DataSnapshot snap, String campo) {
        Object valor = snap.child(campo).getValue();
        return valor != null ? valor.toString() : null;
    }
}
