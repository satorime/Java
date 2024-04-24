package com.example.csit228f2_2;

import com.example.csit228f2_2.Server.DatabaseManager;
import com.example.csit228f2_2.Server.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class EditProfileController {
    private static Stage prevStage;

    @FXML
    private Button editUsernameButton;
    @FXML
    private Button editPasswordButton;
    @FXML
    private Button editEmailButton;
    @FXML
    private TextField editEmailField;
    @FXML
    private TextField editUsernameField;
    @FXML
    private TextField editPasswordField;
    @FXML
    private Button deleteUserButton;

    public static void setPrevStage(Stage stage){
        prevStage = stage;
    }

    @FXML
    private void handleDeleteUser(ActionEvent event) throws IOException {
        Status res = DatabaseManager.getInstance().deleteUser(CurrentUser.userID);

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Login Status");

        if(res == Status.ACCOUNT_DELETED_SUCCESSFULLY){
            alert.setAlertType(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Account deleted successfully.");
            alert.setHeaderText(null);
            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage(); // Create a new stage
            stage.setTitle("Home");
            stage.setScene(new Scene(root));

            prevStage.close(); //close the last stage
            stage.show();
        }else{
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Account deletion failed. Please try again.");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    @FXML
    private void handleReturnToHomePage() throws IOException {
        assert prevStage != null;
        Stage currStage = prevStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("homepage-view.fxml"));
        Parent root = loader.load();

        HomePageController homeController = loader.getController();

        homeController.setPageValues();

        currStage.setTitle("Home");
        currStage.setScene(new Scene(root));
    }

    @FXML
    private void handleEditUserData(ActionEvent event){
        Button eventSource = (Button) event.getSource();
        String field = (String) eventSource.getUserData();
        Status res = null;

        Alert alert = new Alert(Alert.AlertType.NONE);
        String updatedField = "";

        switch (field) {
            case "username" -> {
                res = processUpdate(field, editUsernameField.getText());
                updatedField = "Username";
            }

            case "email" -> {
                res = processUpdate(field, editEmailField.getText());
                updatedField = "Email";
            }

            case "password" -> {
                res = processUpdate(field, editPasswordField.getText());
                updatedField = "Password";
            }
        }

        assert res != null;

        if(res == Status.ACCOUNT_UPDATED_SUCCESSFULLY){
            alert.setAlertType(Alert.AlertType.CONFIRMATION);
            alert.setContentText(updatedField + " updated successfully.");
        }else{
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Error in updating field: " + updatedField);
        }

        alert.setHeaderText(null);
        alert.showAndWait();

    }

    private Status processUpdate(String field, String value){
        return DatabaseManager.getInstance().updateUser(field, value, CurrentUser.userID);
    }
}