package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Paciente;
import model.Propietario;
import service.PacienteService;
import service.PropietarioService;

import util.ComboBoxFilter;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class NuevoPacienteController implements Initializable {

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
        // ── Perros ──────────────────────────────────────────────────────
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
        // ── Gatos ───────────────────────────────────────────────────────
        "Persa", "Siamés", "Maine Coon", "Ragdoll", "Bengalí",
        "British Shorthair", "Abisinio", "Sphynx", "Scottish Fold",
        "Birmano", "Angora Turco", "Azul Ruso", "Devon Rex", "Cornish Rex",
        "Selkirk Rex", "American Shorthair", "American Curl",
        "Noruego del Bosque", "Siberiano", "Bombay", "Burmés",
        "Tonkinés", "Ocicat", "Mau Egipcio", "Balinés",
        "Turkish Van", "Somali", "Chartreux", "Manx",
        "Singapura", "Javanés",
        // ── Aves ────────────────────────────────────────────────────────
        "Periquito Australiano", "Periquito Americano", "Canario",
        "Loro Amazónico", "Loro Gris Africano", "Cacatúa",
        "Cacatúa Ninfa / Cockatiel", "Agapornis", "Guacamayo Azul y Amarillo",
        "Guacamayo Rojo", "Cotorra", "Perico", "Tucán", "Paloma", "Jilguero",
        // ── Reptiles ────────────────────────────────────────────────────
        "Iguana Verde", "Iguana Rinoceronte", "Gecko Leopardo",
        "Gecko de Cresta", "Camaleón Velado", "Tortuga de Tierra",
        "Tortuga Acuática", "Tortuga Mediterránea", "Boa Constrictor",
        "Serpiente Maíz", "Dragón Barbudo", "Lagartija de Jardín",
        // ── Pequeños mamíferos ──────────────────────────────────────────
        "Hámster Dorado", "Hámster Ruso", "Hámster Chino",
        "Conejo", "Cobaya / Cuy", "Ratón Doméstico", "Rata Doméstica",
        "Hurón", "Chinchilla", "Erizo Africano", "Jerbo", "Degú",
        // ── General ─────────────────────────────────────────────────────
        "Mestizo", "Otra"
    );

    @FXML private ComboBox<Propietario> cbPropietario;
    @FXML private TextField             txtNombre;
    @FXML private ComboBox<String>      cbEspecie;
    @FXML private TextField             txtEspecieOtro;
    @FXML private ComboBox<String>      cbRaza;
    @FXML private TextField             txtRazaOtra;
    @FXML private ComboBox<String>      cbSexo;
    @FXML private TextField             txtPeso;
    @FXML private DatePicker            dpFechaNacimiento;
    @FXML private TextField             txtMicrochip;
    @FXML private Button                btnGuardar;
    @FXML private Label                 lblMensaje;

    private Paciente pacienteEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            List<Propietario> propietarios = new PropietarioService().listarTodos();
            ComboBoxFilter.apply(cbPropietario, propietarios, Object::toString);
        } catch (SQLException e) {
            System.err.println("Error cargando propietarios: " + e.getMessage());
        }

        // ComboBox de especie con filtro en tiempo real
        cbEspecie.setEditable(true);
        cbEspecie.getItems().addAll(TODAS_LAS_ESPECIES);
        cbEspecie.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, ev -> {
            String texto = cbEspecie.getEditor().getText();
            String lower = texto == null ? "" : texto.toLowerCase();
            List<String> filtradas = TODAS_LAS_ESPECIES.stream()
                .filter(e -> e.toLowerCase().contains(lower))
                .collect(Collectors.toList());
            cbEspecie.getItems().setAll(filtradas);
            cbEspecie.getEditor().setText(texto);
            cbEspecie.getEditor().positionCaret(texto == null ? 0 : texto.length());
            if (!filtradas.isEmpty()) cbEspecie.show();
        });
        cbEspecie.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean esOtro = "Otro".equals(newVal);
            txtEspecieOtro.setVisible(esOtro);
            txtEspecieOtro.setManaged(esOtro);
            if (!esOtro) txtEspecieOtro.clear();
        });

        cbSexo.getItems().addAll("Macho", "Hembra");

        // ComboBox de raza con filtro en tiempo real
        cbRaza.setEditable(true);
        cbRaza.getItems().addAll(TODAS_LAS_RAZAS);

        cbRaza.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, ev -> {
            String texto = cbRaza.getEditor().getText();
            String lower = texto == null ? "" : texto.toLowerCase();
            List<String> filtradas = TODAS_LAS_RAZAS.stream()
                .filter(r -> r.toLowerCase().contains(lower))
                .collect(Collectors.toList());
            cbRaza.getItems().setAll(filtradas);
            cbRaza.getEditor().setText(texto);
            cbRaza.getEditor().positionCaret(texto == null ? 0 : texto.length());
            if (!filtradas.isEmpty()) cbRaza.show();
        });

        cbRaza.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean esOtra = "Otra".equals(newVal);
            txtRazaOtra.setVisible(esOtra);
            txtRazaOtra.setManaged(esOtra);
            if (!esOtra) txtRazaOtra.clear();
        });
    }

    public void setModoEdicion(Paciente pac) {
        this.pacienteEnEdicion = pac;

        if (pac.getPropietario() != null) {
            String cedula = pac.getPropietario().getCedula();
            for (Propietario p : cbPropietario.getItems()) {
                if (p.getCedula().equals(cedula)) {
                    cbPropietario.setValue(p);
                    break;
                }
            }
        }
        cbPropietario.setDisable(true);

        txtNombre.setText(pac.getNombre());

        String especie = pac.getEspecie() != null ? pac.getEspecie() : "";
        if (!especie.isEmpty() && !TODAS_LAS_ESPECIES.contains(especie)) {
            cbEspecie.setValue("Otro");
            txtEspecieOtro.setText(especie);
            txtEspecieOtro.setVisible(true);
            txtEspecieOtro.setManaged(true);
        } else {
            cbEspecie.setValue(especie.isEmpty() ? null : especie);
        }

        String raza = pac.getRaza() != null ? pac.getRaza() : "";
        if (!raza.isEmpty() && !TODAS_LAS_RAZAS.contains(raza)) {
            cbRaza.setValue("Otra");
            txtRazaOtra.setText(raza);
            txtRazaOtra.setVisible(true);
            txtRazaOtra.setManaged(true);
        } else {
            cbRaza.setValue(raza.isEmpty() ? null : raza);
        }

        cbSexo.setValue(pac.getSexo());
        if (pac.getPeso() > 0) {
            txtPeso.setText(String.valueOf(pac.getPeso()));
        }
        dpFechaNacimiento.setValue(pac.getFechaNacimiento());
        txtMicrochip.setText(pac.getMicrochip() != null ? pac.getMicrochip() : "");
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void handleGuardar() {
        String especieVal;
        if ("Otro".equals(cbEspecie.getValue())) {
            especieVal = txtEspecieOtro.getText().trim();
        } else if (cbEspecie.getValue() != null) {
            especieVal = cbEspecie.getValue();
        } else {
            especieVal = cbEspecie.getEditor().getText() != null
                ? cbEspecie.getEditor().getText().trim() : "";
        }

        if (txtNombre.getText().trim().isEmpty()
                || especieVal == null || especieVal.isEmpty() || cbSexo.getValue() == null) {
            mostrarMensaje("Nombre, especie y sexo son obligatorios.", "#D32F2F");
            return;
        }

        String razaVal;
        if ("Otra".equals(cbRaza.getValue())) {
            razaVal = txtRazaOtra.getText().trim();
        } else if (cbRaza.getValue() != null) {
            razaVal = cbRaza.getValue();
        } else {
            razaVal = cbRaza.getEditor().getText() != null
                ? cbRaza.getEditor().getText().trim() : "";
        }

        try {
            Paciente pac = new Paciente();
            pac.setPropietario(cbPropietario.getValue());
            pac.setNombre(txtNombre.getText().trim());
            pac.setEspecie(especieVal);
            pac.setRaza(razaVal);
            pac.setSexo(cbSexo.getValue());
            if (!txtPeso.getText().trim().isEmpty()) {
                pac.setPeso(Double.parseDouble(txtPeso.getText().trim()));
            }
            pac.setFechaNacimiento(dpFechaNacimiento.getValue());
            pac.setMicrochip(txtMicrochip.getText().trim());

            if (pacienteEnEdicion == null) {
                new PacienteService().guardar(pac);
                mostrarMensaje("Paciente guardado exitosamente.", "#1B6B2F");
            } else {
                pac.setId(pacienteEnEdicion.getId());
                new PacienteService().actualizar(pac);
                mostrarMensaje("Paciente actualizado exitosamente.", "#1B6B2F");
            }
            ((Stage) lblMensaje.getScene().getWindow()).close();
        } catch (NumberFormatException e) {
            mostrarMensaje("El peso debe ser un número válido.", "#D32F2F");
        } catch (SQLException e) {
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