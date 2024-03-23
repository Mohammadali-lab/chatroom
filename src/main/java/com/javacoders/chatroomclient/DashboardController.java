package com.javacoders.chatroomclient;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class DashboardController {
    @FXML
    private Button button_send;
    @FXML
    private TextField tf_message;
    @FXML
    private VBox vbox_messages;
    @FXML
    private VBox vbox_users;
    @FXML
    private ScrollPane sp_main;
    @FXML
    private BorderPane root;
    @FXML
    private ListView<HBox> userList;
    private Client client;
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String currentSessionName = "گروه";
    private Map<String, VBox> chatAreas = new HashMap<>();


    public void setSocket(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        this.socket = socket;
        this.bufferedWriter = bufferedWriter;
        this.bufferedReader = bufferedReader;
    }


    public void initialRoom(String username) {


        // Set a custom cell factory to handle null items
        userList.setCellFactory(param -> new ListCell<HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().add("null-cell");
                } else {
                    setGraphic(item);
                    getStyleClass().remove("null-cell");
                }
            }
        });
        vbox_messages.setPrefHeight(0.85 * root.getHeight()); // 50% of parent's height
        vbox_messages.setPrefWidth(0.7 * root.getWidth()); // 70% of parent's width

        vbox_users.setPrefHeight(0.85 * root.getHeight()); // 50% of parent's height
        vbox_users.setPrefWidth(0.3 * root.getWidth()); // 70% of parent's width

        client = new Client(socket, bufferedReader, bufferedWriter);
        System.out.println("Connected to server.");

        HBox hBox = new HBox();
        Text groupName = new Text("گروه");
        Text groupMessageCount = new Text();
        TextFlow textFlow = new TextFlow(groupMessageCount);
        hBox.getChildren().add(textFlow);
        hBox.getChildren().add(groupName);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        userList.getItems().add(hBox);

        chatAreas.put("گروه", new VBox());


        // Add event handler for Enter key press in TextField
        tf_message.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                button_send.fire(); // Fire the button action
            }
        });

        // Listen for changes to the parent container's size
        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            vbox_messages.setPrefWidth(0.7 * newVal.doubleValue()); // Adjust width based on parent's width
            vbox_users.setPrefWidth(0.3 * newVal.doubleValue()); // Adjust width based on parent's width
        });

        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            vbox_messages.setPrefHeight(0.85 * newVal.doubleValue()); // Adjust height based on parent's height
            vbox_users.setPrefHeight(0.85 * newVal.doubleValue()); // Adjust height based on parent's height
        });

        vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                sp_main.setVvalue((Double) newValue);
            }
        });


        client.receiveMessageFromServer(this);

        button_send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String messageToSend = tf_message.getText();
                if (!messageToSend.isEmpty()) {
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5, 5, 5, 10));

                    Text text = new Text(messageToSend);
                    TextFlow textFlow = new TextFlow(text);

                    textFlow.setStyle("-fx-color: rgb(239,242,255);" +
                            "-fx-background-color: rgb(15,125,242);" +
                            "-fx-background-radius: 20px");

                    textFlow.setPadding(new Insets(5, 10, 5, 10));
                    text.setFill(Color.color(0.934, 0.945, 0.996));
                    text.setFont(Font.font("IRANSansWeb",14));

                    hBox.getChildren().add(textFlow);
                    vbox_messages.getChildren().add(hBox);

                    messageToSend = currentSessionName+":"+messageToSend;
                    client.sendMessageToServer(messageToSend);
                    tf_message.clear();
                }
            }
        });
    }

    @FXML
    private void userSelected(MouseEvent event) {

        HBox selectedUser = userList.getSelectionModel().getSelectedItem();
        if(selectedUser != null) {
            Text text = (Text) selectedUser.getChildren().get(1);
            TextFlow textFlow;
            String username = text.getText();

            if(!currentSessionName.equals(username)){
                VBox previousVbox = chatAreas.get(currentSessionName);
                previousVbox.getChildren().setAll(vbox_messages.getChildren());
                VBox vBox = chatAreas.get(username);
                if(vBox != null)
                    vbox_messages.getChildren().setAll(vBox.getChildren());
                textFlow = (TextFlow) selectedUser.getChildren().get(0);
                textFlow.setStyle("-fx-background-color: null");
                text = (Text) textFlow.getChildren().get(0);
                text.setText("");
                currentSessionName = username;
            }
        }
    }

    public void setUsers(String[] members){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for(String member: members){
                    chatAreas.put(member, new VBox());
                    HBox hBox = new HBox();
                    Text username = new Text(member);
                    Text messageCount = new Text();
                    TextFlow textFlow = new TextFlow(messageCount);
                    hBox.getChildren().add(textFlow);
                    hBox.getChildren().add(username);
                    hBox.setSpacing(10);
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    userList.getItems().add(hBox);
                }
            }
        });
    }

    public void addLabel(String msgFromServer) {

        boolean isServer = false;
        String[] userAndMsg = msgFromServer.split(":");
        VBox vBox;
        if(userAndMsg[0].equals("سرور")){
            isServer = true;
            userAndMsg[0] = "گروه";
        }

        vBox = chatAreas.get(userAndMsg[0]);

        if(!currentSessionName.equals(userAndMsg[0])){
            Text msgCount = new Text();
            TextFlow textFlow;
            for(HBox hBox : userList.getItems()){
                msgCount = (Text) hBox.getChildren().get(1);
                if(msgCount.getText().equals(userAndMsg[0])){
                    textFlow = (TextFlow) hBox.getChildren().get(0);
                    textFlow.setStyle("-fx-background-color: rgb(250,252,250);" +
                            "-fx-background-radius: 20px");
                    textFlow.setPadding(new Insets(0,10,0,10));
                    msgCount = (Text) textFlow.getChildren().get(0);
                    break;
                }
            }
            if(msgCount.getText().equals(""))
                msgCount.setText("۱");
            else {
                int count = convertToEnglishNumber(msgCount.getText());
                count++;
                msgCount.setText(convertToPersianNumber(count));
            }
        }

        HBox hBox = new HBox();


        if(isServer){
            hBox.setAlignment(Pos.CENTER);
            hBox.setPadding(new Insets(5, 5, 5, 10));

            Text text = new Text(userAndMsg[1]);
            text.setFont(Font.font("IRANSansWeb",14));
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
                    "-fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5, 10, 5, 10));
            hBox.getChildren().add(textFlow);
        }
        else {
            if(userAndMsg[0].equals("گروه"))
                msgFromServer = userAndMsg[1] + ": " + userAndMsg[2];
            else
                msgFromServer = userAndMsg[1];

            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5, 5, 5, 10));

            Text text = new Text(msgFromServer);
            text.setFont(Font.font("IRANSansWeb",14));
            text.setFill(Color.color(0.934, 0.945, 0.996));
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: rgb(201,78,252);" +
                    "-fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5, 10, 5, 10));
            hBox.getChildren().add(textFlow);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(currentSessionName.equals(userAndMsg[0]))
                    vbox_messages.getChildren().add(hBox);
                else
                    vBox.getChildren().add(hBox);
            }
        });
    }

    private String convertToPersianNumber(int number) {
        String englishDigits = "0123456789";
        String persianDigits = "۰۱۲۳۴۵۶۷۸۹";

        String persianNumber = String.valueOf(number);
        for (int i = 0; i < englishDigits.length(); i++) {
            persianNumber = persianNumber.replace(englishDigits.charAt(i), persianDigits.charAt(i));
        }

        return persianNumber;
    }

    private int convertToEnglishNumber(String number) {
        String englishDigits = "0123456789";
        String persianDigits = "۰۱۲۳۴۵۶۷۸۹";

        for (int i = 0; i < englishDigits.length(); i++) {
            number = number.replace(persianDigits.charAt(i), englishDigits.charAt(i));
        }

        return Integer.parseInt(number);
    }
}