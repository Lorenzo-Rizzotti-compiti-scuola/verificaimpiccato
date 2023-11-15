package codes.dreaming.shared.server;

import codes.dreaming.shared.client.packets.ClientPacket;
import codes.dreaming.shared.client.packets.ClientDisconnectPacket;
import codes.dreaming.shared.client.packets.ClientGuessPacket;
import codes.dreaming.shared.client.packets.ClientLetterPacket;
import codes.dreaming.shared.server.packets.ServerLetterPacket;
import codes.dreaming.shared.server.packets.ServerWinPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    final Socket socket;
    final Server server;

    public ObjectInputStream input;
    public ObjectOutputStream output;

    public ConnectionHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;

        // Setup streams
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        System.out.println("New connection from " + socket.getInetAddress().getHostAddress());
        // Accept packets from the client
        while (true) {
            try {
                ClientPacket packet = (ClientPacket) input.readObject();
                if (packet instanceof ClientDisconnectPacket) {
                    server.connections.remove(this);
                    return;
                }

                if (!server.isGameStarted()) {
                    continue;
                }

                if (packet instanceof ClientLetterPacket letterPacket) {
                    output.writeObject(new ServerLetterPacket(letterPacket.getLetter(), server.checkLetter(letterPacket.getLetter())));
                    continue;
                }

                if (packet instanceof ClientGuessPacket guessPacket) {
                    if(server.checkWin(this, guessPacket.getGuess())) {
                        server.connections.stream().forEach(connectionHandler -> {
                            try {
                                connectionHandler.output.writeObject(new ServerWinPacket(true));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    continue;
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
