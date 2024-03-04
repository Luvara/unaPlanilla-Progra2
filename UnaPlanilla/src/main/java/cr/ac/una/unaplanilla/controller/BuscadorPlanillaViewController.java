package cr.ac.una.unaplanilla.controller;

import cr.ac.una.unaplanilla.model.EmpleadoDto;
import cr.ac.una.unaplanilla.model.TipoPlanillaDto;
import cr.ac.una.unaplanilla.service.EmpleadoService;
import cr.ac.una.unaplanilla.service.TipoPlanillaService;
import cr.ac.una.unaplanilla.util.FlowController;
import cr.ac.una.unaplanilla.util.Formato;
import cr.ac.una.unaplanilla.util.Mensaje;
import cr.ac.una.unaplanilla.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author UNA
 */
public class BuscadorPlanillaViewController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private VBox vboxParametros;
    @FXML
    private MFXButton btnFiltrar;
    @FXML
    private TableView<TipoPlanillaDto> tblvResultados;
    @FXML
    private MFXButton btnAceptar;

    MFXTextField txtCodigo;
    MFXTextField txtDescripcion;
    MFXTextField txtPlaxmes;
    MFXTextField txtCedula;
    MFXTextField txtNombre;
    EmpleadoDto empleadoDto;

    private Object seleccionado;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void initialize() {
        anadirSlots();
        this.empleadoDto = new EmpleadoDto();
    }

    @FXML
    private void OnActionBtnFiltrar(ActionEvent event) {
        tblvResultados.getItems().clear();
        TipoPlanillaService service = new TipoPlanillaService();
        String planilla = "%" + txtPlaxmes.getText() + "%";
        String codigo = "%" + txtCodigo.getText() + "%";
        String descripcion = "%" + txtDescripcion.getText() + "%";
        String cedula = "%" + txtCedula.getText() + "%";
        Respuesta respuesta;
        
        respuesta = service.getBusqueda(planilla, codigo, descripcion);
        
        if (txtCedula.getText() != null) {
            respuesta = service.getTipoPlanillasPorCedula(cedula);
        }

        if (respuesta.getEstado()) {
            List<TipoPlanillaDto> tipoPlanillas = (List<TipoPlanillaDto>) respuesta.getResultado("TipoPlanilla");
            ObservableList<TipoPlanillaDto> tipoPlanillasObservable = FXCollections.observableArrayList(tipoPlanillas);
            tblvResultados.setItems(tipoPlanillasObservable);
            tblvResultados.refresh();
        } else {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Consultar Planilla", getStage(), respuesta.getMensaje());
        }
    }

    @FXML
    private void OnMouseClickedTabla(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (event.getClickCount() == 2) {
                seleccionado = tblvResultados.getSelectionModel().getSelectedItem();
                TiposPlanillasViewController tiposPlanilla = (TiposPlanillasViewController) FlowController.getInstance().getController("TiposPlanillasView");
                tiposPlanilla.bindBuscar();
                getStage().close();
            }
        }
    }

    @FXML
    private void OnActionBtnAceptar(ActionEvent event) {
        seleccionado = tblvResultados.getSelectionModel().getSelectedItem();
        TiposPlanillasViewController tiposPlanilla = (TiposPlanillasViewController) FlowController.getInstance().getController("TiposPlanillasView");
        tiposPlanilla.bindBuscar();
        getStage().close();
    }

    public Object getSeleccionado() {
        return seleccionado;
    }

    public void anadirSlots() {
        vboxParametros.setSpacing(10);

        txtCodigo = new MFXTextField();
        txtCodigo.setFloatingText("Código");
        txtCodigo.setTextFormatter(Formato.getInstance().maxLengthFormat(4));
        txtCodigo.setPrefWidth(Double.MAX_VALUE);

        txtDescripcion = new MFXTextField();
        txtDescripcion.setFloatingText("Descripción");
        txtDescripcion.setTextFormatter(Formato.getInstance().letrasFormat(30));
        txtDescripcion.setPrefWidth(Double.MAX_VALUE);

        txtPlaxmes = new MFXTextField();
        txtPlaxmes.setFloatingText("Plan por Mes");
        txtPlaxmes.setTextFormatter(Formato.getInstance().integerFormat());
        txtPlaxmes.setPrefWidth(Double.MAX_VALUE);

        txtCedula = new MFXTextField();
        txtCedula.setFloatingText("Cédula");
        txtCedula.setTextFormatter(Formato.getInstance().cedulaFormat(40));
        txtCedula.setPrefWidth(Double.MAX_VALUE);
        txtCedula.addEventFilter(KeyEvent.KEY_PRESSED, (var e) -> {
            if (e.getCode().equals(ENTER) && !txtCedula.getText().isBlank()) {
                cargarEmpleado();
            }
        });

        MFXButton verificar = new MFXButton("Verificar Cedula");
        verificar.setOnAction(event -> {
            cargarEmpleado();
        });

        txtNombre = new MFXTextField();
        txtNombre.setFloatingText("Nombre");
        txtNombre.setEditable(false);
        txtNombre.setTextFormatter(Formato.getInstance().letrasFormat(40));
        txtNombre.setPrefWidth(Double.MAX_VALUE);

        root.getStylesheets().add("/view/Style.css");

        vboxParametros.getChildren().clear();
        vboxParametros.getChildren().addAll(txtCodigo, txtDescripcion, txtPlaxmes, txtCedula, verificar, txtNombre);

        tblvResultados.getItems().clear();
        tblvResultados.getColumns().clear();

        TableColumn<TipoPlanillaDto, String> tbcCodigo = new TableColumn<>("Codigo");
        tbcCodigo.setPrefWidth(100);
        tbcCodigo.setCellValueFactory(cd -> cd.getValue().codigo);
        TableColumn<TipoPlanillaDto, String> tbcDescripcion = new TableColumn<>("Descripcion");
        tbcDescripcion.setPrefWidth(200);
        tbcDescripcion.setCellValueFactory(cd -> cd.getValue().descripcion);
        TableColumn<TipoPlanillaDto, String> tbcPlaxmes = new TableColumn<>("Plan x mes");
        tbcPlaxmes.setPrefWidth(200);
        tbcPlaxmes.setCellValueFactory(cd -> cd.getValue().planillaPorMes);

        tblvResultados.getColumns().add(tbcCodigo);
        tblvResultados.getColumns().add(tbcDescripcion);
        tblvResultados.getColumns().add(tbcPlaxmes);
        tblvResultados.refresh();
    }

    private void bindEmpleado(Boolean nuevo) {

        txtCedula.textProperty().bindBidirectional(this.empleadoDto.cedula);
        txtNombre.textProperty().bindBidirectional(this.empleadoDto.nombre);
    }

    private void unbindEmpleado() {
        txtCedula.textProperty().unbindBidirectional(this.empleadoDto.cedula);
        txtNombre.textProperty().unbindBidirectional(this.empleadoDto.nombre);
    }

    public void cargarEmpleado() {
        String cedula = "%" + txtCedula.getText() + "%";
        try {
            EmpleadoService service = new EmpleadoService();
            Respuesta respuesta = service.getCedula(cedula);
            if (respuesta.getEstado()) {
                unbindEmpleado();
                this.empleadoDto = (EmpleadoDto) respuesta.getResultado("Empleado");
                bindEmpleado(false);
            } else {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar empleado", getStage(), respuesta.getMensaje());
            }
        } catch (Exception ex) {
            Logger.getLogger(EmpleadosViewController.class.getName()).log(Level.SEVERE, "Error consultando el empleado.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Empleado", getStage(), "Ocurrio un error consultando el empleado.");
        }
    }
}
