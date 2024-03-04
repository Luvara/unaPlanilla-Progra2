package cr.ac.una.unaplanilla.controller;

import cr.ac.una.unaplanilla.model.EmpleadoDto;
import cr.ac.una.unaplanilla.service.EmpleadoService;
import cr.ac.una.unaplanilla.util.BindingUtils;
import cr.ac.una.unaplanilla.util.FlowController;
import cr.ac.una.unaplanilla.util.Formato;
import cr.ac.una.unaplanilla.util.Mensaje;
import cr.ac.una.unaplanilla.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.scene.input.KeyEvent;

/**
 * FXML Controller class
 *
 * @author UNA
 */
public class EmpleadosViewController extends Controller implements Initializable {

    @FXML
    private ToggleGroup tggGenero;
    @FXML
    private MFXCheckbox chbAdmin;
    @FXML
    private MFXDatePicker dtpIngreso;
    @FXML
    private MFXDatePicker dtpSalida;
    @FXML
    private MFXRadioButton rdbMasculino;
    @FXML
    private MFXRadioButton rdbFemenino;
    @FXML
    private MFXTextField txtID;
    @FXML
    private MFXTextField txtNombre;
    @FXML
    private MFXTextField txtPApellido;
    @FXML
    private MFXTextField txtSApellido;
    @FXML
    private MFXTextField txtCedula;
    @FXML
    private MFXCheckbox chbActivo;
    @FXML
    private MFXTextField txtCorreo;
    @FXML
    private MFXTextField txtUsuario;
    @FXML
    private MFXTextField txtContrasena;
    @FXML
    private MFXButton btnNuevo;
    @FXML
    private MFXButton btnBuscar;
    @FXML
    private MFXButton btnEliminar;
    @FXML
    private MFXButton btnGuardar;

    EmpleadoDto empleado;
    List<Node> requeridos = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        rdbMasculino.setUserData("M");
        rdbFemenino.setUserData("F");
        txtID.setTextFormatter(Formato.getInstance().integerFormat());
        txtNombre.setTextFormatter(Formato.getInstance().letrasFormat(30));
        txtPApellido.setTextFormatter(Formato.getInstance().letrasFormat(15));
        txtSApellido.setTextFormatter(Formato.getInstance().letrasFormat(15));
        txtCedula.setTextFormatter(Formato.getInstance().cedulaFormat(40));
        txtCorreo.setTextFormatter(Formato.getInstance().maxLengthFormat(80));
        txtUsuario.setTextFormatter(Formato.getInstance().letrasFormat(15));
        txtContrasena.setTextFormatter(Formato.getInstance().maxLengthFormat(8));
        empleado = new EmpleadoDto();
        nuevoEmpleado();
        indicarRequeridos();
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionChbAdmin(ActionEvent event) {
        validarAdministrador();
    }

    @FXML
    private void onActionBtnNuevo(ActionEvent event) {
        nuevoEmpleado();
    }

    @FXML
    private void onActionBtnBuscar(ActionEvent event) {
        FlowController.getInstance().goViewInWindow("BuscadorView");
    }

