import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client extends Application {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private TextArea chatArea;
    private TextField inputField;

    @Override
    public void start(Stage primaryStage) {
        chatArea = new TextArea();
        chatArea.setEditable(false);

        inputField = new TextField();
        Button sendButton = new Button("Send");

        sendButton.setOnAction(e -> sendMessage());

        VBox root = new VBox(10, chatArea, inputField, sendButton);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        connectToServer();
        startReaderThread();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 1234); // connect to server
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            chatArea.appendText("Connected to server\n");
        } catch (IOException e) {
            chatArea.appendText("Failed to connect to server: " + e.getMessage() + "\n");
        }
    }

    private void sendMessage() {
        String msg = inputField.getText();
        if (msg != null && !msg.isEmpty() && writer != null) {
            writer.println(msg);
            inputField.clear();
        } else {
            chatArea.appendText("⚠ Cannot send message. Not connected!\n");
        }
    }

    private void startReaderThread() {
        Thread thread = new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String finalLine = line;
                    javafx.application.Platform.runLater(() -> chatArea.appendText(finalLine + "\n"));
                }
            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> chatArea.appendText("Connection closed.\n"));
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
