package com.javacoders.chatroomclient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        String fontPath = "IRANSansWeb.ttf";
        Font.loadFont(getClass().getResourceAsStream(fontPath),14);
        // Load the username input FXML file
        FXMLLoader usernameLoader = new FXMLLoader(getClass().getResource("username-input.fxml"));
        Parent usernameRoot = usernameLoader.load();
        UsernameInputController usernameController = usernameLoader.getController();

        // Show the username input page
        Scene usernameScene = new Scene(usernameRoot);
        usernameScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(usernameScene);
        primaryStage.show();

        // After obtaining the username, load the main chatroom FXML file
        usernameController.setOnUsernameEntered(username -> {
            try {
                FXMLLoader chatroomLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                Parent chatroomRoot = chatroomLoader.load();
                HelloController chatroomController = chatroomLoader.getController();

                // Pass the username to the chatroom controller
                chatroomController.initialRoom(username);

                // Show the chatroom
                Scene chatroomScene = new Scene(chatroomRoot);
                chatroomScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

                primaryStage.setOnCloseRequest(event -> {
                    // Perform cleanup tasks here before exiting the application
                    System.exit(0);
                    // You can add any additional cleanup or shutdown tasks here
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