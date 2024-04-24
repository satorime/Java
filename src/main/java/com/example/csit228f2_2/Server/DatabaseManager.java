package com.example.csit228f2_2.Server;

import com.example.csit228f2_2.CurrentUser;
import com.example.csit228f2_2.TaskCardController;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private static DatabaseManager instance;
    private DatabaseManager() {};

    public static DatabaseManager getInstance(){
        if(instance == null){
            instance = new DatabaseManager();
        }

        return instance;
    }

    public boolean initializeDB() throws SQLException {
        Statement statement = null;
        try(Connection connection = MySQLConnection.getConnection("")){
            statement = connection.createStatement();
            String createDBQuery  = "CREATE DATABASE IF NOT EXISTS dbbitayo;";
            statement.executeUpdate(createDBQuery);

            connection.setCatalog("dbbitayo"); //toggle to the created db
            connection.setAutoCommit(false);
            statement = connection.createStatement();

            String createUserTableQuery = "CREATE TABLE IF NOT EXISTS user (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "password VARCHAR(100) NOT NULL);";

            statement.executeUpdate(createUserTableQuery);

            String createTaskTableQuery = "CREATE TABLE IF NOT EXISTS task (" +
                    "task_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "task_title VARCHAR(100), " +
                    "task_content VARCHAR(100), " +
                    "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE)";

            statement.executeUpdate(createTaskTableQuery);
            connection.commit();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            assert statement != null;
            statement.close();
        }

        return true;
    }

    public Status createTask(String title, String content, int userID){
        try(Connection c = MySQLConnection.getConnection("dbbitayo");
            PreparedStatement statement = c.prepareStatement("INSERT INTO task(user_id, task_title, task_content) VALUES(?, ?, ?)")){

            statement.setInt(1, userID);
            statement.setString(2, title);
            statement.setString(3, content);

            int res = statement.executeUpdate();

            if(res == 0){
                return Status.TASK_CREATION_FAILED;
            }

        }catch(SQLException e){
            e.printStackTrace();
            return Status.TASK_CREATION_FAILED;
        }

        return Status.TASK_CREATED_SUCCESSFULLY;
    }

    public Status createUser(String username, String email, String password){

        try(Connection c = MySQLConnection.getConnection("dbbitayo");
            PreparedStatement statement = c.prepareStatement("INSERT INTO user(username, email, password) VALUES(?, ?, ?)")){

            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);

            int res = statement.executeUpdate();
            if(res == 0){
                return Status.ACCOUNT_CREATION_FAILED;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return Status.ACCOUNT_CREATION_FAILED;
        }

        return Status.ACCOUNT_CREATED_SUCCESSFULLY;
    }

    public Status deleteUser(int userID){
        try(Connection c = MySQLConnection.getConnection("dbbitayo");
            PreparedStatement statement = c.prepareStatement("DELETE FROM user WHERE user_id = ?")){

            statement.setInt(1, userID);
            int res = statement.executeUpdate();

            if(res == 0){
                return Status.ACCOUNT_DELETION_FAILED;
            }

            //set it to default
            CurrentUser.userID = -1;
            CurrentUser.username = "";
            CurrentUser.email = "";

        }catch(SQLException e){
            e.printStackTrace();
            return Status.ACCOUNT_DELETION_FAILED;
        }

        return Status.ACCOUNT_DELETED_SUCCESSFULLY;
    }

    public Status updateUser(String field, String value, int userID){

        try(Connection c = MySQLConnection.getConnection("dbbitayo");
            PreparedStatement statement = c.prepareStatement("UPDATE user SET " + field + " = ? WHERE user_id = ?")){

            statement.setString(1, value);
            statement.setInt(2, userID);

            int res = statement.executeUpdate();

            if(res == 0){
                return Status.ACCOUNT_UPDATE_FAILED;
            }

            PreparedStatement getUserStatement = c.prepareStatement("SELECT * FROM user WHERE user_id = ?");
            getUserStatement.setInt(1, userID);

            ResultSet userData = getUserStatement.executeQuery();

            if(userData.next()){
                CurrentUser.username = userData.getString("username");
                CurrentUser.email = userData.getString("email");
                CurrentUser.userID = userData.getInt("user_id");
            }

        }catch (SQLException e){
            e.printStackTrace();
            return Status.ACCOUNT_UPDATE_FAILED;
        }

        return Status.ACCOUNT_UPDATED_SUCCESSFULLY;
    }

    public Status validate(String username, String password){
        try(Connection c = MySQLConnection.getConnection("dbbitayo");
            PreparedStatement statement = c.prepareStatement("SELECT * FROM user where username = ?")){

            statement.setString(1, username);
            ResultSet res = statement.executeQuery();

            if(!res.next()) return Status.USERNAME_NOT_FOUND;

            String passwordFromDB = res.getString("password");
            if(!passwordFromDB.equals(password)) return Status.INCORRECT_PASSWORD;

            //setting the current user
            CurrentUser.userID = res.getInt("user_id");
            CurrentUser.username = res.getString("username");
            CurrentUser.email = res.getString("email");
        }catch(SQLException e){
            e.printStackTrace();
        }

        return Status.LOGIN_SUCCESS;
    }

    public List<Map<String, String>> getTasks(){
        try(Connection c = MySQLConnection.getConnection("dbbitayo");
            PreparedStatement statement = c.prepareStatement("SELECT * FROM task WHERE user_id = ?")){
            statement.setInt(1, CurrentUser.userID);

            ResultSet res = statement.executeQuery();

            List<Map<String, String>> tasks = new ArrayList<>();

            while(res.next()){
                Map<String, String> temp = new HashMap<>();
                temp.put("task_id", res.getString("task_id"));
                temp.put("user_id", res.getString("user_id"));
                temp.put("task_title", res.getString("task_title"));
                temp.put("task_content", res.getString("task_content"));
                tasks.add(temp);
            }

            return tasks;


        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public Status deleteTask(int taskID){
        try(Connection c = MySQLConnection.getConnection("dbbitayo");
            PreparedStatement statement = c.prepareStatement("DELETE FROM task WHERE task_id = ?")){

            statement.setInt(1, taskID);
            int res = statement.executeUpdate();

            if(res == 0){
                return Status.TASK_DELETION_FAILED;
            }

        }catch(SQLException e){
            e.printStackTrace();
            return Status.TASK_DELETION_FAILED;
        }

        return Status.TASK_DELETED_SUCCESSFULLY;
    }

    public Status editTask(String title, String content, int taskID, TaskCardController controller){
        String editQuery = "UPDATE task SET ";
        if (!title.isEmpty() && !content.isEmpty()) {
            editQuery += "task_title = ?, task_content = ? ";
        } else if (!title.isEmpty()) {
            editQuery += "task_title = ? ";
        } else {
            editQuery += "task_content = ? ";
        }

        editQuery += "WHERE task_id = ?";

        try (Connection c = MySQLConnection.getConnection("dbbitayo")) {
            PreparedStatement statement = c.prepareStatement(editQuery);
            int parameterIndex = 1;
            if (!title.isEmpty()) {
                statement.setString(parameterIndex++, title);
            }
            if (!content.isEmpty()) {
                statement.setString(parameterIndex++, content);
            }
            statement.setInt(parameterIndex, taskID);

            int res = statement.executeUpdate();

            if (res == 0) {
                return Status.TASK_EDIT_FAILED;
            }

            String getUpdatedTaskQuery = "SELECT * FROM task WHERE task_id = ?";
            statement = c.prepareStatement(getUpdatedTaskQuery);
            statement.setInt(1, taskID);

            ResultSet updatedTask = statement.executeQuery();

            if(updatedTask.next()){
                String newTitle = updatedTask.getString("task_title");
                String newContent = updatedTask.getString("task_content");
                controller.setTaskCardValues(newTitle, newContent, taskID);
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return Status.TASK_EDIT_FAILED;
        }


        return Status.TASK_EDITED_SUCCESSFULLY;
    }
}