package com.example.csit228f2_2;

import com.example.csit228f2_2.Server.DatabaseManager;
import com.example.csit228f2_2.Server.Status;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class TaskCardController {
    @FXML
    private Label taskCardTitle;
    @FXML
    private Label taskCardContent;

    private int taskID;
    public void setTaskCardValues(String title, String content, int taskID){
        taskCardTitle.setText(title);
        taskCardContent.setText(content);
        this.taskID = taskID;
    }

    @FXML
    private void handleDeleteTask(ActionEvent event) throws IOException {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        Status res = dbManager.deleteTask(taskID);
        Alert alert = new Alert(Alert.AlertType.NONE);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("homepage-view.fxml"));
        Parent root = loader.load(); // load a resource so that the loader will not be null
        HomePageController homeController = loader.getController();

        if(res == Status.TASK_DELETED_SUCCESSFULLY){
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setContentText("Task has been deleted.");
            alert.setHeaderText(null);

            Button deleteButton = (Button) event.getSource();
            HBox buttonContainer = (HBox) deleteButton.getParent();
            VBox taskCard = (VBox) buttonContainer.getParent();
            VBox parentContainer = (VBox) taskCard.getParent();

            if (parentContainer != null) {
                parentContainer.getChildren().remove(taskCard);
            }

            alert.showAndWait();
        }else{
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Task deletion failed.");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }
    @FXML
    private void handleEditTask(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-task-view.fxml"));
        Parent root = loader.load();
        EditTaskController controller = loader.getController();

        TaskCardController cardController = this;

        controller.setTaskID(taskID);
        controller.setPrevController(cardController);

        Stage stage = new Stage();
        stage.setTitle("Edit Task");
        stage.setScene(new Scene(root));
        stage.show();
    }

}