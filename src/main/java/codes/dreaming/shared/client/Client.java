package codes.dreaming.shared.client;

import codes.dreaming.shared.client.packets.ClientDisconnectPacket;
import codes.dreaming.shared.client.packets.ClientGuessPacket;
import codes.dreaming.shared.client.packets.ClientLetterPacket;
import codes.dreaming.shared.client.packets.ClientPacket;
import codes.dreaming.shared.server.packets.ServerLetterPacket;
import codes.dreaming.shared.server.packets.ServerPacket;
import codes.dreaming.shared.server.packets.ServerStartPacket;
import codes.dreaming.shared.server.packets.ServerWinPacket;
import com.googlecode.lanterna.gui2.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements Runnable {
    final private MultiWindowTextGUI multiWindowTextGUI;

    private Socket socket;

    public ObjectInputStream input;
    public ObjectOutputStream output;

    public Client(MultiWindowTextGUI gui, String host, Integer port) {
        this.multiWindowTextGUI = gui;
        // Setup streams
        try {
            socket = new Socket(host, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        BasicWindow window = new BasicWindow("Client");

        Panel contentPanel = new Panel(new GridLayout(2));

        Label stateLabel = new Label("Waiting for server to start...");

        // Add a button for disconnecting
        Button disconnectButton = new Button("Disconnect", () -> {
            try {
                output.writeObject(new ClientDisconnectPacket());
                window.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Label inputLabel = new Label("Enter a letter or guess:");
        TextBox inputBox = new TextBox();
        Button send = new Button("Send", () -> {
            String text = inputBox.getText();
            if(text.length() == 1) {
                try {
                    output.writeObject(new ClientLetterPacket(text.charAt(0)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else {
                try {
                    output.writeObject(new ClientGuessPacket(text));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        contentPanel.addComponent(inputLabel);
        contentPanel.addComponent(inputBox);
        contentPanel.addComponent(send);

        contentPanel.addComponent(stateLabel);
        contentPanel.addComponent(disconnectButton);

        window.setComponent(contentPanel);

        multiWindowTextGUI.addWindow(window);
        while (true) {
            try {
                ServerPacket packet = (ServerPacket) input.readObject();

                if(packet instanceof ServerStartPacket serverStartPacket) {
                    stateLabel.setText("_".repeat(serverStartPacket.getWordLength()));
                    System.out.println("Server started");
                }else if (packet instanceof ServerLetterPacket letterPacket) {
                    StringBuilder builder = new StringBuilder(stateLabel.getText());
                    System.out.println("Got letter " + letterPacket.getLetter() + " at " + letterPacket.getIndex());
                    for (Integer index : letterPacket.getIndex()) {
                        builder.setCharAt(index, letterPacket.getLetter());
                        System.out.println("Setting index " + index + " to " + letterPacket.getLetter());
                    }
                    stateLabel.setText(builder.toString());
                }else if (packet instanceof ServerWinPacket winPacket) {
                    if(winPacket.getYouWin()) {
                        stateLabel.setText("You won!");
                    }else {
                        stateLabel.setText("You lost!");
                    }
                }


            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
