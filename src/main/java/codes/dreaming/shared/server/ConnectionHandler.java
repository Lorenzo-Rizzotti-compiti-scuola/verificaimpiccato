package codes.dreaming.shared.server;

import codes.dreaming.shared.client.packets.ClientPacket;
import codes.dreaming.shared.client.packets.ClientDisconnectPacket;
import codes.dreaming.shared.client.packets.ClientGuessPacket;
import codes.dreaming.shared.client.packets.ClientLetterPacket;
import codes.dreaming.shared.server.packets.ServerLetterPacket;
import codes.dreaming.shared.server.packets.ServerWinPacket;
import codes.dreaming.shared.server.packets.ServerWrongGuessPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    public ConnectionHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            System.out.println("New connection from " + socket.getInetAddress().getHostAddress());
            acceptPackets();
        } finally {
            try {
                input.close();
                output.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to close resources.", e);
            }
        }
    }

    private void acceptPackets() {
        while (true) {
            try {
                ClientPacket packet = (ClientPacket) input.readObject();

                if (packet instanceof ClientDisconnectPacket) {
                    server.removeConnection(this);
                    return;
                }

                if (!server.isGameStarted()) {
                    continue;
                }

                handleClientLetterPacket(packet);
                handleClientGuessPacket(packet);
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleClientLetterPacket(ClientPacket packet) throws IOException {
        if (packet instanceof ClientLetterPacket letterPacket) {
            output.writeObject(new ServerLetterPacket(letterPacket.getLetter(), server.checkLetter(letterPacket.getLetter())));
        }
    }

    private void handleClientGuessPacket(ClientPacket packet) throws IOException {
        if (packet instanceof ClientGuessPacket guessPacket) {
            if (server.checkWin(this, guessPacket.getGuess())) {
                this.output.writeObject(new ServerWinPacket(true));
            }else{
                this.output.writeObject(new ServerWrongGuessPacket());
            }
        }
    }

    public void sendPacket(Object packet) {
        try {
            output.writeObject(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
