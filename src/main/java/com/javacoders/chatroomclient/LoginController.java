package com.javacoders.chatroomclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField tf_username;

    @FXML
    private Button button_submit;

    private UsernameEnteredListener listener;

    // Define a functional interface for listener
    public interface UsernameEnteredListener {
        void onUsernameEntered(String username);
    }

    // Set the listener
    public void setOnUsernameEntered(UsernameEnteredListener listener) {
        this.listener = listener;
    }

    // Called when the submit button is clicked
    @FXML
    private void submitUsername() {
        String username = tf_username.getText().trim();
        if (!username.isEmpty() && listener != null) {
            // Notify the listener with the entered username
            listener.onUsernameEntered(username);
        }
    }
}
