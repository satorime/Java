package com.example.csit228f2_2;

import com.example.csit228f2_2.Server.DatabaseManager;
import com.example.csit228f2_2.Server.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LogInController {
    @FXML
    private TextField loginUsernameField;

    @FXML
    private TextField loginPasswordField;

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        Status res = validateCredentials();
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Login Status");

        if(res == Status.LOGIN_SUCCESS){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("homepage-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Home");
            stage.setScene(new Scene(root));

            Stage currStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            HomePageController homeController = loader.getController();
            homeController.setPageValues();

            alert.setAlertType(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Login successful!");
            alert.setHeaderText(null);
            alert.showAndWait();
            stage.show();
            currStage.close();
        }else if(res == Status.INCORRECT_PASSWORD){
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Incorrect password. Please try again.");
            alert.setHeaderText(null);
            alert.showAndWait();
        }else{
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Username not found in the database. Please enter valid credentials");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    private Status validateCredentials(){
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();
        return DatabaseManager.getInstance().validate(username, password);
    }

    @FXML
    private void handleRegister(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("register-view.fxml")));
        Stage currStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currStage.setTitle("Register");
        currStage.setScene(new Scene(root));
        currStage.show();
    }


}