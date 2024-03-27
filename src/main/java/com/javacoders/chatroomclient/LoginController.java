package com.javacoders.chatroomclient;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;

public class LoginController {

    @FXML
    private TextField tf_username;

    @FXML
    private Button button_submit;

    @FXML
    private Label label;
    @FXML
    private HBox hbox;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    // Create a Timeline animation to return border color to original after a delay
    private Timeline toBlack;
    private Timeline toRed;
    private Timeline toBlackBorder;


    private UsernameEnteredListener listener;

    // Define a functional interface for listener
    public interface UsernameEnteredListener {
        void onUsernameEntered(String username);
    }

    public Socket getSocket(){
        return socket;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    // Set the listener
    public void setOnUsernameEntered(UsernameEnteredListener listener) {
        this.listener = listener;
    }

    // Called when the submit button is clicked
    @FXML
    private void submitUsername(){
        try {
            String username = tf_username.getText().trim();
            if (!username.isEmpty() && listener != null) {
                String response;
                if(socket == null){
                    socket = new Socket("localhost", 4321);
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
                bufferedWriter.write(username);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                response = bufferedReader.readLine();
                if(response.equals("error")){
                    if(toBlack == null) {
                        toBlack = new Timeline(
                                new KeyFrame(Duration.seconds(1), new KeyValue(label.textFillProperty(),
                                        javafx.scene.paint.Color.BLACK)));
                        toBlackBorder = new Timeline(
                                new KeyFrame(Duration.seconds(0.1), new KeyValue(tf_username.styleProperty(),
                                        "-fx-border-color: rgba(252, 181, 87, 0.47)"))
                        );
                    }
                    hbox.setLayoutX(30);
                    tf_username.setVisible(true);
                    button_submit.setText("ثبت");
                    label.setTextFill(Paint.valueOf("RED"));
                    tf_username.setStyle("-fx-border-color: RED");
                    toBlack.play();
                    toBlackBorder.play();
                    label.setText("این نام کاربری قبلا ثبت شده است. آن را تغییر دهید:");
                } else {
                    // Notify the listener with the entered username
                    listener.onUsernameEntered(username);
                }
            }
        } catch (IOException e) {
            if(toRed == null)
                toRed = new Timeline(
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(label.textFillProperty(), javafx.scene.paint.Color.RED)));
            label.setText("عدم اتصال به سرور");
            hbox.setLayoutX(40);
            label.setTextFill(Paint.valueOf("BLACK"));
            toRed.play();
            button_submit.setText("تلاش مجدد");
            tf_username.setVisible(false);

        }
    }
}
