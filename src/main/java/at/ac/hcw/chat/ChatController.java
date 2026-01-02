package at.ac.hcw.chat;

//contains UI logic
//useful for creating link between gui and logic to be able to send messages and modify the chat bubbles(make visible)
//Jannat Waheed


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class ChatController {

    @FXML
    private TextField messageField;

    @FXML
    private VBox chatBox;

    // Wird vom Send-Button aufgerufen
    @FXML
    private void onSendMessage() {
        String message = messageField.getText();

        // Leere Nachrichten verhindern
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        // Nachricht darstellen
        Label bubble = new Label(message);
        bubble.setStyle(
                "-fx-background-color: #9ed0f6;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 8;"
        );

        // In den Chat einfügen
        chatBox.getChildren().add(bubble);

        messageField.clear();
    }
}
