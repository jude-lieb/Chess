package project;
/**
 * Jude Lieb
 * Chess Game Interface and Engine
 * Version 15 
 * Start Date: 06/2022
 * Version Completion Date: 07/20/2024
 * 
 * Latest Major Changes: 
 * 1. Castling now functional for the player. This required the addition of status 
 * variables to track when the kings and rooks first moved.
 * 2. The Game Class has been removed and its functions given to other classes.
 * 3. When constructing Crd objects, the coordinates are now taken in y, x order to maintain consistency.
 */

/**
 * App Class
 * Initializes parts of the JavaFX interface
 * This class is NOT original; I used this base code from a school assignment
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
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        scene = new Scene(loadFXML("main"));
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws IOException{
        launch();
	}
}