package project;
/**
 * Jude Lieb
 * Chess Game Interface and Engine
 * Version 16 
 * Project Start Date: 06/2022
 * 
 * Latest Major Changes: 
 * 1. Move Stack now implemented to allow move undoing.
 * 2. En Passant case now included.
 * 3. Underpromotion toggling is now included.
 * 
 */

/**
 * App Class
 * Initializes parts of the JavaFX interface
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

public class App extends Application { 
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("main"));
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
    	FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/project/main.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws IOException{
        launch();
	}
}