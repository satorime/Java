package com.example.csit228f2_2;

import com.example.csit228f2_2.Server.DatabaseManager;
import com.example.csit228f2_2.Server.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class EditTaskController {
    @FXML
    private TextField editTaskTitleField;
    @FXML
    private TextArea editTaskContentField;
    private int taskID;
    private TaskCardController prevController;

    public void setTaskID(int taskID){
        this.taskID = taskID;
    }

    public void setPrevController(TaskCardController controller){
        prevController = controller;
    }

    @FXML
    private void handleSubmitEditTask(ActionEvent event) throws IOException {
        String newTitle = editTaskTitleField.getText();
        String newContent = editTaskContentField.getText();
        DatabaseManager dbManager = DatabaseManager.getInstance();
        Status res = dbManager.editTask(newTitle, newContent, taskID, prevController);
        Alert alert = new Alert(Alert.AlertType.NONE);

        if(res == Status.TASK_EDITED_SUCCESSFULLY){
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setContentText("Task has been edited.");
            alert.setHeaderText(null);
        }else{
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Task edit failed.");
            alert.setHeaderText(null);
        }

        alert.showAndWait();
        Stage currStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("homepage-view.fxml"));
        loader.load();
        currStage.close();
    }

}