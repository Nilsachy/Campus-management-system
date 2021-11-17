package nl.tudelft.oopp.cars.controllers.general;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import nl.tudelft.oopp.cars.communication.ServerUserCommunication;

public class RegisterInterfaceController {

    @FXML private AnchorPane registerPage;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private ChoiceBox<String> role;
    @FXML private Label registerError;

    /**
     * Called when the Login link is clicked, loads the login page.
     * @throws IOException called when it fails to load the login interface fxml
     */
    public void onBackToLoginButtonClicked() throws IOException {
        loadView("/general/loginInterface.fxml", "Login");
    }

    /**
     * Called when the Register button is clicked, sends information to the server.
     * So you can be added as a user.
     * @throws IOException called when it fails to load the login interface fxml
     */
    public void onRegisterButtonClicked() throws IOException {

        //TODO: Send verification request to server and IFF it is valid,
        // save it in the database and confirm to the user

        //TODO: Let the server handle email/student status verification,
        // you would be able to trick the system otherwise

        //TODO: Send user back to login page so they can login with their new account

        String email = this.email.getText();
        String password = this.password.getText();
        String role = this.role.getValue().toString();

        String verify = ServerUserCommunication.addUser(email, password.hashCode());

        // Just a small client-side test, will be deleted later
        if ((role.equals("student") && email.contains("student"))
                || role.equals("employee") && !email.contains("student")) {
            Alert testAlert = new Alert(Alert.AlertType.INFORMATION);
            testAlert.setTitle("Correct Registration");
            testAlert.setHeaderText("Correct registration:" + "\n"
                    + "email: " + email + "\n"
                    + "password: " + password + "\n"
                    + "role: " + role);
            testAlert.show();
            onBackToLoginButtonClicked();
        } else {
            registerError.setText("Register gone wrong");
        }
    }

    /**
     * Loads new scene in same window based on String: url.
     */
    public void loadView(String url, String title) throws IOException {
        URL xmlUrl = getClass().getResource(url);
        AnchorPane pane = FXMLLoader.load(xmlUrl);
        registerPage.getChildren().setAll(pane);
    }
}
