package view;

import controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.AccionInterna;
import model.OrigenMovimiento;
import model.Producto;
import model.Usuario;

public class MainView extends Application {

    private final MainController controller = new MainController();

    @Override
    public void start(Stage stage) {

        Usuario usuarioLogueado = mostrarLogin();
        if (usuarioLogueado == null) {
            Platform.exit();
            return;
        }
        controller.setUsuarioActual(usuarioLogueado);

        Button btnIngreso = new Button("Registrar INGRESO");
        Button btnEntrega = new Button("Registrar ENTREGA");
        Button btnStock   = new Button("Ver STOCK");
        Button btnMovsBD  = new Button("Ver MOVIMIENTOS (BD)");

        btnIngreso.setOnAction(e -> registrarIngreso());
        btnEntrega.setOnAction(e -> registrarEntrega());
        btnStock.setOnAction(e -> verStock());
        btnMovsBD.setOnAction(e -> verMovimientosBD());

        Label lblTitulo  = new Label("SGCC - Prototipo");
        Label lblUsuario = new Label("Usuario actual: " + usuarioLogueado.toString());

        VBox root = new VBox(
                12,
                lblTitulo,
                lblUsuario,
                btnIngreso,
                btnEntrega,
                btnStock,
                btnMovsBD
        );
        root.setPadding(new Insets(16));

        stage.setScene(new Scene(root, 450, 260));
        stage.setTitle("SGCC - Prototipo");
        stage.show();
    }

    private Usuario mostrarLogin() {
        var usuarios = controller.getUsuarios();
        if (usuarios.isEmpty()) {
            mostrarError("No hay usuarios configurados.");
            return null;
        }

        ChoiceDialog<Usuario> dlg = new ChoiceDialog<>(usuarios.get(0), usuarios);
        dlg.setTitle("Login");
        dlg.setHeaderText("Seleccione un usuario para iniciar sesión");
        dlg.setContentText("Usuario:");

        var r = dlg.showAndWait();
        return r.orElse(null);
    }

    private Long elegirProducto() {
        var productos = controller.getProductos();
        if (productos.isEmpty()) {
            mostrarError("No hay productos configurados.");
            return null;
        }

        ChoiceDialog<Producto> dlg =
                new ChoiceDialog<>(productos.get(0), productos);

        dlg.setTitle("Seleccionar producto");
        dlg.setHeaderText("Seleccione un producto");
        dlg.setContentText("Producto:");

        var r = dlg.showAndWait();
        return r.map(Producto::getId).orElse(null);
    }

    private void registrarIngreso() {
        ChoiceDialog<OrigenMovimiento> dlg =
                new ChoiceDialog<>(OrigenMovimiento.DONACION, OrigenMovimiento.values());
        dlg.setHeaderText("Seleccione origen del ingreso");
        var origen = dlg.showAndWait();
        if (origen.isEmpty()) return;

        Long prodId = elegirProducto();
        if (prodId == null) return;

        Double cantidad = pedirDouble("Cantidad a ingresar");
        if (cantidad == null) return;

        String resultado = controller.registrarIngreso(
                origen.get(),
                (origen.get() == OrigenMovimiento.ACCION_INTERNA ? AccionInterna.AJUSTE : null),
                prodId,
                cantidad
        );
        mostrarInfo(resultado);
    }

    private void registrarEntrega() {
        Long prodId = elegirProducto();
        if (prodId == null) return;

        Double cantidad = pedirDouble("Cantidad a entregar");
        if (cantidad == null) return;

        String resultado = controller.registrarEntrega(prodId, cantidad);
        mostrarInfo(resultado);
    }

    private void verStock() {
        String stock = controller.obtenerStock();
        mostrarInfo(stock);
    }

    private void verMovimientosBD() {
        String texto = controller.obtenerMovimientosBD();
        mostrarInfo(texto);
    }

    private Double pedirDouble(String prompt) {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setHeaderText(prompt);
        var r = dlg.showAndWait();
        if (r.isEmpty()) return null;
        try {
            return Double.parseDouble(r.get().trim());
        } catch (NumberFormatException ex) {
            mostrarError("Número inválido");
            return null;
        }
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

    public static void main(String[] args) {
        launch(args);
    }
}