    @FXML
    private void onActionBtnEliminar(ActionEvent event) {
        try {
            if (empleado.getId() == null) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar empleado", getStage(), "Debe cargar el empleado que desea eliminar.");
            } else {
                EmpleadoService service = new EmpleadoService();
                Respuesta respuesta = service.eliminarEmpleado(empleado.getId());
                if (!respuesta.getEstado()) {
                    new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar empleado", getStage(), respuesta.getMensaje());

                } else {
                    new Mensaje().showModal(Alert.AlertType.INFORMATION, "Eliminar empleado", getStage(), "Empleado eliminado correctamente.");
                    nuevoEmpleado();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(EmpleadosViewController.class.getName()).log(Level.SEVERE, "Error eliminando el empleado", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar empleado", getStage(), "Ocurrio un error eliminando el empleado.");
        }
    }

    @FXML
    private void onActionBtnGuardar(ActionEvent event) {
        try {
            String invalidos = validarRequeridos();
            if (!invalidos.isEmpty()) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar empleado", getStage(), invalidos);
            } else {
                EmpleadoService empleadoService = new EmpleadoService();
                Respuesta respuesta = empleadoService.guardarEmpleado(empleado);
                if (!respuesta.getEstado()) {
                    new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar empleado", getStage(), respuesta.getMensaje());
                } else {
                    unbindEmpleado();
                    empleado = (EmpleadoDto) respuesta.getResultado("Empleado");
                    bindEmpleado(false);
                    new Mensaje().showModal(Alert.AlertType.INFORMATION, "Guardar Empleado", getStage(), "Empleado actualizado");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(EmpleadosViewController.class.getName()).log(Level.SEVERE, "Error guardando el empleado", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar empleado", getStage(), "ocurrio un error guardando el cliente");
        }
    }

    @FXML
    private void onKeyPressedID(KeyEvent event) {
        if (event.getCode().equals(ENTER)) {
            EmpleadoService empleadoService = new EmpleadoService();
            Respuesta respuesta = empleadoService.getDatosId(Long.valueOf(txtID.getText()));
            if (respuesta.getEstado()) {
                unbindEmpleado();
                empleado = (EmpleadoDto) respuesta.getResultado("Empleado");
                bindEmpleado(false);
                validarAdministrador();
            } else {
                new Mensaje().show(Alert.AlertType.ERROR, "Validación id", respuesta.getMensaje());
            }
        }
    }

    private void nuevoEmpleado() {
        unbindEmpleado();
        empleado = new EmpleadoDto();
        bindEmpleado(true);
        validarAdministrador();
        txtID.clear();
        txtID.requestFocus();
    }

    private void indicarRequeridos() {
        requeridos.clear();
        requeridos.addAll(Arrays.asList(txtNombre, txtCedula, txtPApellido, txtSApellido, dtpIngreso));
    }

    public String validarRequeridos() {
        Boolean validos = true;
        String invalidos = "";
        for (Node node : requeridos) {
            if (node instanceof MFXTextField && ((MFXTextField) node).getText().isBlank()) {
                if (validos) {
                    invalidos += ((MFXTextField) node).getPromptText();
                } else {
                    invalidos += "," + ((MFXTextField) node).getPromptText();
                }
                validos = false;
            } else if (node instanceof MFXPasswordField && ((MFXPasswordField) node).getText().isBlank()) {
                if (validos) {
                    invalidos += ((MFXPasswordField) node).getPromptText();
                } else {
                    invalidos += "," + ((MFXPasswordField) node).getPromptText();
                }
                validos = false;
            } else if (node instanceof MFXDatePicker && ((MFXDatePicker) node).getValue() == null) {
                if (validos) {
                    invalidos += ((MFXDatePicker) node).getAccessibleText();
                } else {
                    invalidos += "," + ((MFXDatePicker) node).getAccessibleText();
                }
                validos = false;
            } else if (node instanceof MFXComboBox && ((MFXComboBox) node).getSelectionModel().getSelectedIndex() < 0) {
                if (validos) {
                    invalidos += ((MFXComboBox) node).getPromptText();
                } else {
                    invalidos += "," + ((MFXComboBox) node).getPromptText();
                }
                validos = false;
            }
        }
        if (validos) {
            return "";
        } else {
            return "Campos requeridos o con problemas de formato [" + invalidos + "].";
        }
    }

    private void bindEmpleado(boolean nuevo) {
        if (!nuevo) {
            txtID.textProperty().bind(empleado.id);
        }
        if (empleado != null) {
            txtCedula.textProperty().bindBidirectional(empleado.cedula);
            txtNombre.textProperty().bindBidirectional(empleado.nombre);
            txtPApellido.textProperty().bindBidirectional(empleado.primerApellido);
            txtSApellido.textProperty().bindBidirectional(empleado.segundoApellido);
            txtUsuario.textProperty().bindBidirectional(empleado.usuario);
            txtContrasena.textProperty().bindBidirectional(empleado.clave);
            txtCorreo.textProperty().bindBidirectional(empleado.correo);
            dtpIngreso.valueProperty().bindBidirectional(empleado.fechaIngreso);
            dtpSalida.valueProperty().bindBidirectional(empleado.fechaSalida);
            chbActivo.selectedProperty().bindBidirectional(empleado.estado);
            chbAdmin.selectedProperty().bindBidirectional(empleado.administrador);
            BindingUtils.bindToggleGroupToProperty(tggGenero, empleado.genero);
        } else {
            txtCedula.textProperty().unbind(); // Desvincula el cuadro de texto
            txtCedula.clear();
            txtNombre.textProperty().unbind(); // Desvincula el cuadro de texto
            txtNombre.clear();
            txtPApellido.textProperty().unbind(); // Desvincula el cuadro de texto
            txtPApellido.clear();
            txtSApellido.textProperty().unbind(); // Desvincula el cuadro de texto
            txtSApellido.clear();
            txtUsuario.textProperty().unbind(); // Desvincula el cuadro de texto
            txtUsuario.clear();
            txtContrasena.textProperty().unbind(); // Desvincula el cuadro de texto
            txtContrasena.clear();
            txtCorreo.textProperty().unbind(); // Desvincula el cuadro de texto
            txtCorreo.clear();
            dtpIngreso.textProperty().unbind(); // Desvincula el cuadro de texto
            dtpIngreso.clear();
            dtpSalida.textProperty().unbind(); // Desvincula el cuadro de texto
            dtpSalida.clear();
            chbActivo.selectedProperty().bindBidirectional(empleado.estado);
            chbAdmin.selectedProperty().bindBidirectional(empleado.administrador);
            BindingUtils.bindToggleGroupToProperty(tggGenero, empleado.genero);
            
        }
    }

    private void unbindEmpleado() {
        txtID.textProperty().unbind();
        txtCedula.textProperty().unbindBidirectional(empleado.cedula);
        txtNombre.textProperty().unbindBidirectional(empleado.nombre);
        txtPApellido.textProperty().unbindBidirectional(empleado.primerApellido);
        txtSApellido.textProperty().unbindBidirectional(empleado.segundoApellido);
        txtUsuario.textProperty().unbindBidirectional(empleado.usuario);
        txtContrasena.textProperty().unbindBidirectional(empleado.clave);
        txtCorreo.textProperty().unbindBidirectional(empleado.correo);
        dtpIngreso.valueProperty().unbindBidirectional(empleado.fechaIngreso);
        dtpSalida.valueProperty().unbindBidirectional(empleado.fechaSalida);
        chbActivo.selectedProperty().unbindBidirectional(empleado.estado);
        chbAdmin.selectedProperty().unbindBidirectional(empleado.administrador);
        BindingUtils.unbindToggleGroupToProperty(tggGenero, empleado.genero);
    }

    private void validarAdministrador() {
        if (chbAdmin.isSelected()) {
            requeridos.addAll(Arrays.asList(txtUsuario, txtContrasena));
            txtUsuario.setDisable(false);
            txtContrasena.setDisable(false);
        } else {
            requeridos.removeAll(Arrays.asList(txtUsuario, txtContrasena));
            txtUsuario.clear();
            txtUsuario.setDisable(true);
            txtContrasena.clear();
            txtContrasena.setDisable(true);
        }
    }

    public void bindBuscar() {
        BuscadorViewController buscadorViewController = (BuscadorViewController) FlowController.getInstance().getController("BuscadorView");
        unbindEmpleado();
        empleado = (EmpleadoDto) buscadorViewController.getSeleccionado();
        bindEmpleado(false);
        validarAdministrador();
        //txtID.clear();
        txtID.requestFocus();

    }

}
