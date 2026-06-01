package util;

import service.FirebaseService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FirebasePolling {

    private static final Logger LOG = Logger.getLogger(FirebasePolling.class.getName());
    private static ScheduledExecutorService executor;
    private static final FirebaseService firebaseService = new FirebaseService();

    public static void iniciar() {
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "firebase-polling");
            t.setDaemon(true);
            return t;
        });
        executor.scheduleAtFixedRate(() -> {
            try {
                firebaseService.importarSolicitudesNuevas();
            } catch (Exception e) {
                LOG.warning("FirebasePolling: error en ciclo — " + e.getMessage());
            }
        }, 2, 2, TimeUnit.MINUTES);
        LOG.info("FirebasePolling iniciado (cada 2 minutos).");
    }

    public static void detener() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            LOG.info("FirebasePolling detenido.");
        }
    }
}
