package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Paciente;
import model.Propietario;
import service.PacienteService;
import service.PropietarioService;
import util.ComboBoxFilter;
import util.ConexionBD;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class NuevoPropietarioController implements Initializable {

    private static final List<String> TODAS_LAS_ESPECIES = Arrays.asList(
        "Perro", "Gato", "Conejo", "Hámster Dorado", "Hámster Ruso",
        "Cobaya / Cuy", "Chinchilla", "Hurón", "Rata Doméstica", "Ratón Doméstico",
        "Erizo Africano", "Degú", "Jerbo", "Mini Pig",
        "Ave", "Loro / Papagayo", "Periquito / Perico", "Canario",
        "Cacatúa", "Guacamayo", "Agapornis", "Cotorra",
        "Reptil", "Iguana", "Tortuga", "Serpiente", "Gecko", "Camaleón",
        "Pez", "Otro"
    );

    private static final List<String> TODAS_LAS_RAZAS = Arrays.asList(
        "Labrador Retriever", "Golden Retriever", "Pastor Alemán",
        "Bulldog Francés", "Bulldog Inglés", "French Poodle",
        "Beagle", "Rottweiler", "Yorkshire Terrier", "Chihuahua",
        "Dachshund / Teckel", "Boxer", "Doberman Pinscher", "Husky Siberiano",
        "Border Collie", "Cocker Spaniel Inglés", "Cocker Spaniel Americano",
        "Shih Tzu", "Maltés", "Pomerania / Spitz", "Schnauzer Miniatura",
        "Schnauzer Estándar", "Schnauzer Gigante", "Pit Bull Terrier",
        "American Bully", "Dálmata", "Shar-Pei", "Akita Inu", "Shiba Inu",
        "Bichón Frisé", "Gran Danés", "Samoyedo", "Alaskan Malamute",
        "Jack Russell Terrier", "Pug / Carlino", "Basset Hound", "Chow Chow",
        "Weimaraner", "Australian Shepherd", "Australian Cattle Dog",
        "Bernese Mountain Dog", "Cavalier King Charles Spaniel",
        "Springer Spaniel Inglés", "Setter Irlandés", "Setter Inglés",
        "Bull Terrier", "Staffordshire Bull Terrier",
        "American Staffordshire Terrier", "Airedale Terrier",
        "West Highland White Terrier", "Cairn Terrier", "Scottish Terrier",
        "Fox Terrier Pelo Liso", "Fox Terrier Pelo Duro", "Border Terrier",
        "Welsh Corgi Pembroke", "Welsh Corgi Cardigan", "Rough Collie",
        "Shetland Sheepdog", "Old English Sheepdog", "Lhasa Apso",
        "Pekingés", "Cane Corso", "Dogo Argentino", "Fila Brasileño",
        "Boerboel", "Mastín Napolitano", "Mastín Inglés", "Mastín Tibetano",
        "Pastor Belga Malinois", "Pastor Belga Tervuren",
        "Pastor Blanco Suizo", "Rhodesian Ridgeback", "Greyhound / Galgo",
        "Whippet", "Afghan Hound", "Saluki", "Irish Wolfhound",
        "Borzoi", "Vizsla Húngaro", "Braco Alemán",
        "Nova Scotia Duck Tolling Retriever", "Flat-Coated Retriever",
        "Chesapeake Bay Retriever", "Irish Water Spaniel",
        "Newfoundland / Terranova", "San Bernardo", "Leonberger",
        "Miniature Pinscher", "Maltipoo", "Labradoodle", "Goldendoodle",
        "Cockapoo", "Havanés", "Coton de Tuléar", "Bichón Habanero",
        "Perro de Agua Español", "Cimarrón Uruguayo", "Galgo Español",
        "Podenco Ibicenco", "Perro Sin Pelo del Perú", "Xoloitzcuintle",
        "Spitz Japonés",
        "Persa", "Siamés", "Maine Coon", "Ragdoll", "Bengalí",
        "British Shorthair", "Abisinio", "Sphynx", "Scottish Fold",
        "Birmano", "Angora Turco", "Azul Ruso", "Devon Rex", "Cornish Rex",
        "Selkirk Rex", "American Shorthair", "American Curl",
        "Noruego del Bosque", "Siberiano", "Bombay", "Burmés",
        "Tonkinés", "Ocicat", "Mau Egipcio", "Balinés",
        "Turkish Van", "Somali", "Chartreux", "Manx",
        "Singapura", "Javanés",
        "Periquito Australiano", "Periquito Americano", "Canario",
        "Loro Amazónico", "Loro Gris Africano", "Cacatúa",
        "Cacatúa Ninfa / Cockatiel", "Agapornis", "Guacamayo Azul y Amarillo",
        "Guacamayo Rojo", "Cotorra", "Perico", "Tucán", "Paloma", "Jilguero",
        "Iguana Verde", "Iguana Rinoceronte", "Gecko Leopardo",
        "Gecko de Cresta", "Camaleón Velado", "Tortuga de Tierra",
        "Tortuga Acuática", "Tortuga Mediterránea", "Boa Constrictor",
        "Serpiente Maíz", "Dragón Barbudo", "Lagartija de Jardín",
        "Hámster Dorado", "Hámster Ruso", "Hámster Chino",
        "Conejo", "Cobaya / Cuy", "Ratón Doméstico", "Rata Doméstica",
        "Hurón", "Chinchilla", "Erizo Africano", "Jerbo", "Degú",
        "Mestizo", "Otra"
    );

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;
    @FXML private Button    btnGuardar;
    @FXML private Label     lblMensaje;

    @FXML private Label            lblToggleMascota;
    @FXML private VBox             panelMascota;
    @FXML private TextField        txtNombreMascota;
    @FXML private ComboBox<String> cbEspecie;
    @FXML private TextField        txtEspecieOtro;
    @FXML private ComboBox<String> cbRaza;
    @FXML private TextField        txtRazaOtra;
    @FXML private ComboBox<String> cbSexo;
    @FXML private TextField        txtPeso;
    @FXML private DatePicker       dpFechaNacimiento;
    @FXML private TextField        txtMicrochip;

    private Propietario propietarioEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ComboBoxFilter.apply(cbEspecie, TODAS_LAS_ESPECIES);
        cbEspecie.setVisibleRowCount(6);
        cbEspecie.setPromptText("Buscar especie...");
        cbEspecie.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean esOtro = "Otro".equals(newVal);
            txtEspecieOtro.setVisible(esOtro);
            txtEspecieOtro.setManaged(esOtro);
            if (!esOtro) txtEspecieOtro.clear();
        });

        ComboBoxFilter.apply(cbRaza, TODAS_LAS_RAZAS);
        cbRaza.setVisibleRowCount(6);
        cbRaza.setPromptText("Buscar raza...");
        cbRaza.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean esOtra = "Otra".equals(newVal);
            txtRazaOtra.setVisible(esOtra);
            txtRazaOtra.setManaged(esOtra);
            if (!esOtra) txtRazaOtra.clear();
        });

        cbSexo.setVisibleRowCount(2);
        cbSexo.getItems().addAll("Macho", "Hembra");
    }

    @FXML
    private void handleToggleMascota() {
        boolean expandir = !panelMascota.isVisible();
        panelMascota.setVisible(expandir);
        panelMascota.setManaged(expandir);
        lblToggleMascota.setText(expandir ? "✕ Cancelar registro de mascota" : "＋ Registrar mascota");
        if (!expandir) {
            txtNombreMascota.clear();
            cbEspecie.setValue(null);
            txtEspecieOtro.clear();
            cbRaza.setValue(null);
            txtRazaOtra.clear();
            cbSexo.setValue(null);
            txtPeso.clear();
            dpFechaNacimiento.setValue(null);
            txtMicrochip.clear();
        }
    }

    public void setModoEdicion(Propietario p) {
        this.propietarioEnEdicion = p;
        txtCedula.setText(p.getCedula());
        txtCedula.setDisable(true);
        txtNombre.setText(p.getNombre());
        txtApellido.setText(p.getApellido());
        txtTelefono.setText(p.getTelefono() != null ? p.getTelefono() : "");
        txtCorreo.setText(p.getEmail() != null ? p.getEmail() : "");
        txtDireccion.setText(p.getDireccion() != null ? p.getDireccion() : "");
        btnGuardar.setText("Actualizar");
        lblToggleMascota.setVisible(false);
        lblToggleMascota.setManaged(false);
        panelMascota.setVisible(false);
        panelMascota.setManaged(false);
    }

    @FXML
    private void handleGuardar() {
        String cedula   = txtCedula.getText().trim();
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
            mostrarMensaje("Cédula, nombre y apellido son obligatorios.", "#D32F2F");
            return;
        }

        boolean guardarMascota = panelMascota.isVisible() && propietarioEnEdicion == null;

        String especieVal = "";
        String razaVal = "";

        if (guardarMascota) {
            String nombreMascota = txtNombreMascota.getText().trim();
            if (nombreMascota.isEmpty()) {
                mostrarMensaje("El nombre de la mascota es obligatorio.", "#D32F2F");
                return;
            }

            if ("Otro".equals(cbEspecie.getValue())) {
                especieVal = txtEspecieOtro.getText().trim();
            } else if (cbEspecie.getValue() != null) {
                especieVal = cbEspecie.getValue();
            } else {
                especieVal = cbEspecie.getEditor().getText() != null
                    ? cbEspecie.getEditor().getText().trim() : "";
            }
            if (especieVal.isEmpty()) {
                mostrarMensaje("La especie de la mascota es obligatoria.", "#D32F2F");
                return;
            }

            if ("Otra".equals(cbRaza.getValue())) {
                razaVal = txtRazaOtra.getText().trim();
            } else if (cbRaza.getValue() != null) {
                razaVal = cbRaza.getValue();
            } else {
                razaVal = cbRaza.getEditor().getText() != null
                    ? cbRaza.getEditor().getText().trim() : "";
            }
            if (razaVal.isEmpty()) {
                mostrarMensaje("La raza de la mascota es obligatoria.", "#D32F2F");
                return;
            }
        }

        double peso = 0;
        if (guardarMascota && !txtPeso.getText().trim().isEmpty()) {
            try {
                peso = Double.parseDouble(txtPeso.getText().trim().replace(",", "."));
                if (peso < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                mostrarMensaje("El peso debe ser un número positivo (ej: 4.5).", "#D32F2F");
                return;
            }
        }

        Connection con = ConexionBD.getInstancia().getConexion();
        try {
            Propietario p = new Propietario(
                    cedula, nombre, apellido,
                    txtTelefono.getText().trim(),
                    txtCorreo.getText().trim(),
                    txtDireccion.getText().trim()
            );

            if (propietarioEnEdicion == null) {
                if (guardarMascota) {
                    con.setAutoCommit(false);
                    try {
                        new PropietarioService().guardar(p);
                        Paciente mascota = new Paciente(
                                txtNombreMascota.getText().trim(),
                                especieVal,
                                razaVal,
                                cbSexo.getValue() != null ? cbSexo.getValue() : "",
                                peso,
                                dpFechaNacimiento.getValue(),
                                txtMicrochip.getText().trim(),
                                p
                        );
                        new PacienteService().guardar(mascota);
                        con.commit();
                    } catch (Exception ex) {
                        try { con.rollback(); } catch (SQLException ignored) {}
                        mostrarMensaje("Error al guardar: " + ex.getMessage(), "#D32F2F");
                        return;
                    } finally {
                        try { con.setAutoCommit(true); } catch (SQLException ignored) {}
                    }
                    mostrarMensaje("Propietario y mascota guardados exitosamente.", "#1B6B2F");
                } else {
                    new PropietarioService().guardar(p);
                    mostrarMensaje("Propietario guardado exitosamente.", "#1B6B2F");
                }
            } else {
                new PropietarioService().actualizar(p);
                mostrarMensaje("Propietario actualizado exitosamente.", "#1B6B2F");
            }

            ((Stage) lblMensaje.getScene().getWindow()).close();

        } catch (Exception e) {
            mostrarMensaje("Error: " + e.getMessage(), "#D32F2F");
        }
    }

    @FXML
    private void handleCancelar() {
        ((Stage) lblMensaje.getScene().getWindow()).close();
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }
}
