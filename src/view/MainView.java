package view;

import controller.MainController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.AccionInterna;
import model.OrigenMovimiento;

import java.util.Optional;

public class MainView extends Application {

    private final MainController controller = new MainController();

    @Override
    public void start(Stage stage) {
        Button btnIngreso = new Button("Registrar INGRESO");
        Button btnEntrega = new Button("Registrar ENTREGA");
        Button btnStock = new Button("Ver STOCK");

        btnIngreso.setOnAction(e -> registrarIngreso());
        btnEntrega.setOnAction(e -> registrarEntrega());
        btnStock.setOnAction(e -> verStock());

        VBox root = new VBox(12, new Label("SGCC - Prototipo"), btnIngreso, btnEntrega, btnStock);
        root.setPadding(new Insets(16));

        stage.setScene(new Scene(root, 420, 220));
        stage.setTitle("SGCC - Prototipo");
        stage.show();
    }

    private void registrarIngreso() {
        ChoiceDialog<OrigenMovimiento> dlg = new ChoiceDialog<>(OrigenMovimiento.DONACION, OrigenMovimiento.values());
        dlg.setHeaderText("Seleccione origen del ingreso");
        Optional<OrigenMovimiento> origen = dlg.showAndWait();
        if (origen.isEmpty()) return;

        Long prodId = pedirLong("ID de producto (1-5)");
        if (prodId == null) return;

        Double cantidad = pedirDouble("Cantidad a ingresar");
        if (cantidad == null) return;

        String resultado = controller.registrarIngreso(1L, origen.get(),
                (origen.get() == OrigenMovimiento.ACCION_INTERNA ? AccionInterna.AJUSTE : null),
                prodId, cantidad);
        mostrarInfo(resultado);
    }

    private void registrarEntrega() {
        Long prodId = pedirLong("ID de producto (1-5)");
        if (prodId == null) return;
        Double cantidad = pedirDouble("Cantidad a entregar");
        if (cantidad == null) return;

        String resultado = controller.registrarEntrega(2L, 10L, prodId, cantidad);
        mostrarInfo(resultado);
    }

    private void verStock() {
        String stock = controller.obtenerStock();
        mostrarInfo(stock);
    }

    private Long pedirLong(String prompt) {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setHeaderText(prompt);
        var r = dlg.showAndWait();
        if (r.isEmpty()) return null;
        try { return Long.parseLong(r.get().trim()); }
        catch (NumberFormatException ex) { mostrarError("Número inválido"); return null; }
    }

    private Double pedirDouble(String prompt) {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setHeaderText(prompt);
        var r = dlg.showAndWait();
        if (r.isEmpty()) return null;
        try { return Double.parseDouble(r.get().trim()); }
        catch (NumberFormatException ex) { mostrarError("Número inválido"); return null; }
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Atención");
        a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}
