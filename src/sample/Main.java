package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            ViewLoader<BorderPane, MainController> viewLoaderMain = new ViewLoader<>("main.fxml");
            BorderPane borderPane = viewLoaderMain.getLayout();
            MainController mainController = viewLoaderMain.getController();

            Scene scene = new Scene(borderPane);
            mainController.setStage(primaryStage);

            primaryStage.setTitle("Hello Turtle");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest(this::stage_CloseRequest);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void stage_CloseRequest(WindowEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Kończenie pracy");
        alert.setTitle("( ͡° ͜ʖ ͡°)");
        alert.setContentText("Czy chcesz zamknąć aplikację?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(ButtonType.CANCEL) != ButtonType.OK)
            e.consume();
    }

    public static void main(String[] args) {
        launch(args);

    }
}
