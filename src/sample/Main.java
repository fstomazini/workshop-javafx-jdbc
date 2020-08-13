package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("\\\\gui\\\\MainView.fxml"));
        ScrollPane parent = loader.load();
        parent.setFitToHeight(true);
        parent.setFitToWidth(true);
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sample JavaFX Application");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
