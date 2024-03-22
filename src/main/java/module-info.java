module com.javacoders.chatroomclient {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.javacoders.chatroomclient to javafx.fxml;
    exports com.javacoders.chatroomclient;
}