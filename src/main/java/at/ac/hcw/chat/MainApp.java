package at.ac.hcw.chat;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        //scene 1 chat view

        TextField ipField = new TextField();
        ipField.setPromptText("IP Address");

        TextField portField = new TextField();
        portField.setPromptText("Port");

        Button connectButton = new Button("Connect");

        HBox bottomConnectBar = new HBox(10, ipField, portField, connectButton);
        bottomConnectBar.setPadding(new Insets(10));
        bottomConnectBar.setAlignment(Pos.CENTER);

        BorderPane connectRoot = new BorderPane();
        connectRoot.setBottom(bottomConnectBar);

        Scene connectScene = new Scene(connectRoot, 600, 400);

        //scene 2 chat view

        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        TextField messageField = new TextField();
        messageField.setPromptText("Type a message...");

        Button sendButton = new Button("Send");

        HBox chatInputBar = new HBox(10, messageField, sendButton);
        chatInputBar.setPadding(new Insets(10));
        chatInputBar.setAlignment(Pos.CENTER);
        HBox.setHgrow(messageField, Priority.ALWAYS);

        BorderPane chatRoot = new BorderPane();
        chatRoot.setCenter(chatArea);
        chatRoot.setBottom(chatInputBar);

        Scene chatScene = new Scene(chatRoot, 600, 400);

        //actions

        connectButton.setOnAction(e -> {
            String ip = ipField.getText();
            String port = portField.getText();

            // Later: start ChatClient with ip + port
            System.out.println("Connecting to " + ip + ":" + port);

            stage.setScene(chatScene);
            stage.setTitle("Chat Room");
        });

        sendButton.setOnAction(e -> {
            String msg = messageField.getText();
            if (!msg.isEmpty()) {
                chatArea.appendText("Me: " + msg + "\n");
                messageField.clear();

                // Later: send msg via socket
            }
        });

        messageField.setOnAction(e -> sendButton.fire());


//stage set up
        stage.setTitle("Chat Tool");
        stage.setScene(connectScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
