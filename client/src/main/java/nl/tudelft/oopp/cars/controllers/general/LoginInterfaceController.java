package nl.tudelft.oopp.cars.controllers.general;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nl.tudelft.oopp.cars.communication.ServerUserCommunication;

public class LoginInterfaceController {

    @FXML private AnchorPane loginPage;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private Label loginError;

    /**
     * Called when the Login button is clicked, loads the home screen after validating the account.
     * @throws IOException called when it fails to load the home screen interface fxml
     */
    public void onLoginButtonClicked() throws IOException {
        String email = this.email.getText();
        String password = this.password.getText();

        String verify = ServerUserCommunication.validateLogin(email, password.hashCode());

        if (verify.equals("SUCCESS")) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/main.fxml"));
            Parent root = loader.load();

            Stage main = new Stage();
            main.setTitle("Campus Administration & Registration System");
            main.setScene(new Scene(root));
            main.show();

            Stage stage = (Stage) loginPage.getScene().getWindow();
            stage.close();
        } else {
            loginError.setText("Please enter a valid combination of user and password");
        }
    }

    /**
     * Called when the Register button is clicked, loads the register page.
     * @throws IOException called when it fails to load the register interface fxml
     */
    public void onRegisterButtonClicked() throws IOException {
        URL xmlUrl = getClass().getResource("/general/registerInterface.fxml");
        AnchorPane pane = FXMLLoader.load(xmlUrl);
        loginPage.getChildren().setAll(pane);
    }
}