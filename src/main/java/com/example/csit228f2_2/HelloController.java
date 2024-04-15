package com.example.csit228f2_2;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class HelloController {
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Button btnUpdate;
    @FXML
    private TextField tfnewUsername;
    @FXML
    private PasswordField pfnewPassword;
    @FXML
    private Text actionTarget;
    @FXML
    private Button btnDelete;

    @FXML
    private void initialize() {
        btnUpdate.setOnAction(event -> {
            String username = tfUsername.getText();
            String password = pfPassword.getText();

            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try (Connection c = MySQLConnection.getConnection()) {
                statement = c.prepareStatement("SELECT * FROM bitayodb WHERE username = ?");
                statement.setString(1, username);
                resultSet = statement.executeQuery();

                if (!resultSet.next()) {
                    actionTarget.setText("The username does not exist.");
                    actionTarget.setOpacity(1);
                    return;
                }

                if (!resultSet.getString("password").equals(password)) {
                    actionTarget.setText("The password is incorrect.");
                    actionTarget.setOpacity(1);
                    return;
                }

                statement = c.prepareStatement("UPDATE bitayodb SET username = ?, password = ? WHERE username = ?");
                statement.setString(1, tfnewUsername.getText());
                statement.setString(2, pfnewPassword.getText());
                statement.setString(3, username);
                statement.executeUpdate();

                actionTarget.setText("The username and password have been updated.");
                actionTarget.setOpacity(1);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        btnDelete.setOnAction(event -> {
            String username = tfUsername.getText();
            String password = pfPassword.getText();

            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try (Connection c = MySQLConnection.getConnection()) {
                statement = c.prepareStatement("SELECT * FROM bitayodb WHERE username = ?");
                statement.setString(1, username);
                resultSet = statement.executeQuery();

                if (!resultSet.next()) {
                    actionTarget.setText("The username does not exist.");
                    actionTarget.setOpacity(1);
                    return;
                }

                if (!resultSet.getString("password").equals(password)) {
                    actionTarget.setText("The password is incorrect.");
                    actionTarget.setOpacity(1);
                    return;
                }

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Are you sure you want to delete your account?");
                alert.setContentText("This action cannot be undone.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    statement = c.prepareStatement("DELETE FROM bitayodb WHERE username = ?");
                    statement.setString(1, username);
                    statement.executeUpdate();

                    actionTarget.setText("Your account has been deleted.");
                    actionTarget.setOpacity(1);

                    HelloApplication app = new HelloApplication();
                    app.start((Stage) btnDelete.getScene().getWindow());
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}