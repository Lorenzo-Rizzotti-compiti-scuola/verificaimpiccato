package codes.dreaming.shared.client;

import codes.dreaming.shared.client.packets.*;
import codes.dreaming.shared.server.packets.*;
import com.googlecode.lanterna.gui2.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements Runnable {
    private final MultiWindowTextGUI multiWindowTextGUI;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Label stateLabel;

    public Client(MultiWindowTextGUI gui, String host, Integer port) {
        this.multiWindowTextGUI = gui;
        setupStreams(host, port);
    }

    private void setupStreams(String host, Integer port) {
        try {
            Socket socket = new Socket(host, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        BasicWindow window = createWindow();
        multiWindowTextGUI.addWindow(window);
        mainLoop(window);
    }

    private BasicWindow createWindow() {
        BasicWindow window = new BasicWindow("Client");
        Panel contentPanel = createPanel(window);
        window.setComponent(contentPanel);
        return window;
    }

    private Panel createPanel(BasicWindow window) {
        Panel contentPanel = new Panel(new GridLayout(2));
        this.stateLabel = new Label("Waiting for server to start...");

        Button disconnectButton = createDisconnectButton(window);
        Label inputLabel = new Label("Enter a letter or guess:");
        TextBox inputBox = new TextBox();
        Button sendButton = createSendButton(inputBox);

        contentPanel.addComponent(inputLabel);
        contentPanel.addComponent(inputBox);
        contentPanel.addComponent(sendButton);
        contentPanel.addComponent(stateLabel);
        contentPanel.addComponent(disconnectButton);

        return contentPanel;
    }

    private Button createDisconnectButton(BasicWindow window) {
        return new Button("Disconnect", () -> {
            try {
                output.writeObject(new ClientDisconnectPacket());
                window.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Button createSendButton(TextBox inputBox) {
        return new Button("Send", () -> {
            String text = inputBox.getText();
            if (text.length() == 1) {
                sendPacket(new ClientLetterPacket(text.charAt(0)));
            } else {
                sendPacket(new ClientGuessPacket(text));
            }
        });
    }

    private void sendPacket(ClientPacket packet) {
        try {
            output.writeObject(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void mainLoop(BasicWindow window) {
        while (true) {
            try {
                ServerPacket packet = (ServerPacket) input.readObject();
                handleServerPacket(packet);
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleServerPacket(ServerPacket packet) {
        if (packet instanceof ServerStartPacket serverStartPacket) {
            handleServerStartPacket(serverStartPacket);
        } else if (packet instanceof ServerLetterPacket letterPacket) {
            handleServerLetterPacket(letterPacket);
        } else if (packet instanceof ServerWinPacket winPacket) {
            handleServerWinPacket(winPacket);
        } else if (packet instanceof ServerWrongGuessPacket wrongGuessPacket) {
            handleWrongGuessPacket(wrongGuessPacket);
        }
    }

    private void handleServerStartPacket(ServerStartPacket packet) {
        this.stateLabel.setText("_".repeat(packet.getWordLength()));
    }

    private void handleServerLetterPacket(ServerLetterPacket packet) {
        StringBuilder builder = new StringBuilder(this.stateLabel.getText());
        for (Integer index : packet.getIndex()) {
            builder.setCharAt(index, packet.getLetter());
        }
        this.stateLabel.setText(builder.toString());

        // This automatically sends a guess if the word is guessed, technically this should be done on the server side but this is a quick fix
        if (builder.indexOf("_") == -1) {
            sendPacket(new ClientGuessPacket(builder.toString()));
        }
    }

    private void handleServerWinPacket(ServerWinPacket packet) {
        if (packet.getYouWin()) {
            stateLabel.setText("You won!");
        } else {
            stateLabel.setText("You lost!");
        }
    }

    private void handleWrongGuessPacket(ServerWrongGuessPacket packet) {
        stateLabel.setText("Wrong guess!");
    }
}
