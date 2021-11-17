package nl.tudelft.oopp.cars.views;

import com.sun.javafx.css.StyleManager;
import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginDisplay extends Application {

    /**
     * Loaded when the application starts.
     * @param primaryStage the stage to show when the application starts
     * @throws IOException called when it fails to load the login interface fxml
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet("/stylesheets/buttons.css");
        StyleManager.getInstance().addUserAgentStylesheet("/stylesheets/general.css");

        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/general/loginInterface.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * Entry point of the JavaFX Application.
     * @param args passed to JavaFX, but most likely not used.
     */
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        launch(args);
    }
}

