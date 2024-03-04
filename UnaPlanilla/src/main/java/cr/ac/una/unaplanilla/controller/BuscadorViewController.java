package cr.ac.una.unaplanilla.controller;

import cr.ac.una.unaplanilla.model.EmpleadoDto;
import cr.ac.una.unaplanilla.service.EmpleadoService;
import cr.ac.una.unaplanilla.util.FlowController;
import cr.ac.una.unaplanilla.util.Formato;
import cr.ac.una.unaplanilla.util.Mensaje;
import cr.ac.una.unaplanilla.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Luvara
 */
public class BuscadorViewController extends Controller implements Initializable {

    @FXML
    private VBox vboxParametros;
    @FXML
    private MFXButton btnFiltrar;
    @FXML
    private TableView<EmpleadoDto> tblvResultados;
    @FXML
    private MFXButton btnAceptar;
    @FXML
    private AnchorPane root;

    MFXTextField txtCedula;
    MFXTextField txtNombre;
    MFXTextField txtApellido;
    private Object seleccionado;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        anadirSlots();
    }
    
    @Override
    public void initialize() {}

    @FXML
    private void OnActionBtnFiltrar(ActionEvent event) {
        tblvResultados.getItems().clear();
        EmpleadoService service = new EmpleadoService();
        String cedula = "%" + txtCedula.getText() + "%";
        String nombre = "%" + txtNombre.getText() + "%";
        String apellido = "%" + txtApellido.getText() + "%";

        Respuesta respuesta = service.getEmpleados(cedula, nombre, apellido);

        if (respuesta.getEstado()) {
            ObservableList<EmpleadoDto> empleados = FXCollections.observableArrayList((List<EmpleadoDto>) respuesta.getResultado("Empleado"));
            tblvResultados.setItems(empleados);
            tblvResultados.refresh();
        } else {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Consultar Empleados", getStage(), respuesta.getMensaje());
        }
    }

    @FXML
    private void OnMouseClickedTabla(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (event.getClickCount() == 2) {
                seleccionado = tblvResultados.getSelectionModel().getSelectedItem();
                EmpleadosViewController empleadosController = (EmpleadosViewController) FlowController.getInstance().getController("EmpleadosView");
                empleadosController.bindBuscar();
                getStage().close();
            }
        }
    }

    @FXML
    private void OnActionBtnAceptar(ActionEvent event) {
        seleccionado = tblvResultados.getSelectionModel().getSelectedItem();
        EmpleadosViewController empleadosController = (EmpleadosViewController) FlowController.getInstance().getController("EmpleadosView");
        empleadosController.bindBuscar();
        getStage().close();
    }

    public void anadirSlots() {
        vboxParametros.setSpacing(10);

        txtCedula = new MFXTextField();
        txtCedula.setFloatingText("Cedula");
        txtCedula.setTextFormatter(Formato.getInstance().cedulaFormat(40));
        txtCedula.setPrefWidth(Double.MAX_VALUE);

        txtNombre = new MFXTextField();
        txtNombre.setFloatingText("Nombre");
        txtNombre.setTextFormatter(Formato.getInstance().letrasFormat(30));
        txtNombre.setPrefWidth(Double.MAX_VALUE);

        txtApellido = new MFXTextField();
        txtApellido.setFloatingText("Apellido");
        txtApellido.setTextFormatter(Formato.getInstance().letrasFormat(30));
        txtApellido.setPrefWidth(Double.MAX_VALUE);

        root.getStylesheets().add("/view/Style.css");

        vboxParametros.getChildren().clear();
        vboxParametros.getChildren().add(txtCedula);
        vboxParametros.getChildren().add(txtNombre);
        vboxParametros.getChildren().add(txtApellido);

        tblvResultados.getItems().clear();
        tblvResultados.getColumns().clear();
        TableColumn<EmpleadoDto, String> tbcCedula = new TableColumn<>("Cedula");
        tbcCedula.setPrefWidth(100);
        tbcCedula.setCellValueFactory(cd -> cd.getValue().cedula);
        TableColumn<EmpleadoDto, String> tbcNombre = new TableColumn<>("Nombre");
        tbcNombre.setPrefWidth(200);
        tbcNombre.setCellValueFactory(cd -> cd.getValue().nombre);
        TableColumn<EmpleadoDto, String> tbcApellido = new TableColumn<>("Apellido");
        tbcApellido.setPrefWidth(200);
        tbcApellido.setCellValueFactory(cd -> cd.getValue().primerApellido);

        tblvResultados.getColumns().add(tbcCedula);
        tblvResultados.getColumns().add(tbcNombre);
        tblvResultados.getColumns().add(tbcApellido);
        tblvResultados.refresh();
    }

    public Object getSeleccionado() {
        return seleccionado;
    }
    
}
