package com.example.csit228f2_2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bitayodb";
    private static final String USERNAME = "hello";
    private static final String PASSWORD = "bahalana";
    public static Connection getConnection(){
        Connection c = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to the Database");

        }catch(ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }

        return c;
    }

    public static void main(String[] args) {
        getConnection();
    }
}
