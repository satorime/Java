package com.example.csit228f2_2;

import com.example.csit228f2_2.Server.DatabaseManager;
import com.example.csit228f2_2.Server.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomePageController {
    @FXML
    private Label currentUserUName;
    @FXML
    private Label currentUserEmail;
    @FXML
    private VBox tasksContainer;
    @FXML
    private Button createTaskButton;
    @FXML
    private TextField taskCardTitleField;
    @FXML
    private TextArea taskCardContentField;

    public void setPageValues() {
        currentUserUName.setText(CurrentUser.username);
        currentUserEmail.setText(CurrentUser.email);

        try{
            displayUserTasks();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void displayUserTasks() throws IOException {
        tasksContainer.getChildren().clear();

        List<Map<String, String>> tasks = DatabaseManager.getInstance().getTasks();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);

        for(int i = tasks.size() - 1; i >= 0; i--){
            FXMLLoader taskLoader = new FXMLLoader(getClass().getResource("task-card.fxml"));
            VBox taskCard = taskLoader.load();

            TaskCardController taskController = taskLoader.getController();

            String taskTitle = tasks.get(i).get("task_title");
            String taskContent = tasks.get(i).get("task_content");
            String taskID = tasks.get(i).get("task_id");

            taskController.setTaskCardValues(taskTitle, taskContent, Integer.parseInt(taskID));

            tasksContainer.getChildren().add(taskCard);
        }
    }

    @FXML
    private void createTask() throws IOException {
        String title = taskCardTitleField.getText();
        String content = taskCardContentField.getText();
        DatabaseManager dbManager = DatabaseManager.getInstance();
        Status res = dbManager.createTask(title, content, CurrentUser.userID);

        Alert alert = new Alert(Alert.AlertType.NONE);

        if(res == Status.TASK_CREATED_SUCCESSFULLY){
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setContentText("Task created successfully!");
        }else{
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Task creation unsuccessful. Please try again.");
        }

        alert.setHeaderText(null);
        alert.showAndWait();
        displayUserTasks();
    }

    @FXML
    private void handleEditProfile(ActionEvent event) throws IOException {
        Stage currStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        EditProfileController.setPrevStage(currStage);
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("edit-profile-view.fxml")));
        currStage.setTitle("Log In");
        currStage.setScene(new Scene(root));
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login-view.fxml")));
        Stage stage = new Stage(); // Create a new stage
        stage.setTitle("Log In");
        stage.setScene(new Scene(root));

        //set the current user to empty
        CurrentUser.userID = -1;
        CurrentUser.username = "";
        CurrentUser.email = "";

        Stage currStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.show();
        currStage.close(); //close the home page;
    }
}