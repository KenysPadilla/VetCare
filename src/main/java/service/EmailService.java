package service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.InputStream;
import java.util.Properties;

public class EmailService {

    public void enviarCodigoRecuperacion(String destinatario, String codigo) {
        try (InputStream in = getClass().getResourceAsStream("/config.properties")) {

            Properties config = new Properties();
            config.load(in);

            final String host      = config.getProperty("mail.host");
            final String port      = config.getProperty("mail.port");
            final String usuario   = config.getProperty("mail.usuario");
            final String password  = config.getProperty("mail.password");
            final String remitente = config.getProperty("mail.remitente");

            Properties props = new Properties();
            props.put("mail.smtp.host",            host);
            props.put("mail.smtp.port",            port);
            props.put("mail.smtp.auth",            "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(usuario, password);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(destinatario));
            message.setSubject("VetCare - Codigo de Recuperacion");

            String html =
                    "<h2 style='color:#1B6B2F'>VetCare</h2>"
                    + "<p>Recibiste este correo porque solicitaste recuperar tu contrasena.</p>"
                    + "<p>Tu codigo de verificacion es:</p>"
                    + "<h1 style='color:#1B6B2F;letter-spacing:8px'>" + codigo + "</h1>"
                    + "<p>Este codigo expira en <strong>15 minutos</strong>.</p>"
                    + "<p>Si no solicitaste esto, ignora este correo.</p>";

            message.setContent(html, "text/html; charset=utf-8");

            Transport.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo: " + e.getMessage(), e);
        }
    }

    public void enviarConfirmacionCita(String destinatario, String nombrePropietario,
                                        String nombreMascota, String fecha, String hora) {
        try (InputStream in = getClass().getResourceAsStream("/config.properties")) {

            Properties config = new Properties();
            config.load(in);

            final String host      = config.getProperty("mail.host");
            final String port      = config.getProperty("mail.port");
            final String usuario   = config.getProperty("mail.usuario");
            final String password  = config.getProperty("mail.password");
            final String remitente = config.getProperty("mail.remitente");

            Properties props = new Properties();
            props.put("mail.smtp.host",            host);
            props.put("mail.smtp.port",            port);
            props.put("mail.smtp.auth",            "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(usuario, password);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("VetCare - Confirmacion de Cita Medica");

            String html =
                "<h2 style='color:#1B6B2F'>VetCare</h2>"
                + "<p>Hola <strong>" + nombrePropietario + "</strong>,</p>"
                + "<p>Tu solicitud de cita ha sido <strong style='color:#1B6B2F'>ACEPTADA</strong>. "
                + "A continuacion los detalles:</p>"
                + "<table style='border-collapse:collapse;margin:16px 0;'>"
                + "<tr><td style='padding:6px 12px;font-weight:bold;'>Mascota:</td>"
                + "<td style='padding:6px 12px;'>" + nombreMascota + "</td></tr>"
                + "<tr style='background:#f7faf8;'><td style='padding:6px 12px;font-weight:bold;'>Fecha:</td>"
                + "<td style='padding:6px 12px;'>" + fecha + "</td></tr>"
                + "<tr><td style='padding:6px 12px;font-weight:bold;'>Hora:</td>"
                + "<td style='padding:6px 12px;'>" + hora + "</td></tr>"
                + "</table>"
                + "<p>Por favor llega 10 minutos antes de tu cita. Ante cualquier novedad "
                + "comunicate al <strong>3001234567</strong>.</p>"
                + "<p style='color:#888;font-size:12px;'>VetCare — Valledupar, Cesar, Colombia</p>";

            message.setContent(html, "text/html; charset=utf-8");
            Transport.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo de confirmacion: " + e.getMessage(), e);
        }
    }

    public void enviarRechazoSolicitud(String destinatario, String nombrePropietario) {
        try (InputStream in = getClass().getResourceAsStream("/config.properties")) {

            Properties config = new Properties();
            config.load(in);

            final String host      = config.getProperty("mail.host");
            final String port      = config.getProperty("mail.port");
            final String usuario   = config.getProperty("mail.usuario");
            final String password  = config.getProperty("mail.password");
            final String remitente = config.getProperty("mail.remitente");

            Properties props = new Properties();
            props.put("mail.smtp.host",            host);
            props.put("mail.smtp.port",            port);
            props.put("mail.smtp.auth",            "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(usuario, password);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("VetCare - Solicitud de Cita No Disponible");

            String html =
                "<h2 style='color:#1B6B2F'>VetCare</h2>"
                + "<p>Hola <strong>" + nombrePropietario + "</strong>,</p>"
                + "<p>Lamentamos informarte que la fecha u hora que solicitaste "
                + "<strong style='color:#D32F2F'>no esta disponible</strong> en este momento.</p>"
                + "<p>Por favor comunicate con nosotros para encontrar una alternativa:</p>"
                + "<ul>"
                + "<li>📞 Telefono: <strong>3001234567</strong></li>"
                + "<li>🕐 Horario de atencion: Lunes a Viernes 8:00 AM - 6:00 PM | "
                + "Sabados 8:00 AM - 1:00 PM</li>"
                + "</ul>"
                + "<p>Tambien puedes usar el chatbot en nuestra pagina para solicitar "
                + "otra fecha disponible.</p>"
                + "<p style='color:#888;font-size:12px;'>VetCare — Valledupar, Cesar, Colombia</p>";

            message.setContent(html, "text/html; charset=utf-8");
            Transport.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo de rechazo: " + e.getMessage(), e);
        }
    }
}
