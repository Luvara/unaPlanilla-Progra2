package cr.ac.una.unaplanilla.controller;

import cr.ac.una.unaplanilla.service.EmpleadoService;
import cr.ac.una.unaplanilla.util.AppContext;
import cr.ac.una.unaplanilla.util.FlowController;
import cr.ac.una.unaplanilla.util.Mensaje;
import cr.ac.una.unaplanilla.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author UNA
 */
public class LogInViewController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private ImageView imgFondo;
    @FXML
    private MFXTextField txtUsuario;
    @FXML
    private MFXPasswordField txtContrasena;
    @FXML
    private MFXButton btnCancelar;
    @FXML
    private MFXButton btnIngresar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        imgFondo.fitHeightProperty().bind(root.heightProperty());
        imgFondo.fitWidthProperty().bind(root.widthProperty());
    }

    @Override
    public void initialize() {
        //txtUser.clear();
        //txtPassword.clear();
    }
    
    @FXML
    private void onKeyPressContrasena(KeyEvent event) {
        if (event.getCode().equals(ENTER)) {
            ingresar();
        }
    }

    @FXML
    private void onActionBtnCancelar(ActionEvent event) {
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }

    @FXML
    private void onActionBtnIngresar(ActionEvent event) {
        ingresar();
    }
    
    private void ingresar() {
        try {
            if (txtUsuario.getText() == null || txtUsuario.getText().isEmpty()) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Validacion Usuario", getStage(), "Es necesario digital un usuario para ingresar al sistema");
            } else if (txtContrasena.getText() == null || txtContrasena.getText().isEmpty()) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Validacion Clave", getStage(), "Es necesario digital una contraseña para ingresar al sistema");
            } else {
                EmpleadoService empleadoService = new EmpleadoService();
                Respuesta respuesta = empleadoService.getUsuario(txtUsuario.getText(), txtContrasena.getText());
                if (respuesta.getEstado()) {
                    AppContext.getInstance().set("Usuario", respuesta.getResultado("Empleado"));
                    FlowController.getInstance().goMain();
                    getStage().close();
                } else {
                    new Mensaje().show(Alert.AlertType.ERROR, "Validación Usuario", respuesta.getMensaje());
                }

            }
        } catch (Exception ex) {
            Logger.getLogger(LogInViewController.class.getName()).log(Level.SEVERE, "Error ingresando.", ex);
        }
    }

    
}
