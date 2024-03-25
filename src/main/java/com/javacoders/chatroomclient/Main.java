package com.javacoders.chatroomclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream( "logo.png" )));

        String fontPath = "IRANSansWeb.ttf";
        Font.loadFont(getClass().getResourceAsStream(fontPath),14);
        // Load the username input FXML file
        FXMLLoader usernameLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent usernameRoot = usernameLoader.load();
        LoginController usernameController = usernameLoader.getController();
        // Show the username input page
        Scene usernameScene = new Scene(usernameRoot);
        usernameScene.getStylesheets().add(getClass().getResource("loginStyle.css").toExternalForm());
        primaryStage.setScene(usernameScene);
        primaryStage.show();

        // After obtaining the username, load the main chatroom FXML file
        usernameController.setOnUsernameEntered(username -> {
            try {
                Socket socket = usernameController.getSocket();
                BufferedWriter bufferedWriter = usernameController.getBufferedWriter();
                BufferedReader bufferedReader = usernameController.getBufferedReader();
                FXMLLoader chatroomLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                Parent chatroomRoot = chatroomLoader.load();
                DashboardController chatroomController = chatroomLoader.getController();

                chatroomController.setSocket(socket, bufferedReader, bufferedWriter);
                // Pass the username to the chatroom controller
                chatroomController.initialRoom(username);

                // Show the chatroom
                Scene chatroomScene = new Scene(chatroomRoot);
                chatroomScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

                primaryStage.setOnCloseRequest(event -> {
                    System.exit(0);
                });

                primaryStage.setScene(chatroomScene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}