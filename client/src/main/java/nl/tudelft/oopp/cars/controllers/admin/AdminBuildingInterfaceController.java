package nl.tudelft.oopp.cars.controllers.admin;

import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import nl.tudelft.oopp.cars.communication.CurrentPage;
import nl.tudelft.oopp.cars.communication.ServerEntityCommunication;
import nl.tudelft.oopp.cars.controllers.MainController;
import nl.tudelft.oopp.cars.logic.BuildingLogic;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.shared.responses.content.BuildingsResponse;

public class AdminBuildingInterfaceController {


    @FXML private HBox adminBuilding;

    @FXML private ListView<String> listView;
    @FXML private ChoiceBox<String> ordering;
    @FXML private TextField id;
    @FXML private TextField name;
    @FXML private TextField address;
    @FXML private TextField faculty;
    @FXML private Button addBuildingButton;
    @FXML private Button removeBuildingButton;

    @FXML private Label feedbackAddMessage;
    @FXML private Label feedbackRemoveMessage;

    @FXML MainController main;

    BuildingsResponse buildings;
    private ObservableList<String> list = FXCollections.observableArrayList();


    /**
     * Called to establish a connection with the mainController.
     * @param mainController the mainController calling this method
     */
    public void init(MainController mainController) {
        System.out.println("AdminBuildingController connected to the MainController.");
        main = mainController;
        CurrentPage.cls = this.getClass();
        CurrentPage.page = this.adminBuilding;
        CurrentPage.onePage = true;

    }

    /**
     * Automatically called when the page is loaded.
     */
    @FXML public void initialize() throws IOException {
        buildings = ServerEntityCommunication.getAllBuildings();

        list.clear();
        BuildingLogic.sortingBuildingOption(buildings, 2, 1);
        BuildingLogic.parseAdminResponse(list, buildings);
        listView.setItems(list);

        ordering.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!(oldValue.equals(newValue))) {
                        list.clear();
                        int[] order = GeneralLogic.parseOrder(ordering.getSelectionModel()
                                .getSelectedItem());
                        BuildingLogic.sortingBuildingOption(buildings, order[0], order[1]);
                        BuildingLogic.parseAdminResponse(list, buildings);
                    }
                });

        id.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) {
                id.setText(oldValue);
            }
        });
    }

    /**
     * Called when the Add Building button is clicked, adds a building to the database.
     */
    public void onAddBuildingClicked() throws IOException {

        this.id.textProperty().addListener((observable, oldValue, newValue) -> {
            addBuildingButton.setTextFill(Color.BLACK);
            addBuildingButton.setText("Add Building");
            if (!newValue.equals("0000")) {
                this.id.getStyleClass().add("blackText2");
                if (oldValue.equals("0000")) {
                    Platform.runLater(() -> {
                        this.id.setText("");
                    });
                }
            } else {
                this.id.getStyleClass().add("negativeMessage");
            }
        });

        this.name.textProperty().addListener((observable, oldValue, newValue) -> {
            addBuildingButton.setTextFill(Color.BLACK);
            addBuildingButton.setText("Add Building");
            if (!newValue.equals("field is empty. not saved")) {
                this.name.getStyleClass().add("blackText2");
                if (oldValue.equals("field is empty. not saved")) {
                    Platform.runLater(() -> {
                        this.name.setText("");
                    });
                }
            } else {
                this.name.getStyleClass().add("negativeMessage");
            }
        });

        this.address.textProperty().addListener((observable, oldValue, newValue) -> {
            addBuildingButton.setTextFill(Color.BLACK);
            addBuildingButton.setText("Add Building");
            if (!newValue.equals("field is empty. not saved")) {
                this.address.getStyleClass().add("blackText2");
                if (oldValue.equals("field is empty. not saved")) {
                    Platform.runLater(() -> {
                        this.address.setText("");
                    });
                }
            } else {
                this.address.getStyleClass().add("negativeMessage");
            }
        });
        this.faculty.textProperty().addListener((observable, oldValue, newValue) -> {
            addBuildingButton.setTextFill(Color.BLACK);
            addBuildingButton.setText("Add Building");
            if (!newValue.equals("field is empty. not saved")) {
                this.faculty.getStyleClass().add("blackText2");
                if (oldValue.equals("field is empty. not saved")) {
                    Platform.runLater(() -> {
                        this.faculty.setText("");
                    });
                }

            } else {
                this.faculty.getStyleClass().add("negativeMessage");
            }
        });

        String idInString = this.id.getText();
        String name = this.name.getText();
        String address = this.address.getText();
        String faculty = this.faculty.getText();
        String[] setTestMsg = BuildingLogic.checkEmptyField(idInString,name, address, faculty);

        this.id.setText(setTestMsg[0]);
        this.name.setText(setTestMsg[1]);
        this.address.setText(setTestMsg[2]);
        this.faculty.setText(setTestMsg[3]);

        if (!setTestMsg[4].equals("false")) {
            int id = Integer.parseInt(idInString);
            ServerEntityCommunication.addBuilding(id, name, address, faculty);

            buildings = ServerEntityCommunication.getAllBuildings();

            list.clear();
            BuildingLogic.sortingBuildingOption(buildings, 2, 1);
            BuildingLogic.parseAdminResponse(list, buildings);
            listView.setItems(list);

            feedbackAddMessage.getStyleClass().clear();
            feedbackAddMessage.getStyleClass().add("positiveMessage");
            feedbackAddMessage.setText("saved");
        }

    }

    /**
     * Called when the Remove Building button is clicked, removes a building from the database.
     */
    public void onRemoveBuildingClicked() throws IOException {
        String buildingToDelete = listView.getSelectionModel().getSelectedItem();
        listView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (oldValue.equals(buildingToDelete)) {
                        Platform.runLater(() -> {
                            feedbackRemoveMessage.getStyleClass().clear();
                            feedbackRemoveMessage.getStyleClass().add("positiveMessage");
                            feedbackRemoveMessage.setText("removed successfully");
                        });
                    }
                });

        if (buildingToDelete != null && buildingToDelete.length() > 0) {
            int idToDelete = BuildingLogic.getBuildingIdToDelete(buildingToDelete);
            ServerEntityCommunication.removeBuilding(idToDelete);


            listView.getItems().remove(buildingToDelete);
        }
    }
}
